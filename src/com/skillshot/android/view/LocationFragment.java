package com.skillshot.android.view;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Fragment;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
		RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_location, container, false);
		
		TextView nameView = (TextView) view.findViewById(R.id.locationName);
		nameView.setText(location.getName());
		
		TextView addrView = (TextView) view.findViewById(R.id.locationAddress);
		addrView.setText(location.getAddress());
		
		TextView aaView = (TextView) view.findViewById(R.id.locationAllAges);
		aaView.setText(location.isAll_ages() ? R.string.all_ages : R.string.twentyone_plus);
		
		TextView phoneView = (TextView) view.findViewById(R.id.locationPhone);
		phoneView.setText(location.getPhone());
		ImageButton callButton = (ImageButton) view.findViewById(R.id.callButton);
		callButton.getDrawable().setColorFilter(LocationActivity.BUTTON_COLOR, Mode.SRC_ATOP);
		if (location.getPhone() == null || location.getPhone().equals("")) {
			callButton.setVisibility(View.GONE);
			phoneView.setVisibility(View.GONE);
		}
		
		ImageButton mapButton = (ImageButton) view.findViewById(R.id.mapButton);
		mapButton.getDrawable().setColorFilter(LocationActivity.BUTTON_COLOR, Mode.SRC_ATOP);
		
		
		ImageButton websiteButton = (ImageButton) view.findViewById(R.id.websiteButton);
		websiteButton.getDrawable().setColorFilter(LocationActivity.BUTTON_COLOR, Mode.SRC_ATOP);
		if (location.getUrl() == null || location.getUrl().equals("")) {
			websiteButton.setVisibility(View.GONE);
		}
		
		ListView gameListView = (ListView) view.findViewById(R.id.gameListView);
		ArrayList<Machine> machineList = new ArrayList<Machine>(Arrays.asList(location.getMachines()));
		MachineAdapter gameListAdapter = new MachineAdapter(getActivity(), R.layout.machine_list_item, machineList, ((LocationActivity)getActivity()).isLoggedIn());

		View header = (View)getActivity().getLayoutInflater().inflate(R.layout.machine_list_header, null);
		gameListView.addHeaderView(header);

		gameListView.setAdapter(gameListAdapter);
		
		return view;
	}
}
