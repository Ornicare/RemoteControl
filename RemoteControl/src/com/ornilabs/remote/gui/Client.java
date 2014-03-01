package com.ornilabs.remote.gui;

import java.util.ArrayList;
import java.util.List;

import com.ornilabs.interfaces.ICategory;
import com.ornilabs.interfaces.IChild;
import com.ornilabs.server.Status;

public class Client implements ICategory{
	
	
	private String clientName;
	private List<IChild> childList;
	private String uuid;
	private Status state;
	private boolean isUnwrapped;

	public Client(String clientNameS, List<IChild> childList) {
		String[] splitClientName = clientNameS.split(":");
		this.clientName = splitClientName[0];
		this.uuid = splitClientName[1];
		this.childList = childList;
		this.state = Status.Deconnected;
	}

	@Override
	public List<IChild> getChildList() {
		return childList;
	}

	@Override
	public String getText() {
		return clientName;
	}

	public Status getState() {
		return this.state;
	}
	
	public void setState(Status state) {
		if(this.state==Status.ConnectedAndLinked && state==Status.Connected) return;
		this.state = state;
	}

	@Override
	public String getUUID() {
		return uuid;
	}

	public void changeChildList(ArrayList<IChild> childList2) {
		this.childList = childList2;
	}

	@Override
	public boolean isUnwrapped() {
		return isUnwrapped;
	}

	@Override
	public void setUnwrapped(boolean value) {
		isUnwrapped = value;
	}

}
