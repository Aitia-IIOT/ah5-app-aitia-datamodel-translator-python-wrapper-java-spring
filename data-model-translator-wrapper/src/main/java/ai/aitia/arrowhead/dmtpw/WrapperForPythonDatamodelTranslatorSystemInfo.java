package ai.aitia.arrowhead.dmtpw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.http.model.HttpInterfaceModel;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.model.InterfaceModel;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.model.SystemModel;
import eu.arrowhead.common.service.validation.name.DataModelIdentifierNormalizer;
import eu.arrowhead.common.service.validation.name.DataModelIdentifierValidator;
import eu.arrowhead.dto.enums.ServiceInterfacePolicy;

@Component
public class WrapperForPythonDatamodelTranslatorSystemInfo extends SystemInfo {

	//=================================================================================================
	// members

	private SystemModel systemModel;
	
	@Value(WrapperForPythonDatamodelTranslatorConstants.$TRANSLATION_SCRIPT_LOCATION)
	private String translationScriptLocation;

	@Value(WrapperForPythonDatamodelTranslatorConstants.$TRANSLATION_INPUT_FOLDER)
	private String inputFolder;

	@Value(WrapperForPythonDatamodelTranslatorConstants.$TRANSLATION_OUTPUT_FOLDER)
	private String outputFolder;

	@Value(WrapperForPythonDatamodelTranslatorConstants.$INIT_SCRIPT_LOCATION)
	private String initScriptLocation;

	@Value(WrapperForPythonDatamodelTranslatorConstants.$MODEL_IDS)
	private List<String> modelIds;
	
	@Value(WrapperForPythonDatamodelTranslatorConstants.$RESULT_MIME_TYPES)
	private List<String> resultMimeTypes;

	private boolean initScriptHasRun = false;
	
	private Map<List<String>, String> modelIdsWithResultMimeTpyes = null;
	
	@Autowired
	private DataModelIdentifierValidator modelIdValidator;
	
	@Autowired
	private DataModelIdentifierNormalizer modelIdNormalizer;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public String getSystemName() {
		return WrapperForPythonDatamodelTranslatorConstants.SYSTEM_NAME;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public SystemModel getSystemModel() {
		if (systemModel == null) {
			SystemModel.Builder builder = new SystemModel.Builder()
					.address(getAddress())
					.version(WrapperForPythonDatamodelTranslatorConstants.SYSTEM_VERSION);

			if (AuthenticationPolicy.CERTIFICATE == this.getAuthenticationPolicy()) {
				builder = builder.metadata(Constants.METADATA_KEY_X509_PUBLIC_KEY, getPublicKey());
			}

			systemModel = builder.build();
		}

		return systemModel;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public List<ServiceModel> getServices() {

		if (!initScriptHasRun) {
			return null;
		}

		final ServiceModel dataModelTranslationService = new ServiceModel.Builder()
			.serviceDefinition(Constants.SERVICE_DEF_DATA_MODEL_TRANSLATION)
			.version(WrapperForPythonDatamodelTranslatorConstants.VERSION_DATA_MODEL_TRANSLATION)
			.metadata(Constants.METADATA_KEY_DATA_MODEL_IDS, getModelIdsWithResultMimeTpyes().keySet())
			.serviceInterface(getHTTPInterfaceForInterfaceBridgeManagement())
			.build();
		return List.of(dataModelTranslationService);
	}
	
	//-------------------------------------------------------------------------------------------------
	// result keys: input modelid, output modelid
	// result values: mime type
	public Map<List<String>, String> getModelIdsWithResultMimeTpyes() {
		
		if (modelIdsWithResultMimeTpyes == null) {

			if (modelIds.size() % 2 != 0) {
				throw new InvalidParameterException("The list of model ids is not specified correctly");
			}
			
			// normalize model ids
			final List<String> normalizedModelIds = new ArrayList<String>(modelIds.size());
			for (final String modelId : modelIds) {
				final String normalized = modelIdNormalizer.normalize(modelId);
				modelIdValidator.validateDataModelIdentifier(normalized);
				normalizedModelIds.add(normalized);
			}
		
			if (resultMimeTypes.size() != normalizedModelIds.size() / 2) {
				throw new InvalidParameterException("There must be exactly one result mime type specified for each model id pair");
			}

			modelIdsWithResultMimeTpyes = new HashMap<List<String>, String>(normalizedModelIds.size() / 2);

			for (int i = 0, j = 0; i < modelIds.size(); i += 2, ++j) {

				if (modelIdsWithResultMimeTpyes.containsKey(List.of(normalizedModelIds.get(i), normalizedModelIds.get(i+1)))) {
					throw new InvalidParameterException("Duplicated input-output model id pair: " + normalizedModelIds.get(i) + ", " + normalizedModelIds.get(i+1));
				}
				modelIdsWithResultMimeTpyes.put(List.of(normalizedModelIds.get(i), normalizedModelIds.get(i+1)), resultMimeTypes.get(j));
			}
		}
		return modelIdsWithResultMimeTpyes;
	}

	//-------------------------------------------------------------------------------------------------
	public void setInitScriptHasRun() {
		initScriptHasRun = true;
	}

	//-------------------------------------------------------------------------------------------------
	public String getInitScriptLocation() {
		return initScriptLocation;
	}
	
	//-------------------------------------------------------------------------------------------------
	public String getTranslationScriptLocation() {
		return translationScriptLocation;
	}
	
	//-------------------------------------------------------------------------------------------------
	public String getInputFolder() {
		return inputFolder;
	}
	
	//-------------------------------------------------------------------------------------------------
	public String getOutputFolder() {
		return outputFolder;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private InterfaceModel getHTTPInterfaceForInterfaceBridgeManagement() {
		final String templateName = getSslProperties().isSslEnabled() ? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME;

		final HttpOperationModel initTranslation = new HttpOperationModel.Builder()
				.method(HttpMethod.POST.name())
				.path(WrapperForPythonDatamodelTranslatorConstants.HTTP_API_OP_INIT_TRANSLATION_PATH)
				.build();

		final HttpOperationModel getTranslationResult = new HttpOperationModel.Builder()
				.method(HttpMethod.GET.name())
				.path(WrapperForPythonDatamodelTranslatorConstants.HTTP_API_OP_GET_TRANSLATION_RESULT_PATH)
				.build();

		final HttpOperationModel abortTranslation = new HttpOperationModel.Builder()
				.method(HttpMethod.DELETE.name())
				.path(WrapperForPythonDatamodelTranslatorConstants.HTTP_API_OP_ABORT_TRANSLATION_PATH)
				.build();

		return new HttpInterfaceModel.Builder(templateName, getDomainAddress(), getServerPort())
				.policy(ServiceInterfacePolicy.NONE)
				.basePath(WrapperForPythonDatamodelTranslatorConstants.HTTP_API_DATA_MODEL_TRANSLATION_BASE_PATH)
				.operation(Constants.SERVICE_OP_DATA_MODEL_TRANSLATOR_INIT_TRANSLATION, initTranslation)
				.operation(Constants.SERVICE_OP_DATA_MODEL_TRANSLATOR_GET_TRANSLATION_RESULT, getTranslationResult)
				.operation(Constants.SERVICE_OP_DATA_MODEL_TRANSLATOR_ABORT_TRANSLATION, abortTranslation)
				.build();
	}
}
