package com.skillshot.android.rest.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.skillshot.android.BaseActivity;
import com.skillshot.android.rest.model.LocationsList;

public class LocationsRequest extends
SpringAndroidSpiceRequest<LocationsList> {

	public LocationsRequest() {
		super(LocationsList.class);
	}

	@Override
	public LocationsList loadDataFromNetwork() throws Exception {

		String url = String.format("%s/locations.json", BaseActivity.ENDPOINT);

		return getRestTemplate().getForObject(url, LocationsList.class);
	}

	/**
	 * This method generates a unique cache key for this request. In this case
	 * our cache key depends just on the keyword.
	 * @return
	 */
	public String createCacheKey() {
		return "Locations.all";
	}

}
