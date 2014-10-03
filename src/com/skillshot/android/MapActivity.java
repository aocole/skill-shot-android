package com.skillshot.android;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skillshot.android.view.FilterDialogFragment.FilterDialogListener;

public class MapActivity extends LocationsActivity implements FilterDialogListener {
	public static final String MAP_STATE = "com.skillshot.android.MAP_STATE";
	private final int SKILL_SHOT_YELLOW = 42;
	private final String MAP_TAG = "com.skillshot.android.MAP_TAG";
	private GoogleMap mMap;
	private Map<Marker, com.skillshot.android.rest.model.Location> allMarkersMap = new HashMap<Marker, com.skillshot.android.rest.model.Location>();
	private static final float DEFAULT_ZOOM = 15;
	public static double SHORTYS_LAT = 47.613834;
	public static double SHORTYS_LONG = -122.345043;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MapFragment firstFragment = new MapFragment();
		getFragmentManager().beginTransaction().add(R.id.container, firstFragment, MAP_TAG).commit();		
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_list:
			openList();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void openList() {
		Intent intent = new Intent(this, LocationListActivity.class);
		startActivity(intent);
	}
    
	@Override
	protected void filter() {
		for(Marker marker : allMarkersMap.keySet()) {
			com.skillshot.android.rest.model.Location loc = (com.skillshot.android.rest.model.Location) allMarkersMap.get(marker);
			boolean visible = 
					(!filterAllAges || loc.isAll_ages()) // either we're not filtering or the location is all ages (if we are filtering)
					;
			marker.setVisible(visible);
		}
	}
	
	private class MarkerClickListener implements GoogleMap.OnMarkerClickListener {
		@Override
		public boolean onMarkerClick(Marker marker) {
			String snippet = "";
			if(getUserLocation() != null) {
				snippet += userDistanceString(marker.getPosition().latitude, marker.getPosition().longitude) + " – ";
			}
			snippet += numGamesString(allMarkersMap.get(marker).getNum_games());
			marker.setSnippet(snippet);
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
	protected void onSetLocationsList() {
		allMarkersMap.clear();
		for(com.skillshot.android.rest.model.Location loc : getLocationsList()) {
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
