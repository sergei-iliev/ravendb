package com.luee.wally.entity;

import java.io.Serializable;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public class User implements Serializable{
	public enum UserRole{
		ADMIN_ROLE,
		QA_ROLE;
	}
	private Key key;
	private String email;
	private String password;
	private UserRole userRole;
	
	public static User valueOf(Entity entity) {
	   User user=new User();
	   user.key=entity.getKey();
	   user.email=(String)entity.getProperty("email");
	   user.password=(String)entity.getProperty("password");	   
	   user.userRole=UserRole.valueOf((String)entity.getProperty("role"));
	   return user;
	}
	
	public boolean isAdmin(){
		return userRole==UserRole.ADMIN_ROLE;
	}
	
	public boolean hasRole(UserRole userRole){
		return userRole==userRole;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}
	
	
	
	
	
	
}
