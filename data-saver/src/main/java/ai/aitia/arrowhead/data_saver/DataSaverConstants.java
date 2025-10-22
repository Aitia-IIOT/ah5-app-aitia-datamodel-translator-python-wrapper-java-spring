package ai.aitia.arrowhead.data_saver;

import eu.arrowhead.dto.enums.AuthorizationPolicyType;

public class DataSaverConstants {

	//=================================================================================================
	// members
	
	public static final String SYSTEM_NAME = "DataSaver";
	public static final String SYSTEM_VERSION = "1.0.0";
	public static final String SAVE_DATA_SERVICE_DEF = "saveData";
	public static final String VERSION_SAVE_DATA = "1.0.0";
	public static final String SERVICE_OP_SAVE_IPC2581 = "save-ipc2581";
	public static final String SERVICE_OP_SAVE_IPC2581_DATA_MODEL_INPUT = "ipc2581";
	
	// authorizaton grant
	public static final String AUTH_GRANT_DESCRIPTION = "can be used by every system in the Local Cloud";
	public static final String AUTH_GRANT_POLICY_TYPE = AuthorizationPolicyType.ALL.toString();
	
	// HTTP API
	public static final String HTTP_API_SAVE_DATA_BASE_PATH = "/save-data";
	public static final String HTTP_API_OP_SAVE_IPC2581_PATH = "/save-ipc2581";
	
	// save data
	public static final String SAVE_LOCATION = "save.location";
	public static final String $SAVE_LOCATION = "${" + SAVE_LOCATION + ":" + "}";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private DataSaverConstants() {
		throw new UnsupportedOperationException();
	}
}