package ai.aitia.arrowhead.arrowhead_app_datamodel_translator_python_wrapper.service.model;

import java.util.UUID;

import eu.arrowhead.dto.enums.DataModelTranslationTaskStatus;

public class DataModelTranslationTask {

	//=================================================================================================
	// members
	
	private UUID uuid;
	private DataModelTranslationTaskStatus status;
	private String inputModelId;
	private String outputModelId;
	private String payload;

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public DataModelTranslationTask(final String inputModelId, final String outputModelId, final String payload) {
		this.uuid = UUID.randomUUID();
		this.status = DataModelTranslationTaskStatus.PENDING;
		this.inputModelId = inputModelId;
		this.outputModelId = outputModelId;
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
	public String getInputModelId() {
		return inputModelId;
	}

	//-------------------------------------------------------------------------------------------------
	public void setInputModelId(String inputModelId) {
		this.inputModelId = inputModelId;
	}

	//-------------------------------------------------------------------------------------------------
	public String getOutputModelId() {
		return outputModelId;
	}

	//-------------------------------------------------------------------------------------------------
	public void setOutputModelId(String outputModelId) {
		this.outputModelId = outputModelId;
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
