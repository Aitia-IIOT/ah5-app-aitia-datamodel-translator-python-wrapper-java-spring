package ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper;

import java.util.ArrayList;
import java.util.List;

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
import eu.arrowhead.dto.enums.ServiceInterfacePolicy;

@Component
public class WrapperForPythonDatamodelTranslatorSystemInfo extends SystemInfo {

	//=================================================================================================
	// members
	
	private SystemModel systemModel;
	
	@Value(WrapperForPythonDatamodelTranslatorConstants.$INIT_SCRIPT_LOCATION)
	private String initScriptLocation;
	
	@Value(WrapperForPythonDatamodelTranslatorConstants.$MODEL_IDS)
	private List<String> modelIds;
	
	private boolean initScriptHasRun = false;

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
			.metadata(Constants.METADATA_KEY_DATA_MODEL_IDS, getModelIds())
			.serviceInterface(getHTTPInterfaceForInterfaceBridgeManagement())
			.build();
		return List.of(dataModelTranslationService);
	}
	
	//-------------------------------------------------------------------------------------------------
	public void setInitScriptHasRun() {
		initScriptHasRun = true;
	}
	
	//-------------------------------------------------------------------------------------------------
	public String getInitScriptLocation() {
		return initScriptLocation;
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
	
	//-------------------------------------------------------------------------------------------------
	private List<List<String>> getModelIds() {

		if (modelIds.size() % 2 != 0) {
			throw new InvalidParameterException("The list of model ids is not specified correctly!");
		}

		final List<List<String>> result = new ArrayList<List<String>>(modelIds.size()/2);

		for (int i = 0; i < modelIds.size(); i += 2) {
			result.add(List.of(modelIds.get(i), modelIds.get(i+1)));
		}
		
		return result;
	}
}
