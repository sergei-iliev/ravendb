package com.luee.wally.command;

import javax.servlet.ServletRequest;

public class DeleteUserDataForm implements WebForm {
	private String searchOption;
	private String input;

	public static DeleteUserDataForm parse(ServletRequest req){
		DeleteUserDataForm form=new DeleteUserDataForm();
		form.searchOption= req.getParameter("searchOption");
		form.input= req.getParameter("input");
		return form;
	}

	public String getSearchOption() {
		return searchOption;
	}

	public void setSearchOption(String searchOption) {
		this.searchOption = searchOption;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
	

}
