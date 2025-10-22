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
package ai.aitia.arrowhead.data_saver.init;

import javax.naming.ConfigurationException;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.data_saver.DataSaverConstants;
import eu.arrowhead.common.init.ApplicationInitListener;
import eu.arrowhead.dto.AuthorizationGrantRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyResponseDTO;
import eu.arrowhead.dto.enums.AuthorizationTargetType;

@Component
public class DataSaverApplicationInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) throws InterruptedException, ConfigurationException {
		logger.debug("customInit started...");
		
		final AuthorizationGrantRequestDTO payload = new AuthorizationGrantRequestDTO
				.Builder(AuthorizationTargetType.SERVICE_DEF)
				.target(DataSaverConstants.SAVE_DATA_SERVICE_DEF)
				.description(DataSaverConstants.AUTH_GRANT_DESCRIPTION)
				.defaultPolicy(new AuthorizationPolicyRequestDTO(DataSaverConstants.AUTH_GRANT_POLICY_TYPE, null, null))
				.scopedPolicies(null)
				.build();

		try {
			arrowheadHttpService.consumeService(
					Constants.SERVICE_DEF_AUTHORIZATION,
					Constants.SERVICE_OP_GRANT,
					AuthorizationPolicyResponseDTO.class,
					payload);
		} catch (final Exception ex) {
			logger.info("Error while consuming {} service's {} operation: " + ex.getMessage(), Constants.SERVICE_DEF_AUTHORIZATION, Constants.SERVICE_OP_GRANT);
		}
	}
}
