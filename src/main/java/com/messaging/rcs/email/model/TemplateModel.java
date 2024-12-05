package com.messaging.rcs.email.model;

public class TemplateModel {
	private Long id;
	private String templateCode;
	private String templateJson;
	private String templateType;
	private String templateMsgType;
	private Long templateUserId;
	private String botId;
	private String templateCustomParam;
	private String sms_dlt_principle_id;
	private String sms_dlt_content_id;
	private String sms_senderId;
	private String sms_contentId;
	private String sms_content;
	private String msgType;
	private String rcsMsgTypeId;
	private String operatorResponse;
	private String approveResponse;

	public String getOperatorResponse() {
		return operatorResponse;
	}

	public void setOperatorResponse(String operatorResponse) {
		this.operatorResponse = operatorResponse;
	}

	public String getApproveResponse() {
		return approveResponse;
	}

	public void setApproveResponse(String approveResponse) {
		this.approveResponse = approveResponse;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getRcsMsgTypeId() {
		return rcsMsgTypeId;
	}

	public void setRcsMsgTypeId(String rcsMsgTypeId) {
		this.rcsMsgTypeId = rcsMsgTypeId;
	}

	public String getSms_dlt_principle_id() {
		return sms_dlt_principle_id;
	}

	public void setSms_dlt_principle_id(String sms_dlt_principle_id) {
		this.sms_dlt_principle_id = sms_dlt_principle_id;
	}

	public String getSms_dlt_content_id() {
		return sms_dlt_content_id;
	}

	public void setSms_dlt_content_id(String sms_dlt_content_id) {
		this.sms_dlt_content_id = sms_dlt_content_id;
	}

	public String getSms_senderId() {
		return sms_senderId;
	}

	public void setSms_senderId(String sms_senderId) {
		this.sms_senderId = sms_senderId;
	}

	public String getSms_contentId() {
		return sms_contentId;
	}

	public void setSms_contentId(String sms_contentId) {
		this.sms_contentId = sms_contentId;
	}

	public String getSms_content() {
		return sms_content;
	}

	public void setSms_content(String sms_content) {
		this.sms_content = sms_content;
	}

	public String getBotId() {
		return botId;
	}

	public void setBotId(String botId) {
		this.botId = botId;
	}

	public String getTemplateCustomParam() {
		return templateCustomParam;
	}

	public void setTemplateCustomParam(String templateCustomParam) {
		this.templateCustomParam = templateCustomParam;
	}

	public Long getTemplateUserId() {
		return templateUserId;
	}

	public void setTemplateUserId(Long templateUserId) {
		this.templateUserId = templateUserId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getTemplateJson() {
		return templateJson;
	}

	public void setTemplateJson(String templateJson) {
		this.templateJson = templateJson;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getTemplateMsgType() {
		return templateMsgType;
	}

	public void setTemplateMsgType(String templateMsgType) {
		this.templateMsgType = templateMsgType;
	}

	@Override
	public String toString() {
		return "TemplateModel [id=" + id + ", templateCode=" + templateCode + ", templateJson=" + templateJson
				+ ", templateType=" + templateType + ", templateMsgType=" + templateMsgType + ", templateUserId="
				+ templateUserId + ", botId=" + botId + ", templateCustomParam=" + templateCustomParam
				+ ", sms_dlt_principle_id=" + sms_dlt_principle_id + ", sms_dlt_content_id=" + sms_dlt_content_id
				+ ", sms_senderId=" + sms_senderId + ", sms_contentId=" + sms_contentId + ", sms_content=" + sms_content
				+ ", msgType=" + msgType + ", rcsMsgTypeId=" + rcsMsgTypeId + "]";
	}
}
