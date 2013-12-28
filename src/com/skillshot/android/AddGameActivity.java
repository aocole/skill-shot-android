package com.skillshot.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.skillshot.android.rest.model.TitlesList;
import com.skillshot.android.rest.request.TitlesRequest;
import com.skillshot.android.view.TitlesListFragment;

public class AddGameActivity extends BaseActivity {

	public static final String TITLES_LIST = "com.skillshot.android.GAMES_LIST";
	private TitlesList titlesList = null;
	private String locationId = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent intent = getIntent();
	    locationId = intent.getStringExtra(MainActivity.LOCATION_ID);

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
			TitlesListFragment titlesListFragment = new TitlesListFragment();
			Bundle args = new Bundle();
			args.putSerializable(TITLES_LIST, titlesList);
			titlesListFragment.setArguments(args);
			getFragmentManager().beginTransaction().replace(R.id.container, titlesListFragment).commit();
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
		getMenuInflater().inflate(R.menu.add_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent locationIntent = NavUtils.getParentActivityIntent(this);
			locationIntent.putExtra(MainActivity.LOCATION_ID, locationId);
			NavUtils.navigateUpTo(this, locationIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public TitlesList getTitlesList() {
		return titlesList;
	}

	public void setTitlesList(TitlesList titlesList) {
		this.titlesList = titlesList;
	}

}
