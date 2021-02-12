package com.luee.wally.command;

import javax.servlet.ServletRequest;

public class UnremoveUserForm implements WebForm {
	private String userGuid;
	private String removalReason;

	public static UnremoveUserForm parse(ServletRequest req){
		UnremoveUserForm form=new UnremoveUserForm();
		form.setUserGuid(req.getParameter("userGuid"));
		form.setRemovalReason(req.getParameter("removalReason"));
		return form;
	}

	public String getUserGuid() {
		return userGuid;
	}

	public void setUserGuid(String userGuid) {
		if(userGuid==null||userGuid.trim().length()==0){
		  this.userGuid = null;
		}else{
		  this.userGuid = userGuid;
		}
	}

	public String getRemovalReason() {
		return removalReason;
	}

	public void setRemovalReason(String removalReason) {
		if(removalReason==null||removalReason.trim().length()==0){
			  this.removalReason = null;
			}else{
			  this.removalReason = removalReason;
			}			
	}

	

}
