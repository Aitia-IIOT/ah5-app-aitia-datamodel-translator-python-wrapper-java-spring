package ai.aitia.arrowhead.data_saver.api.http;

import ai.aitia.arrowhead.data_saver.DataSaverConstants;
import ai.aitia.arrowhead.data_saver.service.SaveDataService;
import eu.arrowhead.dto.ErrorMessageDTO;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.arrowhead.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(DataSaverConstants.HTTP_API_SAVE_DATA_BASE_PATH)
public class SaveDataAPI {

	//=================================================================================================
	// members
	
	private final Logger logger = LogManager.getLogger(this.getClass());
	
	@Autowired
	private SaveDataService service;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Operation(summary = "Saves input data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = Constants.HTTP_STATUS_CREATED, description = Constants.SWAGGER_HTTP_201_MESSAGE),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_BAD_REQUEST, description = Constants.SWAGGER_HTTP_400_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) }),
			@ApiResponse(responseCode = Constants.HTTP_STATUS_INTERNAL_SERVER_ERROR, description = Constants.SWAGGER_HTTP_500_MESSAGE, content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorMessageDTO.class)) })
	})
	@PostMapping(path = DataSaverConstants.HTTP_API_OP_SAVE_IPC2581_PATH, consumes = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<Void> saveIpc2581(@RequestBody(required = true) final String data) {
		logger.debug("saveIpc2581 started...");

		final String origin = HttpMethod.POST.name() + " "
				+ DataSaverConstants.HTTP_API_SAVE_DATA_BASE_PATH
				+ DataSaverConstants.HTTP_API_OP_SAVE_IPC2581_PATH;

		service.saveIpc2581(data, origin);
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}
}
