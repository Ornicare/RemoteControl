package com.ornilabs.remote.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class MyTextEdit extends EditText {

	public MyTextEdit(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyTextEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyTextEdit(Context context) {
		super(context);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			// Just ignore the [Enter] key
			return true;
		}
		// Handle all other keys in the default way
		return super.onKeyDown(keyCode, event);
	}

}
