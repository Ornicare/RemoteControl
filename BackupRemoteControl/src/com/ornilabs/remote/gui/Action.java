package com.ornilabs.remote.gui;

import android.content.Context;

import com.ornilabs.interfaces.IChild;
import com.ornilabs.interfaces.IChildView;

public enum Action implements IChild{
	TEST("Test")
	;

	private String name;

	private Action(String name) {
		this.name = name;
	}
	
	public CharSequence getName() {
		return name;
	}

	@Override
	public IChildView getView(Context mContext) {
		// TODO Auto-generated method stub
		return null;
	}

}
