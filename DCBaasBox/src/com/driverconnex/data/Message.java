package com.driverconnex.data;

/**
 * 
 * @author Yin Lee (SGI
 * @author Muhammad Azeem Anwar
 * 
 */

public class Message {
	private String id;
	private String title;
	private String subject;
	private String body;
	private String msgObjectId;
	private String createDate;
	private boolean isGlobal;
	private boolean isRead;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setMessageObjectId(String msgObjectId) {
		this.msgObjectId = msgObjectId;
	}

	public String getMessageObjectId() {
		return msgObjectId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String date) {
		this.createDate = date;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public boolean isGlobal() {
		return isGlobal;
	}

	public void setGlobal(boolean isGlobal) {
		this.isGlobal = isGlobal;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}
