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
package ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.service.normalization;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.service.normalization.AttributeNormalizer;
import eu.arrowhead.common.service.validation.name.DataModelIdentifierNormalizer;
import eu.arrowhead.dto.DataModelTranslationInitRequestDTO;

@Service
public class DataModelTranslationServiceNormalization {

	//=================================================================================================
	// members
	
	@Autowired
	private DataModelIdentifierNormalizer modelIdNormalizer;
	
	@Autowired
	private AttributeNormalizer attributeNormalizer;
	
	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public DataModelTranslationInitRequestDTO normalizeDataModelTranslationInitRequestDTO(final DataModelTranslationInitRequestDTO dto) {
		logger.debug("normalizeDataModelTranslationInitRequestDTO started...");
		
		return new DataModelTranslationInitRequestDTO(
				modelIdNormalizer.normalize(dto.inputModelId()),
				modelIdNormalizer.normalize(dto.outputModelId()),
				dto.payload(),
				normalizeSettings(dto.settings()));
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private Map<String, Object> normalizeSettings(final Map<String, Object> settings) {

		if (settings == null) {
			return settings;
		}

		final Map<String, Object> normalized = new HashMap<>(settings.size());
		settings.entrySet().forEach(s -> normalized.put(attributeNormalizer.normalizeAttribute(s.getKey()), s.getValue()));
		return normalized;
	}
}
