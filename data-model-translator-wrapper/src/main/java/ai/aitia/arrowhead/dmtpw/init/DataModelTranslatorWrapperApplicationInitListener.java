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
package ai.aitia.arrowhead.dmtpw.init;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.dmtpw.DataModelTranslatorWrapperConstants;
import ai.aitia.arrowhead.dmtpw.DataModelTranslatorWrapperSystemInfo;
import ai.aitia.arrowhead.dmtpw.service.DataModelTranslationService;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.init.ApplicationInitListener;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.dto.AuthorizationGrantRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyRequestDTO;
import eu.arrowhead.dto.AuthorizationPolicyResponseDTO;
import eu.arrowhead.dto.ServiceInstanceCreateRequestDTO;
import eu.arrowhead.dto.ServiceInstanceInterfaceRequestDTO;
import eu.arrowhead.dto.ServiceInstanceResponseDTO;
import eu.arrowhead.dto.enums.AuthorizationTargetType;

@Component
public class DataModelTranslatorWrapperApplicationInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	// members
	
	@Autowired
	private DataModelTranslationService service;

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) throws InterruptedException, ConfigurationException {
		logger.debug("customInit started...");

		final DataModelTranslatorWrapperSystemInfo info = (DataModelTranslatorWrapperSystemInfo) sysInfo;

		try {
			runInitScript(info.getPythonLauncherPath(), info.getScriptLocation());
        } catch (final IOException ex) {
			throw new RuntimeException("Running the initialization python script was unsuccessful: " + ex.getMessage());
        }

		info.setInitScriptHasRun();
		// register services only after init script has run
		if (info.getServices() != null) {
			for (final ServiceModel serviceModel : info.getServices()) {
				registerServiceAfterInitScript(serviceModel);
			}
			logger.info("System {} published {} service(s)", sysInfo.getSystemName(), registeredServices.size());
		}
		
		final AuthorizationGrantRequestDTO payload = new AuthorizationGrantRequestDTO
				.Builder(AuthorizationTargetType.SERVICE_DEF)
				.target(Constants.SERVICE_DEF_DATA_MODEL_TRANSLATION)
				.description(DataModelTranslatorWrapperConstants.AUTH_GRANT_DESCRIPTION)
				.defaultPolicy(new AuthorizationPolicyRequestDTO(DataModelTranslatorWrapperConstants.AUTH_GRANT_POLICY_TYPE, null, null))
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
	
	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customDestroy() {
		service.RUNNING = false;
	}

	//-------------------------------------------------------------------------------------------------
	private void runInitScript(final String pythonLauncherPath, final String initScriptLocation) throws ConfigurationException, InterruptedException, IOException {
		logger.debug("runInitScript started...");

        /*try {
            final ProcessBuilder initScriptProcessBuilder = new ProcessBuilder(pythonLauncherPath, "-m", DataModelTranslatorWrapperConstants.INIT_MODULE_NAME);
        	initScriptProcessBuilder.directory(new File(initScriptLocation));
            initScriptProcessBuilder.inheritIO();
            final Process initScriptProcess = initScriptProcessBuilder.start();
            final int exitCode = initScriptProcess.waitFor();
            if (exitCode != 0) {
            	throw new InvalidParameterException("Initialization python script exited with code: " + exitCode);
            }
        } catch (final Exception ex) {
			throw ex;
        }*/
    }

	//-------------------------------------------------------------------------------------------------
	private void registerServiceAfterInitScript(final ServiceModel model) {
		logger.debug("registerServiceAfterInitScript started...");

		final List<ServiceInstanceInterfaceRequestDTO> interfaces = model.interfaces()
				.stream()
				.map(i -> new ServiceInstanceInterfaceRequestDTO(i.templateName(), i.protocol(), i.policy(), i.properties()))
				.collect(Collectors.toList());
		final ServiceInstanceCreateRequestDTO payload = new ServiceInstanceCreateRequestDTO(model.serviceDefinition(), model.version(), null, model.metadata(), interfaces);
		final ServiceInstanceResponseDTO response = arrowheadHttpService.consumeService(
				Constants.SERVICE_DEF_SERVICE_DISCOVERY,
				Constants.SERVICE_OP_REGISTER,
				Constants.SYS_NAME_SERVICE_REGISTRY,
				ServiceInstanceResponseDTO.class,
				payload);
		registeredServices.add(response.instanceId());
	}
}
