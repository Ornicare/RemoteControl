package com.ornilabs.interfaces;

import java.util.List;

public interface ICategory {

	List<IChild> getChildList();

	String getText();
	
	String getUUID();
	
	boolean isUnwrapped();

	void setUnwrapped(boolean value);
}
