package com.skillshot.android.view;

import java.util.ArrayList;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.skillshot.android.MainActivity;
import com.skillshot.android.R;
import com.skillshot.android.rest.model.Locality;

public class LocalitiesListFragment extends ListFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ArrayList<Locality> localities = (ArrayList<Locality>) this.getArguments().get(MainActivity.LOCALITIES_ARRAY);
		ArrayAdapter<Locality> adapter = new ArrayAdapter<Locality>(getActivity(), android.R.layout.simple_list_item_1, localities);
		ListView view = (ListView) inflater.inflate(R.layout.fragment_localities_list, container, false);
		view.setAdapter(adapter);
		return view;
	}
}
