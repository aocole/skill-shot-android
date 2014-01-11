package com.skillshot.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.skillshot.android.rest.model.LocationsList;
import com.skillshot.android.rest.request.LocationsRequest;
import com.skillshot.android.view.FilterDialogFragment;
import com.skillshot.android.view.FilterDialogFragment.FilterDialogListener;

public class MapActivity extends BaseActivity implements LocationListener, FilterDialogListener {
	public static final String LOCATIONS_ARRAY = "com.skillshot.android.LOCATIONS_ARRAY";
	public static final String LOCATION_ID = "com.skillshot.android.LOCATION_ID";
	public static final String MAP_STATE = "com.skillshot.android.MAP_STATE";
	public static final String DEFAULT_AREA_ID = "seattle";
	private final int SKILL_SHOT_YELLOW = 42;
	private final String MAP_TAG = "com.skillshot.android.MAP_TAG";
	private GoogleMap mMap;
	private Map<Marker, com.skillshot.android.rest.model.Location> allMarkersMap = new HashMap<Marker, com.skillshot.android.rest.model.Location>();
	private Location userLocation = null;
	private LocationRequest locationUpdateParams;
	private static final int PREFERRED_UPDATE_INTERVAL_MS = 5000;
	private static final int FASTEST_UPDATE_INTERVAL_MS = 1000;
	public static final float MILES_PER_METER = (float) 0.000621371192;
	private static final float DEFAULT_ZOOM = 15;
	public static double SHORTYS_LAT = 47.613834;
	public static double SHORTYS_LONG = -122.345043;
	
