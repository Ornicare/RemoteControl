package com.ornilabs.remote;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ornilabs.classes.ExpandableListAdapter;
import com.ornilabs.interfaces.ICategory;
import com.ornilabs.remote.gui.Action;
import com.ornilabs.remote.gui.Client;
import com.ornilabs.server.Status;

public class ExpandableListAdapterExtend extends ExpandableListAdapter {

	private ExpandableListView adapted;

	public ExpandableListAdapterExtend(Context mContext,
			List<ICategory> dataList, ExpandableListView list) {
		super(mContext, dataList);
		this.adapted = list;
	}

	// @Override
	// public View getGroupView(int groupPosition, boolean isExpanded,
	// View convertView, ViewGroup parent) {
	// //
	// LinearLayout linear = new LinearLayout(mContext);
	// linear.setOrientation(LinearLayout.HORIZONTAL);
	// // linear.setLongClickable(true);
	// //
	// TextView tv = new TextView(mContext);
	// tv.setText(((ICategory) getGroup(groupPosition)).getText());
	// tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
	// tv.setPadding(60, 0, 0, 0);
	//
	// Log.i("fdsfd", "d" + dataList + "j" + dataList.get(groupPosition) + "g"
	// + ((Client) dataList.get(groupPosition)).getState());
	// Status status = ((Client) dataList.get(groupPosition)).getState();
	// status = status == null ? Status.Deconnected : status;
	// switch (status) {
	// case Deconnected:
	// tv.setBackgroundResource(R.drawable.red);
	// adapted.collapseGroup(groupPosition);
	// adapted.setOnGroupClickListener(new
	// ExpandableListView.OnGroupClickListener() {
	// @Override
	// public boolean onGroupClick(ExpandableListView parent, View v,
	// int groupPosition, long id) {
	// // Doing nothing
	// return true;
	// }
	// });
	// break;
	// case Connected:
	// tv.setBackgroundResource(R.drawable.notlinked);
	// makeClickable();
	// break;
	// case ConnectedAndLinked:
	// tv.setBackgroundResource(R.drawable.green);
	// makeClickable();
	// break;
	// default:
	// break;
	// }
	//
	// linear.addView(tv);
	// return linear;
	// }

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		Log.e("ffsd",""+RemoteLaunch.showNotLinkedClients);
		try {
			View view = convertView;
			TextView text = null;
			ImageView image = null;
		
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.expandable_list_group_view, parent, false);
			}

			text = (TextView) view.findViewById(R.id.groupHeader);
			image = (ImageView) view.findViewById(R.id.expandableIcon);

			StringBuilder title = new StringBuilder();

	
			title.append(((ICategory) getGroup(groupPosition)).getText());
			text.setText(title.toString());

//			title.append(" (");
//			title.append(((ICategory) getGroup(groupPosition)).getChildList()
//					.size());
//			title.append(")");


			/*
			 * if this is not the first group (future travel) show the arrow image
			 * and change state if necessary
			 */
			// if(groupPosition != 0){
			int imageResourceId = isExpanded ? android.R.drawable.ic_menu_revert
					: android.R.drawable.ic_input_add;
			image.setImageResource(imageResourceId);

			 
			// } else {
			// image.setVisibility(View.INVISIBLE);
			// }

			Status status = ((Client) dataList.get(groupPosition)).getState();
			status = status == null ? Status.Deconnected : status;
			
			switch (status) {
			case Deconnected:
				text.setBackgroundResource(R.drawable.red);
				adapted.collapseGroup(groupPosition);
//				adapted.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//					@Override
//					public boolean onGroupClick(ExpandableListView parent, View v,
//							int groupPosition, long id) {
//						// Doing nothing
//						return true;
//					}
//				});
				image.setImageResource(android.R.drawable.ic_dialog_alert);
				image.setVisibility(View.VISIBLE);
				break;
			case Connected:
				text.setBackgroundResource(R.drawable.notlinked);
				makeClickable(image);
				break;
			case ConnectedAndLinked:
				text.setBackgroundResource(R.drawable.green);
				makeClickable(image);
				break;
			default:
				break;
			}

			return view;
		}
		catch(Exception e) {
			e.printStackTrace();
			 return new FrameLayout(mContext);
		}
		
	}

	private void makeClickable(View image) {
		image.setVisibility(View.VISIBLE);
//		adapted.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//			@Override
//			public boolean onGroupClick(ExpandableListView parent, View v,
//					int groupPosition, long id) {
//				parent.smoothScrollToPosition(groupPosition);
//
//				if (parent.isGroupExpanded(groupPosition)) {
//					parent.collapseGroup(groupPosition);
//				} else {
//					parent.expandGroup(groupPosition);
//				}
//
//				return true;
//			}
//		});
	}
}
