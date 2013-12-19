package com.skillshot.android;

import android.app.Activity;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

public class BaseActivity extends Activity {
//	public final static String ENDPOINT = "http://list.skill-shot.com"; 
	public final static String ENDPOINT = "http://172.16.5.176:5000"; 
	protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);

	@Override
	protected void onStart() {
	  super.onStart();
	  spiceManager.start(this);
	}

	@Override
	protected void onStop() {
	  spiceManager.shouldStop();
	  super.onStop();
	}


}
