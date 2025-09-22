package ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.naming.ConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.WrapperForPythonDatamodelTranslatorConstants;
import ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.service.model.DataModelTranslationTask;
import ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.service.validation.DataModelTranslationServiceValidation;
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

	@Value(WrapperForPythonDatamodelTranslatorConstants.$TRANSLATION_SCRIPT_LOCATION)
	private String translationScriptLocation;

	@Value(WrapperForPythonDatamodelTranslatorConstants.$TRANSLATION_INPUT_FOLDER)
	private String inputFolder;

	@Value(WrapperForPythonDatamodelTranslatorConstants.$TRANSLATION_OUTPUT_FOLDER)
	private String outputFolder;
	
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
    	logger.debug("initTranslationJob started...");
    	
    	final DataModelTranslationInitRequestDTO normalized = validator.validateAndNormalizeDataModelTranslationInitRequestDTO(dto, origin);

        DataModelTranslationTask job = new DataModelTranslationTask(normalized.inputModelId(), normalized.outputModelId(), normalized.payload());
        jobQueue.add(job);
        jobCache.put(job.getUuid(), job);
        return job.getUuid();
    }

    //-------------------------------------------------------------------------------------------------
    public DataModelTranslationResultResponseDTO getTranslationResult(UUID jobId, final String origin) {
    	logger.debug("getTranslationResult started...");

        final DataModelTranslationTask job = jobCache.get(jobId);
        
        if (job == null) {
        	throw new InvalidParameterException("Invalid UUID", origin);
        }

        final String result = readTranslatedFile(fileNameCache.get(jobId));
        return new DataModelTranslationResultResponseDTO(job.getStatus(), result);
    }

	//=================================================================================================
	// assistant methods

    //-------------------------------------------------------------------------------------------------
    private void doTranslationJob(final DataModelTranslationTask job) {
    	// start the job
    	job.setStatus(DataModelTranslationTaskStatus.IN_PROGRESS);

    	// decode the bytes
    	final byte[] bytes = Base64.getDecoder().decode(job.getPayload());

    	final String fileName = job.getUuid().toString() + ".xml";
    	try {
    		// save the file
    		Files.write(Paths.get(inputFolder, fileName), bytes);

    		// run the script
    		runTranslationScript(job.getUuid().toString());
    	} catch (final Exception ex) {
    		job.setStatus(DataModelTranslationTaskStatus.ERROR);
    		throw new InternalServerError("An error occured while running the translation script");
    	}

    	fileNameCache.put(job.getUuid(), fileName);

    	// the job is done
    	job.setStatus(DataModelTranslationTaskStatus.DONE);
    }

  //-------------------------------------------------------------------------------------------------
    private void runTranslationScript(final String name) throws ConfigurationException, InterruptedException, IOException {
    	
        try {
            final ProcessBuilder translationScriptProcessBuilder = new ProcessBuilder("python", translationScriptLocation, name);
            translationScriptProcessBuilder.inheritIO();
            translationScriptProcessBuilder.directory(new File(new File(translationScriptLocation).getParent()));
            final Process translationScriptProcess = translationScriptProcessBuilder.start();
            final int exitCode = translationScriptProcess.waitFor();
            if (exitCode != 0) {
            	logger.debug("Translation python script exited with code: " + exitCode);
            }
        } catch (final Exception ex) {
			throw ex;
        }
    }

    //-------------------------------------------------------------------------------------------------
    private String readTranslatedFile(final String filename) {

    	if (filename == null) {
    		return null;
    	}

        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(outputFolder, filename));
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (final IOException ex) {
        	logger.error(ex.getMessage());
            return null;
        }
    }

	//-------------------------------------------------------------------------------------------------
    private void start() {
        Thread worker = new Thread(() -> {
            while (true) {
                try {
                    DataModelTranslationTask job = jobQueue.take();
                    doTranslationJob(job);
                } catch (InterruptedException e) {
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
}
