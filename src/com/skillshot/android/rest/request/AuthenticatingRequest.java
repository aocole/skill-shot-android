package com.skillshot.android.rest.request;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import android.util.Log;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.skillshot.android.BaseActivity;

public abstract class AuthenticatingRequest<RESULT> extends SpringAndroidSpiceRequest<RESULT> {
	protected String cookie = null;
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	
	public AuthenticatingRequest(Class<RESULT> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}

	@Override
	public RESULT loadDataFromNetwork() throws Exception {
		try {
			return authenticatedLoadDataFromNetwork();
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
				Log.d(BaseActivity.APPTAG, "Authentication failed.");
				throw new AuthenticationFailedException();
			}
			throw e;
		}
	}
	
	protected HttpEntity<?> getRequestEntity() throws AuthenticationFailedException {
		HttpHeaders requestHeaders = new HttpHeaders();
		//set the session cookie
		if (cookie == null) {
			throw new AuthenticationFailedException();
		}
		requestHeaders.set("Cookie", cookie);
		//create the request entity
		HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
		return requestEntity;
	}
	
	public abstract RESULT authenticatedLoadDataFromNetwork() throws Exception;

}