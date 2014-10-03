package com.skillshot.android;

import java.util.ArrayList;

import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.skillshot.android.rest.model.LocationsList;
import com.skillshot.android.rest.request.LocationsRequest;
import com.skillshot.android.view.FilterDialogFragment;
import com.skillshot.android.view.FilterDialogFragment.FilterDialogListener;

public abstract class LocationsActivity extends BaseActivity implements FilterDialogListener, LocationListener {
	public static final String LOCATION_ID = "com.skillshot.android.LOCATION_ID";
	public static final String LOCATIONS_ARRAY = "com.skillshot.android.LOCATIONS_ARRAY";
	public static final String FILTER_ALL_AGES = "com.skillshot.android.FILTER_ALL_AGES";
	public static final String FILTER_SORT = "com.skillshot.android.FILTER_SORT";
	protected int filterSort = R.string.distance;
	protected boolean filterAllAges = false;
	private LocationsList locationsList;
	private Location userLocation = null;
	private LocationRequest locationUpdateParams;
	private static final int PREFERRED_UPDATE_INTERVAL_MS = 5000;
	private static final int FASTEST_UPDATE_INTERVAL_MS = 1000;
	public static final float MILES_PER_METER = (float) 0.000621371192;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(FILTER_ALL_AGES)) {
				filterAllAges = savedInstanceState.getBoolean(FILTER_ALL_AGES);
			}
			if (savedInstanceState.containsKey(FILTER_SORT)) {
				filterSort = savedInstanceState.getInt(FILTER_SORT);
			}
		}

		performRequest(new ListLocationsRequestListener());

		// However, if we're being restored from a previous state,
		// then we don't need to do anything and should return or else
		// we could end up with overlapping fragments.
		if (savedInstanceState != null) {
			return;
		}
	}

	/*
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		super.onConnected(dataBundle);

		locationUpdateParams = LocationRequest.create();
		locationUpdateParams.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationUpdateParams.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
		locationUpdateParams.setInterval(PREFERRED_UPDATE_INTERVAL_MS);
		mLocationClient.requestLocationUpdates(locationUpdateParams, this);
		
		setUserLocation(getLocation());
		if (getUserLocation() != null) {
		} else {
			Toast.makeText(this, R.string.location_unavailable, Toast.LENGTH_SHORT).show();
		}

	}
	
	public String numGamesString(int num) {
        String formatString = num == 1 
        		? getResources().getString(R.string.n_game) 
				: getResources().getString(R.string.n_games);
		return String.format(formatString, num);
	}
	
	public String userDistanceString(double latitude, double longitude) {
		return metersToMilesString(userDistance(latitude, longitude));
	}
	
	public String userDistanceString(com.skillshot.android.rest.model.Location location) {
		return metersToMilesString(userDistance(location));
	}
	
	private String metersToMilesString(float meters) {
		float distanceMiles = meters * MILES_PER_METER;
		String formatString = distanceMiles > 1 
				? distanceMiles >= 10
						? "%.0f"
						: "%.1f"
				: "%.2f";
		return String.format(formatString + " mi", distanceMiles);
	}
	
	public float userDistance(double latitude, double longitude) {
		float[] aDistance = new float[1];
		Location.distanceBetween(getUserLocation().getLatitude(),
				getUserLocation().getLongitude(), latitude, longitude, aDistance);
		return aDistance[0];
	}

	public float userDistance(com.skillshot.android.rest.model.Location location) {
		return userDistance(location.getLatitude(), location.getLongitude());
	}

	// Define the callback method that receives location updates
    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
    	setUserLocation(location);
    }
	
	public Location getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(Location userLocation) {
		this.userLocation = userLocation;
	}

	protected void performRequest(RequestListener<LocationsList> listener) {
		setProgressBarIndeterminateVisibility(true);

		LocationsRequest request = new LocationsRequest();
		String lastRequestCacheKey = request.createCacheKey();

		spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_WEEK, listener);
	}


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem logoutItem = menu.findItem(R.id.action_logout);
		MenuItem loginItem = menu.findItem(R.id.action_login);

		boolean showLoggedIn = isLoggedIn();
	    logoutItem.setVisible(showLoggedIn);
	    loginItem.setVisible(!showLoggedIn);

	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_filter:
			openFilterDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void openFilterDialog() {
		Bundle args = new Bundle();
		args.putBoolean(FILTER_ALL_AGES, filterAllAges);
		args.putInt(FILTER_SORT, filterSort);
		FilterDialogFragment dialog = new FilterDialogFragment();
		dialog.setArguments(args);
		dialog.show(getFragmentManager(), null);
	}
    

	@Override
	public void onFilterCheckboxes(ArrayList<String> filters) {
		filterAllAges = filters.contains(getResources().getString(R.string.all_ages));
		filter();
	}
	
	protected abstract void filter();

	private class ListLocationsRequestListener implements RequestListener<LocationsList> {

		@Override
		public void onRequestFailure(SpiceException e) {
			setProgressBarIndeterminateVisibility(false);
			Toast.makeText(
					getBaseContext(), 
					"Couldn't load data from the server. Please exit the app and try again.", 
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(LocationsList locationsList) {
			setProgressBarIndeterminateVisibility(false);
			setLocationsList(locationsList);
		}
	}

	public void setLocationsList(LocationsList locationsList) {
		this.locationsList = locationsList;
		onSetLocationsList();
	}
	
	public LocationsList getLocationsList() {
		return locationsList;
	}
	
	protected abstract void onSetLocationsList();

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(FILTER_ALL_AGES, filterAllAges);
		outState.putInt(FILTER_SORT, filterSort);
		super.onSaveInstanceState(outState);
	}

}
