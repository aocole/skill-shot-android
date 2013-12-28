package com.skillshot.android.view;

import java.util.ArrayList;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.skillshot.android.AddGameActivity;
import com.skillshot.android.R;
import com.skillshot.android.rest.model.Title;

public class TitlesListFragment extends ListFragment {
	ListView list;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		@SuppressWarnings("unchecked")
		ArrayList<Title> titles = (ArrayList<Title>) this.getArguments().get(AddGameActivity.TITLES_LIST);
		ArrayAdapter<Title> adapter = new ArrayAdapter<Title>(getActivity(), android.R.layout.simple_list_item_1, titles);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_titles_list, container, false);
		list = (ListView) layout.findViewById(android.R.id.list);
		list.setFastScrollEnabled(true);
		list.setTextFilterEnabled(true);
		list.setAdapter(adapter);
		
		SearchView filter = (SearchView) layout.findViewById(R.id.filter);
		filter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public boolean onQueryTextChange(String newText) {
				((ArrayAdapter<Title>)list.getAdapter()).getFilter().filter(newText);
				return false;
			}
		});
		
		
		return layout;
	}
	
}
