package com.ornilabs.interfaces;

import android.content.Context;
import android.view.View;


public interface IChild {

	View getView(Context mContext, ICategory motherCathegory);

}
