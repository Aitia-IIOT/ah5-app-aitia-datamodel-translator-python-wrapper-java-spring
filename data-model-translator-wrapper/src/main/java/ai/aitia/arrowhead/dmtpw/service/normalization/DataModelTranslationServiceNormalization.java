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
package ai.aitia.arrowhead.dmtpw.service.normalization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.service.validation.name.DataModelIdentifierNormalizer;
import eu.arrowhead.dto.DataModelTranslationInitRequestDTO;

@Service
public class DataModelTranslationServiceNormalization {

	//=================================================================================================
	// members
	
	@Autowired
	private DataModelIdentifierNormalizer modelIdNormalizer;
	
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
				dto.settings());
	}
}
