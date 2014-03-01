package com.ornilabs.remote.gui;

import java.util.ArrayList;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	
	private ArrayList<Client> list;

	public MyExpandableListAdapter(Context context, ArrayList<Client> list) {
		super();
		this.list = list;
		mContext = context;
	}

	@Override
	public Action getChild(int groupPosition, int childPosition) {
		return list.get(groupPosition).getControlPanel().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		
		return new Child(mContext, list.get(groupPosition).getControlPanel().get(childPosition)).create();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return list.get(groupPosition).getControlPanel().size();
	}

	@Override
	public Client getGroup(int groupPosition) {
		return list.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return list.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}
	
	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
		View convertView, ViewGroup parent) {
	
		TextView tv = new TextView(mContext);
		tv.setText(((Client)getGroup(groupPosition)).getName());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
		tv.setPadding(60, 0, 0, 0);
		tv.getHeight();
		
		int nb = getChildrenCount(groupPosition);
		int sum = 0;
//		for(String action: list.get(groupPosition).getControlPanel().keySet()){
//			sum += task.isChecked()?1:0;
//		}
//		sum = nb==0?0:(10*sum)/nb;
//		switch(sum){
//		//0 - 10
//		case 0:
//			tv.setBackgroundResource(0);
//			break;
//		case 1:
//			tv.setBackgroundResource(R.drawable.i10);
//			break;
//		case 2:
//			tv.setBackgroundResource(R.drawable.i20);
//			break;
//		case 3:
//			tv.setBackgroundResource(R.drawable.i30);
//			break;
//		case 4:
//			tv.setBackgroundResource(R.drawable.i40);
//			break;
//		case 5:
//			tv.setBackgroundResource(R.drawable.i50);
//			break;
//		case 6:
//			tv.setBackgroundResource(R.drawable.i60);
//			break;
//		case 7:
//			tv.setBackgroundResource(R.drawable.i70);
//			break;
//		case 8:
//			tv.setBackgroundResource(R.drawable.i80);
//			break;
//		case 9:
//			tv.setBackgroundResource(R.drawable.i90);
//			break;
//		case 10:
//			tv.setBackgroundResource(R.drawable.i100);
//			break;
//		default:
//			tv.setBackgroundResource(0);
//		}
		
		return tv;
	}
	
	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}