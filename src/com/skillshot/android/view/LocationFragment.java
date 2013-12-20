package com.skillshot.android.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.skillshot.android.LocationActivity;
import com.skillshot.android.R;
import com.skillshot.android.rest.model.Location;
import com.skillshot.android.rest.model.Machine;

public class LocationFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Location location = (Location) this.getArguments().get(LocationActivity.LOCATION);
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_location, container, false);
		
		TextView nameView = (TextView) view.findViewById(R.id.locationName);
		nameView.setText(location.getName());
		
		TextView addrView = (TextView) view.findViewById(R.id.locationAddress);
		addrView.setText(location.getAddress());
		
		TextView aaView = (TextView) view.findViewById(R.id.locationAllAges);
		aaView.setText(location.isAll_ages() ? "All ages!" : "21+");
		
		TextView phoneView = (TextView) view.findViewById(R.id.locationPhone);
		phoneView.setText(location.getPhone());
		
		TextView urlView = (TextView) view.findViewById(R.id.locationUrl);
		urlView.setText(location.getUrl());
		
		ListView gameListView = (ListView) view.findViewById(R.id.gameListView);
		ArrayAdapter<Machine> gameListAdapter = new ArrayAdapter<Machine>(getActivity(), android.R.layout.simple_list_item_1, location.getMachines());
		gameListView.setAdapter(gameListAdapter);
		
		return view;
	}
}
