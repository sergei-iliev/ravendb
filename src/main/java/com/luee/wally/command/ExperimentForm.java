package com.luee.wally.command;

import javax.servlet.ServletRequest;

public class ExperimentForm implements WebForm {
	private String userGuid;
	private String experiment;

	public static ExperimentForm parse(ServletRequest req){
		ExperimentForm form=new ExperimentForm();
		form.setUserGuid(req.getParameter("userGuid"));
		form.setExperiment(req.getParameter("experiment"));
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

	public String getExperiment() {
		return experiment;
	}

	public void setExperiment(String experiment) {
		if(experiment==null||experiment.trim().length()==0){
			  this.experiment = null;
			}else{
			  this.experiment = experiment;
			}			
	}

	

}
