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
package ai.aitia.arrowhead.dmtpw.service.model;

import java.util.UUID;

import eu.arrowhead.dto.enums.DataModelTranslationTaskStatus;

public class DataModelTranslationTask {

	//=================================================================================================
	// members

	private final UUID uuid;
	private DataModelTranslationTaskStatus status;
	private String resultMimeType;
	private String payload;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public DataModelTranslationTask(final String resultMimeType, final String payload) {
		this.uuid = UUID.randomUUID();
		this.status = DataModelTranslationTaskStatus.PENDING;
		this.resultMimeType = resultMimeType;
		this.payload = payload;
	}

	//=================================================================================================
	// boilerplate

	//-------------------------------------------------------------------------------------------------
	public UUID getUuid() {
		return uuid;
	}

	//-------------------------------------------------------------------------------------------------
	public DataModelTranslationTaskStatus getStatus() {
		return status;
	}

	//-------------------------------------------------------------------------------------------------
	public void setStatus(final DataModelTranslationTaskStatus status) {
		this.status = status;
	}

	//-------------------------------------------------------------------------------------------------
	public String getResultMimeType() {
		return resultMimeType;
	}

	//-------------------------------------------------------------------------------------------------
	public String getPayload() {
		return payload;
	}

	//-------------------------------------------------------------------------------------------------
	public void setPayload(final String payload) {
		this.payload = payload;
	}
}
