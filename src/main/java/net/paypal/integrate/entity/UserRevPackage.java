package net.paypal.integrate.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;


@Entity
public class UserRevPackage {
	@Id
	private Long id;

	@Index
	private String packageName;
	
	/*
	 * YYYY-MM-DD
	 */
	@Index
	private String lastUsedDate;

	public UserRevPackage() {
	
	}
	
	public UserRevPackage(String packageName,String date) {
		this.packageName=packageName;
		this.lastUsedDate=date;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getLastUsedDate() {
		return lastUsedDate;
	}

	public void setLastUsedDate(String lastUsedDate) {
		this.lastUsedDate = lastUsedDate;
	}
	
	
	
	
}
