package net.paypal.integrate.entity;

import com.google.cloud.Timestamp;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class RedeemingRequests {

	@Id
	private Long uuid;
	@Index
	private String amount;
	
	@Index
	private String email;
	
	@Index
	private String message;
	
	@Index
	private String from;
	
	@Index
	private String userGuid;
	
	@Index
	private boolean paid;
	
	@Index
	private Timestamp date;  /*this is the date where the request to redeem the money was created.*/
	
	@Index
	private String packageName;
	
	@Index
	private boolean confirmedEmail;
	
	@Index
	private String type;  /*"PayPal" / "Google Play" / "Amazon" */
	
	@Index
	private String countryCode;

	@Index
	private String fullName;
    
	@Index
	private String fullAddress;
	
	
	private Timestamp creationDate; /* this is the date when the user was created*/
	
	public RedeemingRequests() {
	   this.message="Congratulations! This is your reward from PlaySpot";
	   this.from="PlaySpot";	
	   this.packageName="com.moregames.makemoney";
	}

	public Long getUuid() {
		return uuid;
	}

	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}



	public String getUserGuid() {
		return userGuid;
	}

	public void setUserGuid(String userGuid) {
		this.userGuid = userGuid;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isConfirmedEmail() {
		return confirmedEmail;
	}

	public void setConfirmedEmail(boolean confirmedEmail) {
		this.confirmedEmail = confirmedEmail;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
	  StringBuilder sb=new StringBuilder();
	  sb.append("id="+uuid);
	  sb.append(",date="+date);
	  sb.append(",paid="+paid);
	  sb.append(",countryCode="+countryCode);
	  sb.append(",type="+type);
	  sb.append(",confirmedEmail="+confirmedEmail);
	  sb.append(",packageName="+packageName);
	  return sb.toString();
	}
}
