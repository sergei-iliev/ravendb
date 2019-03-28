package net.paypal.integrate.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;


public class Email {
	@NotNull
	@Size(min=1)
	private String to;
	
	private String from="sergei.iliev@gmail.com";
	
	@NotNull
	@Size(min=1)
	private String subject;
	@NotNull
	@Size(min=1)
	private String content;

	private Attachment attachment;
	
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
	
	
}
