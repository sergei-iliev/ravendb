package com.luee.wally.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

public class CampaignSearchForm implements WebForm{
	private Date startDate, endDate;
	private String countryCode;
	private Collection<String> sources=new HashSet<>();
	private Collection<String> adNetworks=new HashSet<>();
	private Collection<String> campaigns=new HashSet<>();
	private String packageName;
	private Double minRevThreshold; 
	
	public  static CampaignSearchForm parse(ServletRequest req) throws ServletException{
		CampaignSearchForm form=new CampaignSearchForm();
		form.setStartDate(form.parseDate(req.getParameter("startDate")));
		form.setEndDate(form.parseDate(req.getParameter("endDate")));
		form.setCountryCode((req.getParameter("country").length() == 0 ? null : req.getParameter("country")));
		form.setPackageName((req.getParameter("packageName").length() == 0 ? null : req.getParameter("packageName")));
		
		form.minRevThreshold=(((req.getParameter("minRevThreshold").length() == 0 ? null : Double.parseDouble(req.getParameter("minRevThreshold")))));
		
		if(req.getParameterValues("adnetworks")!=null){
			form.setAdNetworks(req.getParameterValues("adnetworks"));
		}
		if(req.getParameterValues("campaigns")!=null){
			form.setCompaigns(req.getParameterValues("campaigns"));
		}
		if(req.getParameterValues("sources")!=null){
			form.setSources(req.getParameterValues("sources"));
		}
		
		
		
		return form;
	}

	public String getEndDateAsText() {
		if(endDate!=null){
			return formatedDate(endDate, "yyyy-MM-dd");
		}		
		return null;
	}
	public String getStartDateAsText() {
		if(startDate!=null){
			return formatedDate(startDate, "yyyy-MM-dd");
		}
		
		return null;
	}
	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public String getCountryCode() {
		return countryCode;
	}


	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}


	public Collection<String> getSources() {
		return sources;
	}


	public void setSources(String[] sources) {
		this.sources.addAll(Arrays.asList(sources));
	}

	public String getSourcesFirst() {
		if(sources.size()==0){
			return null;
		}else{
			return sources.iterator().next();
		}
	}
	
	public Collection<String> getAdNetworks() {
		return adNetworks;
	}


	public void setAdNetworks(String[] adNetworks) {
		this.adNetworks.addAll(Arrays.asList(adNetworks));
	}
	public String getAdNetworksFirst() {
		if(adNetworks.size()==0){
			return null;
		}else{
			return adNetworks.iterator().next();
		}
	}


	public Collection<String> getCampaigns() {
		return campaigns;
	}

	public String getCampaignsFirst() {
		if(campaigns.size()==0){
			return null;
		}else{
			return campaigns.iterator().next();
		}
	}
	public void setCompaigns(String[] campaigns) {
		this.campaigns.addAll(Arrays.asList(campaigns));
	}


	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public void setMinRevThreshold(Double minRevThreshold) {
		this.minRevThreshold = minRevThreshold;
	}
	public Double getMinRevThreshold() {
		return minRevThreshold;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("startDate:"+(startDate==null?"":startDate)+"\r\n");
		sb.append("endDate:"+(endDate==null?"":endDate)+"\r\n");
		sb.append("country:"+(countryCode==null?"":countryCode)+"\r\n");
		sb.append("packageName:"+(packageName==null?"":packageName)+"\r\n");
		sb.append("minRevThreshold:"+minRevThreshold+"\r\n");
		sb.append("Ad Network:"+adNetworks+"\r\n");
		sb.append("Source:"+sources+"\r\n");
		sb.append("Campaign:"+campaigns +"\r\n");
		return sb.toString();
	}
	
}
