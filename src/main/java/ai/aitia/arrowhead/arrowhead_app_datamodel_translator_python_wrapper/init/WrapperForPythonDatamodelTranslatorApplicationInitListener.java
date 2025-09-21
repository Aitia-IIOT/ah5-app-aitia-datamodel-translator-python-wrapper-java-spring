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
package ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.init;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.ConfigurationException;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.Constants;
import ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.WrapperForPythonDatamodelTranslatorSystemInfo;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.init.ApplicationInitListener;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.dto.ServiceInstanceCreateRequestDTO;
import eu.arrowhead.dto.ServiceInstanceInterfaceRequestDTO;
import eu.arrowhead.dto.ServiceInstanceResponseDTO;

@Component
public class WrapperForPythonDatamodelTranslatorApplicationInitListener extends ApplicationInitListener {

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) throws InterruptedException, ConfigurationException {
		logger.debug("customInit started...");

		final WrapperForPythonDatamodelTranslatorSystemInfo info = (WrapperForPythonDatamodelTranslatorSystemInfo) sysInfo;

		try {
			runInitScript(info.getInitScriptLocation());
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
	}

	//-------------------------------------------------------------------------------------------------
	private void runInitScript(final String initScriptLocation) throws ConfigurationException, InterruptedException, IOException {
		logger.debug("runInitScript started...");

        try {
            final ProcessBuilder initScriptProcessBuilder = new ProcessBuilder("python", initScriptLocation);
            initScriptProcessBuilder.inheritIO();
            final Process initScriptProcess = initScriptProcessBuilder.start();
            final int exitCode = initScriptProcess.waitFor();
            if (exitCode != 0) {
            	throw new InvalidParameterException("Initialization python script exited with code: " + exitCode);
            }
        } catch (final Exception ex) {
			throw ex;
        }
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
