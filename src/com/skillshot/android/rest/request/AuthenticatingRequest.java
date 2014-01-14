package com.skillshot.android.rest.request;

import java.util.ArrayList;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

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
				throw new AuthenticationFailedException();
			}
			throw e;
		}
	}
	
	protected HttpEntity<?> getRequestEntity() throws AuthenticationFailedException {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(new MediaType("application","json"));
		
		ArrayList<MediaType> accept = new ArrayList<MediaType>();
		accept.add(new MediaType("application","json"));
		requestHeaders.setAccept(accept);

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
