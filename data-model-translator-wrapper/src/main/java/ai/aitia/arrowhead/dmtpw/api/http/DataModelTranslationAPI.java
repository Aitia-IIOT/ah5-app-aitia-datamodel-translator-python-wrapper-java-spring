package ai.aitia.arrowhead.dmtpw.api.http;

import java.security.Provider.Service;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.dmtpw.WrapperForPythonDatamodelTranslatorConstants;
import ai.aitia.arrowhead.dmtpw.service.DataModelTranslationService;
import eu.arrowhead.dto.DataModelTranslationInitRequestDTO;
import eu.arrowhead.dto.DataModelTranslationResultResponseDTO;
import eu.arrowhead.dto.ErrorMessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(WrapperForPythonDatamodelTranslatorConstants.HTTP_API_DATA_MODEL_TRANSLATION_BASE_PATH)
@SecurityRequirement(name = Constants.SECURITY_REQ_AUTHORIZATION)
public class DataModelTranslationAPI {

	//=================================================================================================
	// members

	@Autowired
	private DataModelTranslationService translationService;

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	// init-translation

	//-------------------------------------------------------------------------------------------------
	@Operation(summary = "Initiates the data model translation job and returns its ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = Constants.HTTP_STATUS_OK, description = Constants.SWAGGER_HTTP_200_MESSAGE, content = {
					@Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = UUID.class)) }),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_BAD_REQUEST, description = Constants.SWAGGER_HTTP_400_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) }),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_INTERNAL_SERVER_ERROR, description = Constants.SWAGGER_HTTP_500_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) }),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_SERVICE_UNAVAILABLE, description = Constants.SWAGGER_HTTP_503_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) })
	})
	@PostMapping(path = WrapperForPythonDatamodelTranslatorConstants.HTTP_API_OP_INIT_TRANSLATION_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String initTranslation(@RequestBody(required = true) final DataModelTranslationInitRequestDTO dto) {
		logger.debug("initTranslation started...");

		final String origin = HttpMethod.POST.name() + " "
				+ WrapperForPythonDatamodelTranslatorConstants.HTTP_API_DATA_MODEL_TRANSLATION_BASE_PATH
				+ WrapperForPythonDatamodelTranslatorConstants.HTTP_API_OP_INIT_TRANSLATION_PATH;

		return translationService.initTranslation(dto, origin).toString();
	}

	// get-translation-result
	//-------------------------------------------------------------------------------------------------
	@Operation(summary = "Returns the translation status and optionally the result file by ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = Constants.HTTP_STATUS_OK, description = Constants.SWAGGER_HTTP_200_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataModelTranslationResultResponseDTO.class)) }),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_BAD_REQUEST, description = Constants.SWAGGER_HTTP_400_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) }),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_NOT_FOUND, description = Constants.SWAGGER_HTTP_404_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) }),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_INTERNAL_SERVER_ERROR, description = Constants.SWAGGER_HTTP_500_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) })
	})
	@GetMapping(path = WrapperForPythonDatamodelTranslatorConstants.HTTP_API_OP_GET_TRANSLATION_RESULT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody DataModelTranslationResultResponseDTO getTranslationResult(@RequestParam final UUID taskId) {
		logger.debug("getTranslationResult started...");
		
		final String origin = HttpMethod.POST.name() + " "
				+ WrapperForPythonDatamodelTranslatorConstants.HTTP_API_DATA_MODEL_TRANSLATION_BASE_PATH
				+ WrapperForPythonDatamodelTranslatorConstants.HTTP_API_OP_GET_TRANSLATION_RESULT_PATH;

		return translationService.getTranslationResult(taskId, origin);
	}

	// abort-translation

	//-------------------------------------------------------------------------------------------------
	@Operation(summary = "Not implemented operation")
	@ApiResponses(value = {
			@ApiResponse(responseCode = Constants.HTTP_STATUS_OK, description = Constants.SWAGGER_HTTP_200_MESSAGE),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_BAD_REQUEST, description = Constants.SWAGGER_HTTP_400_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) }),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_INTERNAL_SERVER_ERROR, description = Constants.SWAGGER_HTTP_500_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) })
	})
	@DeleteMapping(path = WrapperForPythonDatamodelTranslatorConstants.HTTP_API_OP_ABORT_TRANSLATION_PATH)
	public ResponseEntity<Void> abortTranslation(@RequestParam final UUID taskId) {
		logger.debug("abortTranslation started...");
		
		logger.info("abortTranslation is not implemented!");
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
