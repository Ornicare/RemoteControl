package com.ornilabs.classes;

import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ornilabs.interfaces.ICategory;
import com.ornilabs.interfaces.IChild;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
	
	protected Context mContext;
	protected List<ICategory> dataList;

	public ExpandableListAdapter(Context mContext, List<ICategory> dataList) {
		super();
		this.dataList = dataList;
		this.mContext = mContext;
	}

	@Override
	public IChild getChild(int groupPosition, int childPosition) {
		return dataList.get(groupPosition).getChildList().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		IChild child = dataList.get(groupPosition).getChildList().get(childPosition);

		return child.getView(mContext, dataList.get(groupPosition));
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return dataList.get(groupPosition).getChildList().size();
	}

	@Override
	public ICategory getGroup(int groupPosition) {
		return dataList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return dataList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}
	
	@Override
	public boolean isEmpty() {
		return dataList.isEmpty();
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
		View convertView, ViewGroup parent) {
	
		TextView tv = new TextView(mContext);
		tv.setText(((ICategory)getGroup(groupPosition)).getText());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
		tv.setPadding(60, 0, 0, 0);
		tv.getHeight();
		
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