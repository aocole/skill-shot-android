package com.skillshot.android;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.skillshot.android.rest.model.Location;
import com.skillshot.android.rest.model.Machine;
import com.skillshot.android.rest.request.LocationRequest;
import com.skillshot.android.rest.request.MachineDeleteRequest;
import com.skillshot.android.view.LocationFragment;
import com.skillshot.android.view.MachineAdapter;
import com.skillshot.android.view.SpinnerFragment;

public class LocationActivity extends BaseActivity {

	public static final String LOCATION = "com.skillshot.android.LOCATION";
	public static final int BUTTON_COLOR = Color.parseColor("#FF33b5e5");
	private Location location = null;
	private String locationId = null;

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
	    locationId = intent.getStringExtra(MapActivity.LOCATION_ID);
	    performRequest(locationId);

	    // However, if we're being restored from a previous state,
		// then we don't need to do anything and should return or else
		// we could end up with overlapping fragments.
		if (savedInstanceState != null) {
			return;
		}
	    
		// Create a new Fragment to be placed in the activity layout
	 	SpinnerFragment firstFragment = new SpinnerFragment();
	 	getFragmentManager().beginTransaction().add(R.id.container, firstFragment).commit();
	 	
	}

	private void performRequest(String locationId) {
		setProgressBarIndeterminateVisibility(true);

		LocationRequest request = new LocationRequest(locationId);
		String lastRequestCacheKey = request.createCacheKey();

		spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_HOUR, new LocationRequestListener());
	}

	private class LocationRequestListener implements RequestListener<Location> {
		@Override
		public void onRequestFailure(SpiceException e) {
			setProgressBarIndeterminateVisibility(false);
			Toast.makeText(
					getBaseContext(), 
					"Couldn't load data from the server. Please exit the app and try again.", 
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(Location location) {
			setProgressBarIndeterminateVisibility(false);
			setLocation(location);
			LocationFragment locationFragment = new LocationFragment();
			Bundle args = new Bundle();
			args.putSerializable(LOCATION, location);
			locationFragment.setArguments(args);
			getFragmentManager().beginTransaction().replace(R.id.container, locationFragment).commit();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem addGameItem = menu.findItem(R.id.action_add_game);

		boolean showLoggedIn = isLoggedIn();
	    addGameItem.setVisible(showLoggedIn);

	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Using finish instead of navigating up so that we return to map or list caller
			finish();
			return true;
		case R.id.action_add_game:
			Intent intent = new Intent(getBaseContext(), AddGameActivity.class);
			intent.putExtra(MapActivity.LOCATION_ID, locationId);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onMapButtonClick(View view) {
		if (location == null) {
			Toast.makeText(this, "Location data could not be loaded :-(", Toast.LENGTH_SHORT).show();
			return;
		}
		String uriBegin = "geo:0,0";
		String query = location.getAddress() + ' ' + location.getCity() + ' ' + location.getPostal_code();
		String encodedQuery = Uri.encode(query);
		String uriString = uriBegin + "?q=" + encodedQuery;
		Uri uri = Uri.parse(uriString);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
		startActivity(intent);	
	}

	public void onCallButtonClick(View view) {
		if (location == null) {
			Toast.makeText(this, "Location data could not be loaded :-(", Toast.LENGTH_SHORT).show();
			return;
		}
		Uri uri = Uri.fromParts("tel", location.getPhone(), null);
		Intent intent = new Intent(android.content.Intent.ACTION_DIAL, uri);
		startActivity(intent);	
	}
	
	public void onWebsiteButtonClick(View view) {
		if (location == null) {
			Toast.makeText(this, "Location data could not be loaded :-(", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(location.getUrl()));
		startActivity(intent);	
	}
	
    public class MachineDeleteClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Machine machine = (Machine)v.getTag();
			confirmDelete(machine);
		}
    	
	}

    public void deleteMachine(Machine machine) {
		getAdapter().setEnabled(false);
		setProgressBarIndeterminateVisibility(true);

		MachineDeleteRequest request = new MachineDeleteRequest(machine.getId());
		SharedPreferences mPrefs = getSharedPreferences(LoginActivity.LOGIN_PREFS, Context.MODE_PRIVATE);
		String cookie = mPrefs.getString(LoginActivity.PREF_TOKEN, null);
		request.setCookie(cookie);

		spiceManager.execute(request, null, DurationInMillis.ALWAYS_EXPIRED, new MachineDeleteRequestListener(machine));
		
    }
    
    public void confirmDelete(final Machine machine) {
    	Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_menu_delete);
        AlertDialog dialog = builder.create();
        dialog.setTitle("Confirm Deletion");
        dialog.setMessage(String.format("Delete %s?", machine.getTitle().getName()));
        dialog.setCancelable(true);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
        		deleteMachine(machine);
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            }
        });
        dialog.show();
    }

    private class MachineDeleteRequestListener implements RequestListener<Void> {
    	private Machine machine;

		public MachineDeleteRequestListener(Machine machine) {
			this.machine = machine;
		}

		@Override
		public void onRequestFailure(SpiceException e) {
			getAdapter().setEnabled(true);
			setProgressBarIndeterminateVisibility(false);
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
		public void onRequestSuccess(Void v) {
			getAdapter().setEnabled(true);
			setProgressBarIndeterminateVisibility(false);
			// Clear cached location information
			spiceManager.removeDataFromCache(LocationRequest.class, new LocationRequest(locationId).createCacheKey());
			MachineAdapter adapter = getAdapter();
			adapter.remove(machine);
			adapter.notifyDataSetChanged();
			Toast.makeText(
					getBaseContext(), 
					String.format("Deleted %s", machine.getTitle().getName()), 
					Toast.LENGTH_SHORT).show();			
		}
		
	}

	private MachineAdapter getAdapter() {
		ListView gameListView = (ListView) findViewById(R.id.gameListView);
		return ((MachineAdapter) ((HeaderViewListAdapter)gameListView.getAdapter()).getWrappedAdapter());
	}
		
	
}
