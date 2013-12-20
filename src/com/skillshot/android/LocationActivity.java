package com.skillshot.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.skillshot.android.rest.model.Location;
import com.skillshot.android.rest.request.LocationRequest;
import com.skillshot.android.view.LocationFragment;
import com.skillshot.android.view.SpinnerFragment;

public class LocationActivity extends BaseActivity {

	public static final String LOCATION = "com.skillshot.android.LOCATION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
	    String locationId = intent.getStringExtra(MainActivity.LOCATION_ID);
	    performRequest(locationId);

	    // However, if we're being restored from a previous state,
		// then we don't need to do anything and should return or else
		// we could end up with overlapping fragments.
		if (savedInstanceState != null) {
			return;
		}
	    
		// Create a new Fragment to be placed in the activity layout
	 	SpinnerFragment firstFragment = new SpinnerFragment();
	 	getFragmentManager().beginTransaction().add(R.id.container, firstFragment).commit();
	 	
	}

	private void performRequest(String locationId) {
		setProgressBarIndeterminateVisibility(true);

		LocationRequest request = new LocationRequest(locationId);
		String lastRequestCacheKey = request.createCacheKey();

		// XXX Set cache timeout correctly (one week?)
		spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ALWAYS_EXPIRED, new LocationRequestListener());
	}

	private class LocationRequestListener implements RequestListener<Location> {
		@Override
		public void onRequestFailure(SpiceException e) {
			setProgressBarIndeterminateVisibility(false);
			//XXX show error message
		}

		@Override
		public void onRequestSuccess(Location location) {
			setProgressBarIndeterminateVisibility(false);
			LocationFragment locationFragment = new LocationFragment();
			Bundle args = new Bundle();
			args.putSerializable(LOCATION, location);
			locationFragment.setArguments(args);
			getFragmentManager().beginTransaction().replace(R.id.container, locationFragment).commit();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
