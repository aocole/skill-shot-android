package com.skillshot.android.rest.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.skillshot.android.BaseActivity;
import com.skillshot.android.rest.model.Location;

public class LocationRequest extends
SpringAndroidSpiceRequest<Location> {
	private String locationId;

	public LocationRequest(String locationId) {
		super(Location.class);
		this.locationId = locationId;
	}

	@Override
	public Location loadDataFromNetwork() throws Exception {

		String url = String.format("%s/locations/%s.json", BaseActivity.ENDPOINT, locationId);

		return getRestTemplate().getForObject(url, Location.class);
	}

	/**
	 * This method generates a unique cache key for this request. In this case
	 * our cache key depends just on the keyword.
	 * @return
	 */
	public String createCacheKey() {
		return "Location." + locationId;
	}

}
