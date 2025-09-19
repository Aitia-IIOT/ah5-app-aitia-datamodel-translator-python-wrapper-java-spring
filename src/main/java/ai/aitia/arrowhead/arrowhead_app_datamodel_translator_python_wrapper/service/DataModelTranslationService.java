package ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.service;

import java.io.BufferedReader;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.WrapperForPythonDatamodelTranslatorConstants;
import ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.service.enums.DataModelTranslationTask;
import eu.arrowhead.common.exception.InternalServerError;
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
	
	private final BlockingQueue<DataModelTranslationTask> jobQueue = new LinkedBlockingQueue<>();
	private final Map<UUID, DataModelTranslationTask> jobCache = new ConcurrentHashMap<>();
	private final Map<UUID, String> fileNameCache = new ConcurrentHashMap<>();
	
	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
    public UUID initTranslation(final DataModelTranslationInitRequestDTO dto) {
    	logger.debug("initTranslationJob started...");
    	
        DataModelTranslationTask job = new DataModelTranslationTask(dto.inputModelId(), dto.outputModelId(), dto.payload());
        jobQueue.add(job);
        jobCache.put(job.getUuid(), job);
        return job.getUuid();
    }

    //-------------------------------------------------------------------------------------------------
    public DataModelTranslationResultResponseDTO getTranslationResult(UUID jobId) {
    	logger.debug("getTranslationResult started...");

        final DataModelTranslationTask job = jobCache.get(jobId);
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
    	
    	final String inputFileName = job.getUuid().toString() + ".bin";
    	String outputFileName = null;
    	try {
    		// save the file
    		Files.write(Paths.get(inputFolder, inputFileName), bytes);
    	
    		// run the script
    		outputFileName = runTranslationScript(inputFileName);
    	} catch (final Exception ex) {
    		job.setStatus(DataModelTranslationTaskStatus.ERROR);
    		throw new InternalServerError("An error occured while running the translation script");
    	}
    	if (outputFileName != null) {
    		fileNameCache.put(job.getUuid(), outputFileName);
    	}
    	
    	// the job is done
    	job.setStatus(DataModelTranslationTaskStatus.DONE);
    }
    
  //-------------------------------------------------------------------------------------------------
    private String runTranslationScript(final String inputFileName) throws ConfigurationException, InterruptedException, IOException {

        try {
            final ProcessBuilder initScriptProcessBuilder = new ProcessBuilder("python", translationScriptLocation, Paths.get(inputFolder, inputFileName).toString());
            final Process initScriptProcess = initScriptProcessBuilder.start();
            
            String outputFileName = null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(initScriptProcess.getInputStream()))) {
            	outputFileName = reader.readLine();
            }
            final int exitCode = initScriptProcess.waitFor();
            if (exitCode != 0) {
            	logger.debug("Translation python script exited with code: " + exitCode);
            }
            return outputFileName;
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
