package com.ornilabs.classes;

import android.content.Context;
import android.view.View;

import com.ornilabs.interfaces.ICategory;
import com.ornilabs.interfaces.IChild;
import com.ornilabs.interfaces.IChildView;

public abstract class ChildView extends View implements IChildView{

	protected Context mContext;
	protected IChild childObject;

	public ChildView(Context mContext, IChild iChild) {
		super(mContext);
		this.mContext = mContext;
		this.childObject = iChild;
	}
	
	public ChildView(Context mContext) {
		super(mContext);
		this.mContext = mContext;
		
	}

	public abstract View create(ICategory motherCathegory);
}
