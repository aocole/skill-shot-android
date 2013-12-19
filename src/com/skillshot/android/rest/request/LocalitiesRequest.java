package com.skillshot.android.rest.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.skillshot.android.BaseActivity;
import com.skillshot.android.rest.model.LocalitiesList;

public class LocalitiesRequest extends
SpringAndroidSpiceRequest<LocalitiesList> {
	private String area_id;

	public LocalitiesRequest(String area_id) {
		super(LocalitiesList.class);
		this.area_id = area_id;
	}

	@Override
	public LocalitiesList loadDataFromNetwork() throws Exception {

		String url = String.format("%s/areas/%s/localities.json", BaseActivity.ENDPOINT, area_id);

		return getRestTemplate().getForObject(url, LocalitiesList.class);
	}

	/**
	 * This method generates a unique cache key for this request. In this case
	 * our cache key depends just on the keyword.
	 * @return
	 */
	public String createCacheKey() {
		return "Localities." + area_id;
	}

}
