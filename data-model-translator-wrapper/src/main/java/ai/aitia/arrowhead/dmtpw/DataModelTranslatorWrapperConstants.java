/*******************************************************************************
 *
 * Copyright (c) 2025 AITIA
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 *
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  	AITIA
 *
 *******************************************************************************/
package ai.aitia.arrowhead.dmtpw;

import eu.arrowhead.dto.enums.AuthorizationPolicyType;

public class DataModelTranslatorWrapperConstants {

	//=================================================================================================
	// members

	public static final String SYSTEM_VERSION = "1.0.0";
	public static final String VERSION_DATA_MODEL_TRANSLATION = "1.0.0";
	
	// authorizaton grant
	public static final String AUTH_GRANT_DESCRIPTION = "can be used by every system in the Local Cloud";
	public static final String AUTH_GRANT_POLICY_TYPE = AuthorizationPolicyType.ALL.toString();

	// HTTP API
	public static final String HTTP_API_DATA_MODEL_TRANSLATION_BASE_PATH = "/data-model-translation";
	public static final String HTTP_API_OP_INIT_TRANSLATION_PATH = "/init-translation";
	public static final String HTTP_API_OP_GET_TRANSLATION_RESULT_PATH = "/get-translation-result";
	public static final String HTTP_API_OP_GET_TRANSLATION_RESULT_REQUEST_PARAM = "?taskId={}";
	public static final String HTTP_API_OP_ABORT_TRANSLATION_PATH = "/abort-translation";

	// translation
	public static final String SCRIPT_LOCATION = "script.location";
	public static final String $SCRIPT_LOCATION = "${" + SCRIPT_LOCATION + ":" + "}";
	public static final String TRANSLATION_INPUT_FOLDER= "translation.script.input.folder";
	public static final String $TRANSLATION_INPUT_FOLDER = "${" + TRANSLATION_INPUT_FOLDER + ":" + "}";
	public static final String TRANSLATION_OUTPUT_FOLDER= "translation.script.output.folder";
	public static final String $TRANSLATION_OUTPUT_FOLDER = "${" + TRANSLATION_OUTPUT_FOLDER + ":" + "}";
	public static final String MODEL_IDS = "model.ids";
	public static final String $MODEL_IDS = "#{'${" + MODEL_IDS + ":" +  "}'.split(',')}";
	public static final String RESULT_MIME_TYPES = "result.mime.types";
	public static final String $RESULT_MIME_TYPES = "#{'${" + RESULT_MIME_TYPES + ":" +  "}'.split(',')}";
	public static final String PYTHON_LAUNCHER_PATH = "python.launcher.path";
	public static final String $PYTHON_LAUNCHER_PATH = "${" + PYTHON_LAUNCHER_PATH + ":" + "}";
	
	// module names of the scripts
	public static final String INIT_MODULE_NAME = "src.automated.map_gen";
	public static final String TRANSLATION_MODULE_NAME = "src.scripts.translation.tag_swap2";

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private DataModelTranslatorWrapperConstants() {
		throw new UnsupportedOperationException();
	}
}
