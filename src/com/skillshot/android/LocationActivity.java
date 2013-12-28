package com.skillshot.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.skillshot.android.rest.model.Location;
import com.skillshot.android.rest.request.LocationRequest;
import com.skillshot.android.view.LocationFragment;
import com.skillshot.android.view.SpinnerFragment;

public class LocationActivity extends BaseActivity {

	public static final String LOCATION = "com.skillshot.android.LOCATION";
	private Location location = null;
	private String locationId = null;

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
	    locationId = intent.getStringExtra(MainActivity.LOCATION_ID);
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

		spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_DAY, new LocationRequestListener());
	}

	private class LocationRequestListener implements RequestListener<Location> {
		@Override
		public void onRequestFailure(SpiceException e) {
			setProgressBarIndeterminateVisibility(false);
			Toast.makeText(
					getBaseContext(), 
					"Couldn't load data from the server. Please exit the app and try again.", 
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(Location location) {
			setProgressBarIndeterminateVisibility(false);
			setLocation(location);
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
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(APPTAG, "Preparing options menu");
		MenuItem addGameItem = menu.findItem(R.id.action_add_game);

		boolean showLoggedIn = isLoggedIn();
		Log.d(APPTAG, String.format("User is logged in? %s", showLoggedIn));
	    addGameItem.setVisible(showLoggedIn);

	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_add_game:
			Intent intent = new Intent(getBaseContext(), AddGameActivity.class);
			intent.putExtra(MainActivity.LOCATION_ID, locationId);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onMapButtonClick(View view) {
		if (location == null) {
			Toast.makeText(this, "Location data could not be loaded :-(", Toast.LENGTH_SHORT).show();
			return;
		}
		String uriBegin = "geo:0,0";
		String query = location.getAddress() + ' ' + location.getCity() + ' ' + location.getPostal_code();
		String encodedQuery = Uri.encode(query);
		String uriString = uriBegin + "?q=" + encodedQuery;
		Uri uri = Uri.parse(uriString);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
		startActivity(intent);	
	}

	public void onCallButtonClick(View view) {
		if (location == null) {
			Toast.makeText(this, "Location data could not be loaded :-(", Toast.LENGTH_SHORT).show();
			return;
		}
		Uri uri = Uri.fromParts("tel", location.getPhone(), null);
		Intent intent = new Intent(android.content.Intent.ACTION_DIAL, uri);
		startActivity(intent);	
	}
	
}
