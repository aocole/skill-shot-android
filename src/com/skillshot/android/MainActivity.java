package com.skillshot.android;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.skillshot.android.rest.model.LocalitiesList;
import com.skillshot.android.rest.request.LocalitiesRequest;
import com.skillshot.android.view.LocalitiesListFragment;
import com.skillshot.android.view.SpinnerFragment;

public class MainActivity extends BaseActivity {
	public static final String LOCALITIES_ARRAY = "com.skillshot.android.LOCALITIES_ARRAY";
	private final String DEFAULT_AREA_ID = "seattle";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		performRequest(DEFAULT_AREA_ID);
		setContentView(R.layout.activity_main);
		// Check that the activity is using the layout version with
		// the fragment_container FrameLayout
		if (findViewById(R.id.container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}

			// Create a new Fragment to be placed in the activity layout
			SpinnerFragment firstFragment = new SpinnerFragment();

			// In case this activity was started with special instructions from an
			// Intent, pass the Intent's extras to the fragment as arguments
			firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getFragmentManager().beginTransaction().add(R.id.container, firstFragment).commit();
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void performRequest(String area) {
		MainActivity.this.setProgressBarIndeterminateVisibility(true);

		LocalitiesRequest request = new LocalitiesRequest(area);
		String lastRequestCacheKey = request.createCacheKey();

		spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_WEEK, new ListLocalitiesRequestListener());
	}

	private class ListLocalitiesRequestListener implements RequestListener<LocalitiesList> {

		@Override
		public void onRequestFailure(SpiceException e) {
		}

		@Override
		public void onRequestSuccess(LocalitiesList localitiesList) {
			LocalitiesListFragment localitiesListFragment = new LocalitiesListFragment();
			Bundle args = new Bundle();
			args.putSerializable(LOCALITIES_ARRAY, localitiesList);
			localitiesListFragment.setArguments(args);
			
			FragmentTransaction trx = getFragmentManager().beginTransaction();
			
			trx.replace(R.id.container, localitiesListFragment);
			trx.commit();
		}
	}
}
