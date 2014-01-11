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

import com.skillshot.android.BaseActivity;
import com.skillshot.android.LocationActivity;
import com.skillshot.android.MapActivity;
import com.skillshot.android.R;
import com.skillshot.android.rest.model.Location;

public class LocationsListFragment extends ListFragment {
	ArrayList<Location> locationsList = null;
	private boolean filterAllAges = false;
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		locationsList = (ArrayList<Location>) this.getArguments().get(MapActivity.LOCATIONS_ARRAY);

		ArrayAdapter<Location> adapter = new ArrayAdapter<Location>(getActivity(), android.R.layout.simple_list_item_1, locationsList);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_locations_list, container, false);
		setListAdapter(adapter);
		filter();
		
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

	private void filter() {
		ArrayList<Location> filteredList = new ArrayList<Location>();
		for(Location loc : locationsList) {
			boolean visible = 
					(!filterAllAges  || loc.isAll_ages()) // either we're not filtering or the location is all ages (if we are filtering)
					;
			if (visible) {
				filteredList.add(loc);
			}
		}
		@SuppressWarnings("unchecked")
		ArrayAdapter<Location> adapter = (ArrayAdapter<Location>) getListAdapter();
		adapter.clear();
		adapter.addAll(filteredList);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView listView, View itemView, int position,
			long id) {
		Location location = (Location)listView.getItemAtPosition(position);
		Intent intent = new Intent(getActivity(), LocationActivity.class);
		intent.putExtra(MapActivity.LOCATION_ID, location.getId());
		startActivity(intent);	
	}

}
