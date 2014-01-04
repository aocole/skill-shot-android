package com.skillshot.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.skillshot.android.rest.model.Machine;
import com.skillshot.android.rest.model.Title;
import com.skillshot.android.rest.model.TitlesList;
import com.skillshot.android.rest.request.LocationRequest;
import com.skillshot.android.rest.request.MachinePostRequest;
import com.skillshot.android.rest.request.TitlesRequest;
import com.skillshot.android.view.TitleAdapter;

public class AddGameActivity extends BaseActivity {

	public static final String TITLES_LIST = "com.skillshot.android.GAMES_LIST";
	private TitlesList titlesList = null;
	private String locationId = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_add_game);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent intent = getIntent();
	    locationId = intent.getStringExtra(MapActivity.LOCATION_ID);

	    performRequest();
	}

	private void performRequest() {
		setProgressBarIndeterminateVisibility(true);

		TitlesRequest request = new TitlesRequest(TitlesList.class);
		SharedPreferences mPrefs = getSharedPreferences(LoginActivity.LOGIN_PREFS, Context.MODE_PRIVATE);
		String cookie = mPrefs.getString(LoginActivity.PREF_TOKEN, null);
		request.setCookie(cookie);
		String lastRequestCacheKey = request.createCacheKey();

		spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_WEEK, new TitlesRequestListener());
	}

	private class TitlesRequestListener implements RequestListener<TitlesList> {
		@Override
		public void onRequestFailure(SpiceException e) {
			Log.d(APPTAG, String.format("Spice raised an exception: %s", e));
			setProgressBarIndeterminateVisibility(false);
			if (checkAuthentication(e)) {
				return;
			}
			Toast.makeText(
					getBaseContext(), 
					"Couldn't load data from the server. Please go back and try again.", 
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(TitlesList titlesList) {
			setProgressBarIndeterminateVisibility(false);
			setTitlesList(titlesList);

			TitleAdapter adapter = new TitleAdapter(getBaseContext(), android.R.layout.simple_list_item_1, titlesList);
			ListView list = (ListView) findViewById(android.R.id.list);
			list.setFastScrollEnabled(true);
			list.setTextFilterEnabled(true);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new TitleClickListener());
			
			
			SearchView filter = (SearchView)findViewById(R.id.filter);
			filter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				
				@Override
				public boolean onQueryTextSubmit(String query) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean onQueryTextChange(String newText) {
					getAdapter().getFilter().filter(newText);
					return false;
				}
			});
		}
	}
	
	private class TitleClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> listView, View itemView, int position,
				long id) {
			getAdapter().setEnabled(false);
			setProgressBarIndeterminateVisibility(true);

			Title title = (Title)listView.getItemAtPosition(position);
			MachinePostRequest request = new MachinePostRequest(locationId, title.getId());
			SharedPreferences mPrefs = getSharedPreferences(LoginActivity.LOGIN_PREFS, Context.MODE_PRIVATE);
			String cookie = mPrefs.getString(LoginActivity.PREF_TOKEN, null);
			request.setCookie(cookie);

			spiceManager.execute(request, null, DurationInMillis.ALWAYS_EXPIRED, new MachinePostRequestListener());
			
		}
	}
	
	private class MachinePostRequestListener implements RequestListener<Machine> {

		@Override
		public void onRequestFailure(SpiceException e) {
			Log.d(APPTAG, String.format("Spice raised an exception: %s", e));
			setProgressBarIndeterminateVisibility(false);
			getAdapter().setEnabled(true);
			if (checkAuthentication(e)) {
				return;
			}
			Toast.makeText(
					getBaseContext(), 
					"Couldn't load data from the server. Please go back and try again.", 
					Toast.LENGTH_LONG).show();
			navigateUp();
		}

		@Override
		public void onRequestSuccess(Machine machine) {
			setProgressBarIndeterminateVisibility(false);
			getAdapter().setEnabled(true);
			spiceManager.removeDataFromCache(LocationRequest.class, new LocationRequest(locationId).createCacheKey());
			Toast.makeText(
					getBaseContext(), 
					String.format("%s added successfully", machine.getTitle().getName()), 
					Toast.LENGTH_SHORT).show();
			navigateUp();
		}
		
	}

	private TitleAdapter getAdapter() {
		ListView listView = (ListView) findViewById(android.R.id.list);
		return (TitleAdapter) listView.getAdapter();
	}
		
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			navigateUp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void navigateUp() {
		Intent locationIntent = NavUtils.getParentActivityIntent(this);
		locationIntent.putExtra(MapActivity.LOCATION_ID, locationId);
		NavUtils.navigateUpTo(this, locationIntent);
	}

	public TitlesList getTitlesList() {
		return titlesList;
	}

	public void setTitlesList(TitlesList titlesList) {
		this.titlesList = titlesList;
	}

}