	public static final String FILTER_ALL_AGES = "com.skillshot.android.FILTER_ALL_AGES";
	private boolean filterAllAges = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		Log.d(APPTAG, "onCreate");
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(FILTER_ALL_AGES)) {
				filterAllAges = savedInstanceState.getBoolean(FILTER_ALL_AGES);
			}
		}

		performRequest(this, spiceManager, new ListLocationsRequestListener());

		// However, if we're being restored from a previous state,
		// then we don't need to do anything and should return or else
		// we could end up with overlapping fragments.
		if (savedInstanceState != null) {
			return;
		}

		MapFragment firstFragment = new MapFragment();
		getFragmentManager().beginTransaction().add(R.id.container, firstFragment, MAP_TAG).commit();		
	}	
	
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(FILTER_ALL_AGES, filterAllAges);
		super.onSaveInstanceState(outState);
	}



	@Override
	protected void onStart() {
		super.onStart();
		mMap = ((MapFragment) getFragmentManager().findFragmentByTag(MAP_TAG)).getMap();
		mMap.setMyLocationEnabled(true);
		GoogleMap.OnInfoWindowClickListener infoListener = new LocationClickListener();
		mMap.setOnInfoWindowClickListener(infoListener);
		GoogleMap.OnMarkerClickListener markerListener = new MarkerClickListener();
		mMap.setOnMarkerClickListener(markerListener);
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
		
		userLocation = getLocation();
		if (userLocation != null) {
//			LatLng pos = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
//			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM));
//			Toast.makeText(this, "Found you!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Your location could not be determined :-(", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(APPTAG, "Preparing options menu");
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
		case R.id.action_list:
			openList();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void openFilterDialog() {
		Bundle args = new Bundle();
		args.putBoolean(FILTER_ALL_AGES, filterAllAges);
		FilterDialogFragment dialog = new FilterDialogFragment();
		dialog.setArguments(args);
		dialog.show(getFragmentManager(), null);
	}
    
	private void openList() {
		Intent intent = new Intent(this, LocationListActivity.class);
		startActivity(intent);
	}
    
	public static void performRequest(Activity activity, SpiceManager spiceManager, RequestListener<LocationsList> listener) {
		activity.setProgressBarIndeterminateVisibility(true);

		LocationsRequest request = new LocationsRequest();
		String lastRequestCacheKey = request.createCacheKey();

		spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ALWAYS_EXPIRED, listener);
	}

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
			allMarkersMap.clear();
			for(com.skillshot.android.rest.model.Location loc : locationsList) {
				LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
				Marker marker = mMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title(loc.getName())
				.icon(BitmapDescriptorFactory.defaultMarker(SKILL_SHOT_YELLOW))
				);
				allMarkersMap.put(marker, loc);
			}
			filter();
		}
	}
	
	private void filter() {
		for(Marker marker : allMarkersMap.keySet()) {
			com.skillshot.android.rest.model.Location loc = (com.skillshot.android.rest.model.Location) allMarkersMap.get(marker);
			boolean visible = 
					(!filterAllAges || loc.isAll_ages()) // either we're not filtering or the location is all ages (if we are filtering)
					;
			marker.setVisible(visible);
		}
	}
	
	// Define the callback method that receives location updates
    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
    	userLocation = location;
    }
	
	private class MarkerClickListener implements GoogleMap.OnMarkerClickListener {
		@Override
		public boolean onMarkerClick(Marker marker) {
			if(userLocation != null) {
				Log.d(APPTAG, String.format("userLocation is %f, %f", userLocation.getLatitude(), userLocation.getLongitude()));
				Log.d(APPTAG, String.format("marker Position is %f, %f", marker.getPosition().latitude, marker.getPosition().longitude));
				float[] aDistance = new float[1];
				Location.distanceBetween(
						userLocation.getLatitude(), userLocation.getLongitude(), 
						marker.getPosition().latitude, marker.getPosition().longitude,
						aDistance);
				float distanceMiles = aDistance[0] * MILES_PER_METER; 
				marker.setSnippet(String.format("%.2f mi", distanceMiles));
			}
			return false;
		}
	}
	
	private class LocationClickListener implements GoogleMap.OnInfoWindowClickListener {
		@Override
		public void onInfoWindowClick(Marker marker) {
			String locationId = allMarkersMap.get(marker).getId();
			Intent intent = new Intent(getBaseContext(), LocationActivity.class);
			intent.putExtra(LOCATION_ID, locationId);
			startActivity(intent);
		}
	}

	@Override
	protected void onPause() {
		CameraPosition cam = mMap.getCameraPosition();
		double longitude = cam.target.longitude;
		double latitude = cam.target.latitude;
		float zoom = cam.zoom;
		float tilt = cam.tilt;
		float bearing = cam.bearing;
		
		SharedPreferences settings = getSharedPreferences(MAP_STATE, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		putDouble(editor, "longitude", longitude);
		putDouble(editor, "latitude", latitude);
		editor.putFloat("zoom", zoom);
		editor.putFloat("tilt", tilt);
		editor.putFloat("bearing", bearing);
		editor.commit();
		
		super.onPause();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    Log.d(APPTAG, "on Resume");
	    SharedPreferences settings = getSharedPreferences(MAP_STATE, MODE_PRIVATE);
	    double latitude = getDouble(settings, "latitude", SHORTYS_LAT);
	    double longitude = getDouble(settings, "longitude", SHORTYS_LONG);
	    float zoom = settings.getFloat("zoom", DEFAULT_ZOOM);
	    float tilt = settings.getFloat("tilt", 0);
	    float bearing = settings.getFloat("bearing", 0);

	    LatLng startPosition = new LatLng(latitude, longitude);

	    CameraPosition cameraPosition = new CameraPosition.Builder()
	    .target(startPosition)      // Sets the center of the map to Mountain View
	    .zoom(zoom)                   // Sets the zoom
	    .bearing(bearing)                // Sets the orientation of the camera to east
	    .tilt(tilt)                   // Sets the tilt of the camera to 30 degrees
	    .build();                   // Creates a CameraPosition from the builder
	    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	Editor putDouble(final Editor edit, final String key, final double value) {
	   return edit.putLong(key, Double.doubleToRawLongBits(value));
	}

	double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
		return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
	}



	@Override
	public void onFilterCheckboxes(ArrayList<String> filters) {
		filterAllAges = filters.contains(getResources().getString(R.string.all_ages));
		filter();
	}
	
}
