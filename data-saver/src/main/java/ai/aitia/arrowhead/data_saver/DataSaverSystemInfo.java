package ai.aitia.arrowhead.data_saver;

import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.http.model.HttpDataModelsOperationModel;
import eu.arrowhead.common.http.model.HttpInterfaceModel;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.model.InterfaceModel;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.model.SystemModel;
import eu.arrowhead.dto.enums.ServiceInterfacePolicy;

@Component
public class DataSaverSystemInfo extends SystemInfo {

	//=================================================================================================
	// members
	
	private SystemModel systemModel;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public String getSystemName() {
		return DataSaverConstants.SYSTEM_NAME;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public SystemModel getSystemModel() {

		if (systemModel == null) {
			SystemModel.Builder builder = new SystemModel.Builder()
					.address(getAddress())
					.version(DataSaverConstants.SYSTEM_VERSION);

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

		final ServiceModel saveDataService = new ServiceModel.Builder()
				.serviceDefinition(DataSaverConstants.SAVE_DATA_SERVICE_DEF)
				.version(DataSaverConstants.VERSION_SAVE_DATA)
				.serviceInterface(getHTTPInterfaceForSaveData())
				.build();
			return List.of(saveDataService);
	}
	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	private InterfaceModel getHTTPInterfaceForSaveData() {

		final String templateName = getSslProperties().isSslEnabled() ? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME;

		final HttpOperationModel saveIpc2581 = new HttpOperationModel.Builder()
				.method(HttpMethod.POST.name())
				.path(DataSaverConstants.HTTP_API_OP_SAVE_IPC2581_PATH)
				.build();
		
		final HttpDataModelsOperationModel saveIpc2581DataModel = new HttpDataModelsOperationModel.Builder()
				.input(DataSaverConstants.SERVICE_OP_SAVE_IPC2581_DATA_MODEL_INPUT)
				.build();

		return new HttpInterfaceModel.Builder(templateName, getDomainAddress(), getServerPort())
				.policy(ServiceInterfacePolicy.NONE)
				.basePath(DataSaverConstants.HTTP_API_SAVE_DATA_BASE_PATH)
				.operation(DataSaverConstants.SERVICE_OP_SAVE_IPC2581, saveIpc2581)
				.dataModel(DataSaverConstants.SERVICE_OP_SAVE_IPC2581, saveIpc2581DataModel)
				.build();
	}
}
