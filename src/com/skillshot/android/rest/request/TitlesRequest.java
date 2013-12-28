package com.skillshot.android.rest.request;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.skillshot.android.BaseActivity;
import com.skillshot.android.rest.model.TitlesList;

public class TitlesRequest extends AuthenticatingRequest<TitlesList> {
	public TitlesRequest(Class<TitlesList> clazz) {
		super(clazz);
	}

	@Override
	public TitlesList authenticatedLoadDataFromNetwork() throws RestClientException, AuthenticationFailedException {
		String url = String.format("%s/titles.json", BaseActivity.ENDPOINT);
		ResponseEntity<TitlesList> response = getRestTemplate().exchange(url, HttpMethod.GET, getRequestEntity(), TitlesList.class);
		return response.getBody();
	}

	/**
	 * This method generates a unique cache key for this request. In this case
	 * our cache key depends just on the keyword.
	 * @return
	 */
	public String createCacheKey() {
		return "Titles.";
	}

}
