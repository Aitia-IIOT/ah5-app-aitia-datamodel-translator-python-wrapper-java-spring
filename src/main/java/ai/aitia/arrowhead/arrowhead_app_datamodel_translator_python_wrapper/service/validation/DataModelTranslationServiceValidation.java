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
 *  	AITIA - implementation
 *  	Arrowhead Consortia - conceptualization
 *
 *******************************************************************************/
package ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.service.validation;

import java.util.Base64;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.WrapperForPythonDatamodelTranslatorSystemInfo;
import ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.service.normalization.DataModelTranslationServiceNormalization;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.service.validation.name.DataModelIdentifierValidator;

import eu.arrowhead.dto.DataModelTranslationInitRequestDTO;

@Service
public class DataModelTranslationServiceValidation {

	//=================================================================================================
	// members

	@Autowired
	private DataModelTranslationServiceNormalization normalizer;

	@Autowired
	private DataModelIdentifierValidator modelIdValidator;
	
	@Autowired
	private WrapperForPythonDatamodelTranslatorSystemInfo sysInfo;

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods
	
	// VALIDATION AND NORMALIZATION

	//-------------------------------------------------------------------------------------------------
	public DataModelTranslationInitRequestDTO validateAndNormalizeDataModelTranslationInitRequestDTO(final DataModelTranslationInitRequestDTO dto, final String origin) {
		logger.debug("validateAndNormalizeDataModelTranslationInitRequestDTO started...");

		final DataModelTranslationInitRequestDTO normalized = normalizer.normalizeDataModelTranslationInitRequestDTO(dto);
		validatDataModelTranslationInitRequestDTO(normalized, origin);
		return normalized;
	}

	//=================================================================================================
	// assistant methods

	// VALIDATION

	//-------------------------------------------------------------------------------------------------
	private void validatDataModelTranslationInitRequestDTO(final DataModelTranslationInitRequestDTO dto, final String origin) {

		// model ids
		try {
			modelIdValidator.validateDataModelIdentifier(dto.inputModelId());
			modelIdValidator.validateDataModelIdentifier(dto.outputModelId());	
		} catch (final InvalidParameterException ex) {
			throw new InvalidParameterException(ex.getMessage(), origin);
		}
		if (!sysInfo.getModelIdsWithResultMimeTpyes().containsKey(List.of(dto.inputModelId(), dto.outputModelId()))) {
			throw new InvalidParameterException("It is not possible to translate from " + dto.inputModelId() + " to " + dto.outputModelId());
		}
		
		// payload
		if (Utilities.isEmpty(dto.payload())) {
			throw new InvalidParameterException("Translation payload is empty!", origin);
		}
		try {
			  Base64.getDecoder().decode(dto.payload());
		} catch (final IllegalArgumentException ex) {
			throw new InvalidParameterException("Translation payload format is incorrect, must be base64 encoded!", origin);
		}
	}
}
