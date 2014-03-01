package com.ornilabs.remote.gui;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ornilabs.classes.ChildView;
import com.ornilabs.interfaces.ICategory;
import com.ornilabs.interfaces.IChild;
import com.ornilabs.interfaces.IChildView;
import com.ornilabs.remote.RemoteLaunch;

public enum Action implements IChild {
	LINK("Link"), STOP("Stop"), LOCK("Lock");

	private String name;
	private Action type;

	private Action(String name) {
		this.name = name;
		this.type = this;
	}

	public CharSequence getName() {
		return name;
	}

	@Override
	public View getView(Context mContext, ICategory motherCathegory) {
		return new ActionChildView(mContext, this).create(motherCathegory);
	}

	private class ActionChildView extends ChildView implements IChildView {

		public ActionChildView(Context mContext, IChild iChild) {
			super(mContext, iChild);
		}

		@Override
		public View create(final ICategory motherCathegory) {
			LinearLayout linear = new LinearLayout(mContext);
			linear.setOrientation(LinearLayout.HORIZONTAL);

			TextView tv = new TextView(mContext);
			tv.setText(name);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			tv.setPadding(10, 0, 0, 0);
			linear.addView(tv);

			Button button = new Button(mContext);
			button.setText(getName());
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						switch (type) {
						case LINK:

							((RemoteLaunch) mContext).linkWithDevice(motherCathegory.getText()+":"+motherCathegory.getUUID());

							break;
						case STOP:
							((RemoteLaunch) mContext)
									.sendCommandToDevice("shutdown -s -t 00", motherCathegory.getText()+":"+motherCathegory.getUUID());
							break;
						case LOCK:
							((RemoteLaunch) mContext)
									.sendCommandToDevice("rundll32.exe user32.dll, LockWorkStation", motherCathegory.getText()+":"+motherCathegory.getUUID());
							break;
						}
					} catch (IOException e) {
						Log.e("Action", "Cannot execute action "+name);
						e.printStackTrace();
					}

				}
			});

			linear.addView(button);

			linear.setLongClickable(true);
			return linear;
		}
	}

}
