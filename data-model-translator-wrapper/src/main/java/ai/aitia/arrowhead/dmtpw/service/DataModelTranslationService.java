/*******************************************************************************
 *
 * Copyright (c) 2025 AITIA
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 *
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  	AITIA
 *
 *******************************************************************************/
package ai.aitia.arrowhead.dmtpw.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.naming.ConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.aitia.arrowhead.dmtpw.DataModelTranslatorWrapperSystemInfo;
import ai.aitia.arrowhead.dmtpw.service.model.DataModelTranslationTask;
import ai.aitia.arrowhead.dmtpw.service.validation.DataModelTranslationServiceValidation;
import eu.arrowhead.common.exception.InternalServerError;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.dto.DataModelTranslationInitRequestDTO;
import eu.arrowhead.dto.DataModelTranslationResultResponseDTO;
import eu.arrowhead.dto.enums.DataModelTranslationTaskStatus;
import jakarta.annotation.PostConstruct;

@Service
public class DataModelTranslationService {

	//=================================================================================================
	// members
	
	private volatile boolean RUNNING = true;
	
	@Autowired
	private DataModelTranslatorWrapperSystemInfo sysInfo;

	@Autowired
	private DataModelTranslationServiceValidation validator;

	private final BlockingQueue<DataModelTranslationTask> jobQueue = new LinkedBlockingQueue<>();
	private final Map<UUID, DataModelTranslationTask> jobCache = new ConcurrentHashMap<>();
	private final Map<UUID, String> fileNameCache = new ConcurrentHashMap<>(); 

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
    public UUID initTranslation(final DataModelTranslationInitRequestDTO dto, final String origin) {
    	logger.debug("initTranslation started...");
    	
    	final DataModelTranslationInitRequestDTO normalized = validator.validateAndNormalizeDataModelTranslationInitRequestDTO(dto, origin);

        final DataModelTranslationTask job = new DataModelTranslationTask(sysInfo.getModelIdsWithResultMimeTpyes().get(List.of(normalized.inputModelId(), normalized.outputModelId())), normalized.payload());
        jobCache.put(job.getUuid(), job);
        jobQueue.add(job);
        return job.getUuid();
    }

    //-------------------------------------------------------------------------------------------------
    public DataModelTranslationResultResponseDTO getTranslationResult(final String jobIdString, final String origin) {
    	logger.debug("getTranslationResult started...");

    	final UUID jobId = validator.validateAndNormalizeJobIdString(jobIdString, origin);
        final DataModelTranslationTask job = jobCache.get(jobId);
        
        if (job == null) {
        	throw new InvalidParameterException("Invalid UUID", origin);
        }
        
        String result = null;
        
        if (job.getStatus() == DataModelTranslationTaskStatus.ERROR) {
        	result = job.getErrorMessage();
        } else {
        	result = readTranslatedFile(fileNameCache.get(jobId));
        }

        return new DataModelTranslationResultResponseDTO(job.getStatus(), result, job.getResultMimeType());
    }

	//=================================================================================================
	// assistant methods

    //-------------------------------------------------------------------------------------------------
    private void doTranslationJob(final DataModelTranslationTask job) {
    	// start the job
    	job.setStatus(DataModelTranslationTaskStatus.IN_PROGRESS);

    	// decode the bytes
    	final byte[] bytes = Base64.getDecoder().decode(job.getPayload().getBytes(StandardCharsets.UTF_8));

    	final String fileName = job.getUuid().toString() + ".xml";
    	try {
    		// save the file
    		Files.write(Paths.get(sysInfo.getInputFolder(), fileName), bytes);

    		// run the script
    		runTranslationScript(job.getUuid().toString());
    	} catch (final Exception ex) {
    		job.setStatusToError(ex.getMessage());
    		logger.error(ex.getMessage());
    		throw new InternalServerError("An error occured while running the translation script: " + ex.getMessage());
    	}

    	fileNameCache.put(job.getUuid(), fileName);

    	// the job is done
    	job.setStatus(DataModelTranslationTaskStatus.DONE);
    }

  //-------------------------------------------------------------------------------------------------
    private void runTranslationScript(final String name) throws ConfigurationException, InterruptedException, IOException {
    	
    	final ProcessBuilder translationScriptProcessBuilder = new ProcessBuilder(sysInfo.getPythonLauncherPath(), sysInfo.getTranslationScriptLocation(), name);
        translationScriptProcessBuilder.inheritIO();
        translationScriptProcessBuilder.directory(new File(sysInfo.getTranslationScriptLocation()).getParentFile());

        final Process translationScriptProcess = translationScriptProcessBuilder.start();
        
        final StringBuilder stdOutMessages = new StringBuilder();
        final StringBuilder stdErrMessages = new StringBuilder();
        
        try (
        	final BufferedReader stdErr = new BufferedReader(new InputStreamReader(translationScriptProcess.getErrorStream()));
            final BufferedReader stdOut = new BufferedReader(new InputStreamReader(translationScriptProcess.getInputStream()));
        ) {
            String errLine;
            while ((errLine = stdErr.readLine()) != null) {
            	stdErrMessages.append(errLine);
            }
            
            String outLine;
            while ((outLine = stdOut.readLine()) != null) {
            	stdOutMessages.append(outLine);
            }
        }
        
        logger.info("Python script standard output: " + stdOutMessages);
        
        if (!stdErrMessages.isEmpty()) {
        	throw new InvalidParameterException("Translation python script has thrown an error message: " + stdErrMessages);
        }
        
        final int exitCode = translationScriptProcess.waitFor();
        if (exitCode != 0) {
        	final String errorMessage = "Translation python script exited with code: " + exitCode;
        	logger.error(errorMessage);
        	throw new InternalServerError(errorMessage);
        }
    }

    //-------------------------------------------------------------------------------------------------
    private String readTranslatedFile(final String filename) {

    	if (filename == null) {
    		return null;
    	}

        try {
            final byte[] fileBytes = Files.readAllBytes(Paths.get(sysInfo.getOutputFolder(), filename));
            return new String(Base64.getEncoder().encode(fileBytes), StandardCharsets.UTF_8);
        } catch (final IOException ex) {
        	logger.error(ex.getMessage());
            return null;
        }
    }

	//-------------------------------------------------------------------------------------------------
    private void start() {
        final Thread worker = new Thread(() -> {
            while (RUNNING) {
                try {
                    final DataModelTranslationTask job = jobQueue.take();
                    doTranslationJob(job);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        worker.start();
    }

    //-------------------------------------------------------------------------------------------------
    @PostConstruct
    private void init() {
        start();
    }
    
	//=================================================================================================
	// boilerplate

    //-------------------------------------------------------------------------------------------------
    public void stopRunning() {
    	this.RUNNING = false;
    }
}
