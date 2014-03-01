package com.ornilabs.remote;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.ornilabs.remote.gui.Client;
import com.ornilabs.remote.gui.MyExpandableListAdapter;

public class RemoteLaunch extends Activity {

	private ExpandableListView list;
	private RemoteLaunch context;
	private SharedPreferences shared;
	private MyExpandableListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		shared = PreferenceManager.getDefaultSharedPreferences(this);

		this.context = this;


		list = new ExpandableListView(this);
		restoreState();
		list.setChildIndicator(null);
		
		adapter.notifyDataSetChanged();
		
		
		setContentView(list);
//		setContentView(R.layout.activity_remote_launch);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.remote_launch, menu);
		return true;
	}
	
	public void restoreState() {
		String s = null;
		try {
			s = shared.getString("main", null);
		} catch (Throwable e) {
			e.printStackTrace();

		}
		finally {
//			categoryManager = new CategoryManager(this);
		}
//		
//		if (s != null){
//			try {
//				categoryManager.deserialize(s);
//			}
//			catch(Exception e) {
//				//In case of corrupted data, recreate them.
//				categoryManager = new CategoryManager(this);
//			}
//		}
		//Log.i("s",s);adapter = new MyExpandableListAdapter(this,
		
		ArrayList<Client> test = new ArrayList<Client>();
		test.add(new Client());
		adapter = new MyExpandableListAdapter(this,
				test);
		list.setAdapter(adapter);		
		
		
		//save state of collapse
//		list.setOnGroupCollapseListener(new OnGroupCollapseListener() {
//
//			@Override
//			public void onGroupCollapse(int groupPosition) {
//				categoryManager.getCategoriesList().get(groupPosition)
//						.setUnwrapped(false);
//				//saveState();
//				adapter.notifyDataSetChanged();
//			}
//		});
//
//		list.setOnGroupExpandListener(new OnGroupExpandListener() {
//
//			@Override
//			public void onGroupExpand(int groupPosition) {
//				categoryManager.getCategoriesList().get(groupPosition)
//						.setUnwrapped(true);
//				//saveState();
//				adapter.notifyDataSetChanged();
//			}			
//		});

//		for (int i = 0; i < categoryManager.getCategoriesList().size(); i++) {
//			if (categoryManager.getCategoriesList().get(i).isUnwrapped())
//				list.expandGroup(i);
//		}
	}

}
