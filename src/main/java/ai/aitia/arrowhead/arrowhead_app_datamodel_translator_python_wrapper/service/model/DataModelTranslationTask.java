package ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.service.model;

import java.util.UUID;

import eu.arrowhead.dto.enums.DataModelTranslationTaskStatus;

public class DataModelTranslationTask {

	//=================================================================================================
	// members

	private UUID uuid;
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
	public void setStatus(DataModelTranslationTaskStatus status) {
		this.status = status;
	}

	//-------------------------------------------------------------------------------------------------
	public String getResultMimeType() {
		return resultMimeType;
	}

	//-------------------------------------------------------------------------------------------------
	public void setInputModelId(final String resultMimeType) {
		this.resultMimeType = resultMimeType;
	}

	//-------------------------------------------------------------------------------------------------
	public String getPayload() {
		return payload;
	}

	//-------------------------------------------------------------------------------------------------
	public void setPayload(String payload) {
		this.payload = payload;
	}
}
