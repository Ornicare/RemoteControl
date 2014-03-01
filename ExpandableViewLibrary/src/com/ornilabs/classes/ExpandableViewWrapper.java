package com.ornilabs.classes;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ExpandableListView;

import com.ornilabs.interfaces.ICategory;

public class ExpandableViewWrapper{


	private ExpandableListView list;
	private ExpandableListAdapter adapter;
	
	
	public ExpandableViewWrapper(Context context, List<ICategory> dataList) {
		list = new ExpandableListView(context);
		createView(context, dataList);
		list.setChildIndicator(null);
		
		adapter.notifyDataSetChanged();	
	}
	
	public View getView() {
		return list;
	}
	
	public void createView(Context context,List<ICategory> dataList) {
		adapter = new ExpandableListAdapter(context,dataList);
		list.setAdapter(adapter);		
	}

}
