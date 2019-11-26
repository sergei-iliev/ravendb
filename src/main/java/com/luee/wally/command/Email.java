package com.luee.wally.command;

public class Email {
	private String to;
	private String from;
	private String subject;
	private String content;
	private String toName;
	private String fromName;

	private String cc;
	private String ccName;

	private Attachment attachment;
	
	public String getCC() {
		return cc;
	}
	public void setCC(String cc) {
		this.cc = cc;
	}

	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getCCName() {
		return ccName;
	}
	public void setCCName(String ccName) {
		this.ccName = ccName;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Attachment getAttachment() {
		return attachment;
	}
	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}
	
	public String getToName() {
		return toName;
	}
	public void setToName(String toName) {
		this.toName = toName;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	
}
