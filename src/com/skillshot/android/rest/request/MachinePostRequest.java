package com.skillshot.android.rest.request;

import java.util.ArrayList;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.skillshot.android.BaseActivity;
import com.skillshot.android.rest.model.Machine;

public class MachinePostRequest extends AuthenticatingRequest<Machine> {
	private String locationId;
	private int titleId;
	public MachinePostRequest(String locationId, int titleId) {
		super(Machine.class);
		this.locationId = locationId;
		this.titleId = titleId;
	}
	
	private class NewMachine {
		private int title_id;

		public NewMachine(int titleId) {
			this.title_id = titleId;
		}

		public int getTitle_id() {
			return title_id;
		}

		public void setTitle_id(int title_id) {
			this.title_id = title_id;
		}
	}
	
	@Override
	public Machine authenticatedLoadDataFromNetwork() throws RestClientException, AuthenticationFailedException {
		String url = String.format("%s/locations/%s/machines.json", BaseActivity.ENDPOINT, locationId);
		
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

		NewMachine machine = new NewMachine(titleId);
		HttpEntity<NewMachine> requestEntity =
				new HttpEntity<NewMachine>(machine, requestHeaders);

		ResponseEntity<Machine> response = getRestTemplate().exchange(url, HttpMethod.POST, requestEntity, Machine.class);
		return response.getBody();
	}

}
