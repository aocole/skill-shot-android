package com.skillshot.android.view;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.skillshot.android.LocationActivity;
import com.skillshot.android.LocationListActivity;
import com.skillshot.android.LocationsActivity;
import com.skillshot.android.R;
import com.skillshot.android.rest.model.Location;

public class LocationsListFragment extends ListFragment {
	private ArrayList<Location> locationsList = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		locationsList = (ArrayList<Location>) this.getArguments().get(LocationsActivity.LOCATIONS_ARRAY);
		
		// We have to clone the object here because actions that modify the list 
		// adapter later will modify the object in-place. Fun debugging.
		locationsList = (ArrayList<Location>) locationsList.clone();

		LocationAdapter adapter = new LocationAdapter(getActivity(), R.layout.location_list_item, locationsList);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_locations_list, container, false);
		setListAdapter(adapter);
		((LocationListActivity)getActivity()).filter();
		
		return layout;
	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ListView list = getListView();
		list.setFastScrollEnabled(true);
		list.setTextFilterEnabled(true);
		
		
		SearchView nameSearch = (SearchView) view.findViewById(R.id.filter);
		nameSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public boolean onQueryTextChange(String newText) {
				((ArrayAdapter<Location>) getListAdapter()).getFilter().filter(newText);
				return false;
			}
		});
	}

	@Override
	public void onListItemClick(ListView listView, View itemView, int position, long id) {
		Location location = (Location)listView.getItemAtPosition(position);
		Intent intent = new Intent(getActivity(), LocationActivity.class);
		intent.putExtra(LocationsActivity.LOCATION_ID, location.getId());
		startActivity(intent);	
	}

}
