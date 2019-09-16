package com.luee.wally.api.route;

public class Route {
	private final Controller controller;
	private final String method;
	
	public Route(Controller controller,String method) {
		this.controller=controller;
		this.method=method;
	}
	
	public Controller getController() {
		return controller;
	}
	
	public String getMethod() {
		return method;
	}
}
