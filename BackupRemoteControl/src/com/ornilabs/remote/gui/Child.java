package com.ornilabs.remote.gui;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Child extends View{

	private Context mContext;
	private Action action;

	public Child(Context mContext, Action action) {
		super(mContext);
		this.mContext = mContext;
		this.action = action;
	}
	
	public Child(Context mContext) {
		super(mContext);
		this.mContext = mContext;
		
	}

	public View create() {
		LinearLayout linear = new LinearLayout(mContext);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		
		TextView tv = new TextView(mContext);
		tv.setText(action.getName());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
		tv.setPadding(10, 0, 0, 0);
		
//		final CheckBox cb = new CheckBox(mContext);
//		cb.setChecked(task.isChecked());
//		linear.setPadding(20, 0, 0, 0);
//		linear.setGravity(Gravity.CENTER_VERTICAL);
//		
//
//		linear.addView(cb);
		linear.addView(tv);
		
		linear.setLongClickable(true);
		//linear.setClickable(true);
		
		//linear.setOnCreateContextMenuListener(this);
		
//		linear.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				cb.setChecked(!cb.isChecked());
//				//Toast.makeText(mContext, "Child !", Toast.LENGTH_LONG).show();
//			}
//		});
		
		//((Activity)mContext).registerForContextMenu(linear);
		
//		cb.setChecked(task.isChecked());
//		cb.setOnCheckedChangeListener(new MyOnCheckedChangeListener(mContext, task));
		
		return linear;
	}

//	public Task getTask() {
//		return task;
//	}

}
