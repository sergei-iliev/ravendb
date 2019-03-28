package net.paypal.integrate.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;


public class PayoutForm {

	@NotEmpty
	private String currency;
	@NotEmpty
	private String amount;
	
	@NotEmpty
	private String receiver;
	
	private Attachment attachment;
	
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public Attachment getAttachment() {
		return attachment;
	}
	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}
	public List<String> getCurrencyList() {
        List<String> list = new ArrayList<>();
        list.add("EUR");
        list.add("USD");
        list.add("GBR");
        list.add("CAD");
        list.add("CHF");
        return list;
    }
	
}
