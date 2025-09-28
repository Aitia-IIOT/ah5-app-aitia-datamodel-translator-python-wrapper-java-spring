package ai.aitia.arrowhead.dmtpw;

public class DataModelTranslatorWrapperConstants {

	//=================================================================================================
	// members

	public static final String SYSTEM_NAME = "DataModelTranslatorWrapper";
	public static final String SYSTEM_VERSION = "1.0.0";
	public static final String VERSION_DATA_MODEL_TRANSLATION = "1.0.0";

	// HTTP API
	public static final String HTTP_API_DATA_MODEL_TRANSLATION_BASE_PATH = "/data-model-translation";
	public static final String HTTP_API_OP_INIT_TRANSLATION_PATH = "/init-translation";
	public static final String HTTP_API_OP_GET_TRANSLATION_RESULT_PATH = "/get-translation-result";
	public static final String HTTP_API_OP_ABORT_TRANSLATION_PATH = "/abort-translation";

	// translation
	public static final String INIT_SCRIPT_LOCATION = "init.script.location";
	public static final String $INIT_SCRIPT_LOCATION = "${" + INIT_SCRIPT_LOCATION + ":" + "}";
	public static final String TRANSLATION_SCRIPT_LOCATION = "translation.script.location";
	public static final String $TRANSLATION_SCRIPT_LOCATION = "${" + TRANSLATION_SCRIPT_LOCATION + ":" + "}";
	public static final String TRANSLATION_INPUT_FOLDER= "translation.script.input.folder";
	public static final String $TRANSLATION_INPUT_FOLDER = "${" + TRANSLATION_INPUT_FOLDER + ":" + "}";
	public static final String TRANSLATION_OUTPUT_FOLDER= "translation.script.output.folder";
	public static final String $TRANSLATION_OUTPUT_FOLDER = "${" + TRANSLATION_OUTPUT_FOLDER + ":" + "}";
	public static final String MODEL_IDS = "model.ids";
	public static final String $MODEL_IDS = "#{'${" + MODEL_IDS + ":" +  "}'.split(',')}";
	public static final String RESULT_MIME_TYPES = "result.mime.types";
	public static final String $RESULT_MIME_TYPES = "#{'${" + RESULT_MIME_TYPES + ":" +  "}'.split(',')}";

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private DataModelTranslatorWrapperConstants() {
		throw new UnsupportedOperationException();
	}
}
