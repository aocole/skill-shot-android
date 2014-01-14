package com.skillshot.android;

import java.util.ArrayList;
import java.util.Comparator;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.skillshot.android.rest.model.Location;
import com.skillshot.android.view.FilterDialogFragment.SortingDialogListener;
import com.skillshot.android.view.LocationsListFragment;

public class LocationListActivity extends LocationsActivity implements SortingDialogListener {

	private static final String LIST_TAG = "com.skillshot.android.LIST_TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();

		LocationsListFragment listFragment = new LocationsListFragment();
		getFragmentManager().beginTransaction().replace(R.id.container, listFragment, LIST_TAG).commit();
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		MenuItem mapItem = menu.findItem(R.id.action_map);
		MenuItem listItem = menu.findItem(R.id.action_list);
	    mapItem.setVisible(true);
	    listItem.setVisible(false);

	    return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_map:
			openMap();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void openMap() {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}
    
	@Override
	protected void onSetLocationsList() {
		filter();
	}

	@Override
	public void filter() {
		ArrayList<Location> filteredList = new ArrayList<Location>();
		for(Location loc : getLocationsList()) {
			boolean visible = 
					(!filterAllAges  || loc.isAll_ages()) // either we're not filtering or the location is all ages (if we are filtering)
					;
			if (visible) {
				filteredList.add(loc);
			}
		}
		ListFragment fragment = ((ListFragment) getFragmentManager().findFragmentByTag(LIST_TAG));
		@SuppressWarnings("unchecked")
		ArrayAdapter<Location> adapter = (ArrayAdapter<Location>) fragment.getListAdapter();
		adapter.clear();
		adapter.addAll(filteredList);
		sort();
	}

	@Override
	public void onSort(int sort) {
		filterSort = sort;
		sort();
	}
	
	private void sort() {
		ListFragment fragment = ((ListFragment) getFragmentManager().findFragmentByTag(LIST_TAG));
		@SuppressWarnings("unchecked")
		ArrayAdapter<Location> adapter = (ArrayAdapter<Location>) fragment.getListAdapter();
		Comparator<Location> comparator = new AlphaSort();
		if (filterSort == R.string.distance) {
			if (getUserLocation() == null) {
				Toast.makeText(this, R.string.location_unavailable, Toast.LENGTH_SHORT).show();
				return;
			}
			comparator = new DistanceSort();
		} else if (filterSort == R.string.number_of_games) {
			comparator = new NumGamesSort();
		}
		 
		adapter.sort(comparator);
		adapter.notifyDataSetChanged();
	}
	
	private class AlphaSort implements Comparator<Location> {

		@Override
		public int compare(Location a, Location b) {
			return a.getName().compareTo(b.getName());
		}
		
	}

	private class DistanceSort implements Comparator<Location> {

		@Override
		public int compare(Location a, Location b) {
			return Float.compare(userDistance(a), userDistance(b));
		}
		
	}

	private class NumGamesSort implements Comparator<Location> {

		/***
		 * This sort is reversed, since we want the locations 
		 * with more games at the top of the list
		 */
		@Override
		public int compare(Location a, Location b) {
			return Integer.valueOf(b.getNum_games()).compareTo(a.getNum_games());
		}
		
	}

}
