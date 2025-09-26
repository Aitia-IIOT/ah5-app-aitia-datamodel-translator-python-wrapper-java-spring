package ai.aitia.arrowhead.data_saver;

import java.util.List;

import org.springframework.stereotype.Component;

import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.model.SystemModel;

@Component
public class DataSaverSystemInfo extends SystemInfo {

	//=================================================================================================
	// members

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
		return null;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public List<ServiceModel> getServices() {
		return List.of();
	}
}
