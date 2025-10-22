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
package ai.aitia.arrowhead.data_saver.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ai.aitia.arrowhead.data_saver.DataSaverConstants;
import eu.arrowhead.common.exception.InternalServerError;
import jakarta.annotation.PostConstruct;

@Service
public class SaveDataService {

	//=================================================================================================
	// members
	
	private final Logger logger = LogManager.getLogger(this.getClass());
	
	@Value(DataSaverConstants.$SAVE_LOCATION)
	private String saveLocation;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void saveIpc2581(final String data, final String origin) {
		logger.debug("SaveDataService.saveIpc2581 started...");
		
		final String fileName = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".bin";
		
        try {
            Files.writeString(Paths.get(saveLocation, fileName), data);
            logger.debug("saved data to: {}", fileName);
        } catch (final IOException ex) {
            throw new InternalServerError("Could not save data", origin);
        }
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() {
        try {
            Paths.get(saveLocation);
        } catch (final InvalidPathException | NullPointerException ex) {
        	throw new RuntimeException("Invalid save location path");
        }

	}
}
