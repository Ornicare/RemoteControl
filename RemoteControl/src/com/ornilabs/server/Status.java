package com.ornilabs.server;

public enum Status {
	Connected("Connected"),
	Deconnected("Deconnected"),
	ConnectedAndLinked("Linked");
	
	private String name;

	private Status(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
