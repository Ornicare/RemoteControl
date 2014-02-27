package com.ornilabs.remote;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class RemoteLaunch extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_launch);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.remote_launch, menu);
		return true;
	}

}
