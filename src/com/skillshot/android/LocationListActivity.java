package com.skillshot.android;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.skillshot.android.rest.model.Location;
import com.skillshot.android.rest.model.LocationsList;
import com.skillshot.android.rest.model.Title;
import com.skillshot.android.view.LocationsListFragment;

public class LocationListActivity extends BaseActivity {

	private static final String LIST_TAG = "com.skillshot.android.LIST_TAG";
	private boolean filterAllAges;
	private LocationsList locationsList;

	public void setLocationsList(LocationsList locationsList) {
		this.locationsList = locationsList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		// Show the Up button in the action bar.
		setupActionBar();
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(MapActivity.FILTER_ALL_AGES)) {
				filterAllAges = savedInstanceState.getBoolean(MapActivity.FILTER_ALL_AGES);
			}
		}

		MapActivity.performRequest(this, spiceManager, new ListLocationsRequestListener());

		// However, if we're being restored from a previous state,
		// then we don't need to do anything and should return or else
		// we could end up with overlapping fragments.
		if (savedInstanceState != null) {
			return;
		}
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
			setLocationsList(locationsList);

			LocationsListFragment firstFragment = new LocationsListFragment();
			Bundle args = new Bundle();
			args.putSerializable(MapActivity.LOCATIONS_ARRAY, locationsList);
			firstFragment.setArguments(args);
			getFragmentManager().beginTransaction().add(R.id.container, firstFragment, LIST_TAG).commit();		

		}
	}
	
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
