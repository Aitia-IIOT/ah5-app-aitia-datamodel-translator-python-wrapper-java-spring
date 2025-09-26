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
package ai.aitia.arrowhead.data_saver.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ai.aitia.arrowhead.data_saver.DataSaverSystemInfo;
import eu.arrowhead.common.swagger.DefaultSwaggerConfig;
import jakarta.annotation.PostConstruct;

@Configuration
public class SwaggerConfig extends DefaultSwaggerConfig {

	//=================================================================================================
	// members
	
	@Autowired
	private DataSaverSystemInfo sysInfo;

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public SwaggerConfig() {
		super(null, "1.0.0");
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() {
		setSystemName(sysInfo.getSystemName());
	}
}
