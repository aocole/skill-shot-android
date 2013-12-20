package com.skillshot.android;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.skillshot.android.rest.model.LocationsList;
import com.skillshot.android.rest.request.LocationsRequest;

public class MainActivity extends BaseActivity {
	public static final String LOCALITIES_ARRAY = "com.skillshot.android.LOCALITIES_ARRAY";
	public static final String LOCATION_ID = "com.skillshot.android.LOCATION_ID";
	private final String DEFAULT_AREA_ID = "seattle";
	private final int SKILL_SHOT_YELLOW = 42;
	private final int DEFAULT_ZOOM = 15;
	private final String MAP_TAG = "com.skillshot.android.MAP_TAG";
	private GoogleMap mMap;
	private Map<Marker, String> allMarkersMap = new HashMap<Marker, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		performRequest(DEFAULT_AREA_ID);

		// However, if we're being restored from a previous state,
		// then we don't need to do anything and should return or else
		// we could end up with overlapping fragments.
		if (savedInstanceState != null) {
			return;
		}


		// Create a new Fragment to be placed in the activity layout
		//			SpinnerFragment firstFragment = new SpinnerFragment();

		// In case this activity was started with special instructions from an
		// Intent, pass the Intent's extras to the fragment as arguments
		//			firstFragment.setArguments(getIntent().getExtras());
		MapFragment firstFragment = new MapFragment();

		// Add the fragment to the 'fragment_container' FrameLayout
		getFragmentManager().beginTransaction().add(R.id.container, firstFragment, MAP_TAG).commit();
	}

	/*
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		super.onConnected(dataBundle);
		mMap = ((MapFragment) getFragmentManager().findFragmentByTag(MAP_TAG)).getMap();
		mMap.setMyLocationEnabled(true);
		GoogleMap.OnInfoWindowClickListener listener = new LocationClickListener();
		mMap.setOnInfoWindowClickListener(listener);
		Location loc = getLocation();
		if (loc != null) {
			LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM));
			Toast.makeText(this, "Found you!", Toast.LENGTH_SHORT).show();
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

	private void performRequest(String area) {
		setProgressBarIndeterminateVisibility(true);

		LocationsRequest request = new LocationsRequest();
		String lastRequestCacheKey = request.createCacheKey();

		// XXX Set cache timeout correctly (one week?)
		spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ALWAYS_EXPIRED, new ListLocationsRequestListener());
	}

	private class ListLocationsRequestListener implements RequestListener<LocationsList> {

		@Override
		public void onRequestFailure(SpiceException e) {
			setProgressBarIndeterminateVisibility(false);
			//XXX show error message
		}

		@Override
		public void onRequestSuccess(LocationsList locationsList) {
			setProgressBarIndeterminateVisibility(false);
			LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
			allMarkersMap.clear();
			for(com.skillshot.android.rest.model.Location loc : locationsList) {
				LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
				boundsBuilder.include(latlng);
				Marker marker = mMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title(loc.getName())
				.icon(BitmapDescriptorFactory.defaultMarker(SKILL_SHOT_YELLOW))
				);
				allMarkersMap.put(marker, loc.getId());
			}
//			LatLngBounds bounds = boundsBuilder.build();
//			mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
		}
	}
	
	private class LocationClickListener implements GoogleMap.OnInfoWindowClickListener {

		@Override
		public void onInfoWindowClick(Marker marker) {
			String locationId = allMarkersMap.get(marker);
			Intent intent = new Intent(getBaseContext(), LocationActivity.class);
			intent.putExtra(LOCATION_ID, locationId);
			startActivity(intent);
		}

	}
}
