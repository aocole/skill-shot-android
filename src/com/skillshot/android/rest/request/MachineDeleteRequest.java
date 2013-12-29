package com.skillshot.android.rest.request;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.skillshot.android.BaseActivity;

public class MachineDeleteRequest extends AuthenticatingRequest<Void> {
	private int machineId;
	public MachineDeleteRequest(int machineId) {
		super(Void.class);
		this.machineId = machineId;
	}

	@Override
	public Void authenticatedLoadDataFromNetwork() throws RestClientException, AuthenticationFailedException {
		String url = String.format("%s/machines/%s.json", BaseActivity.ENDPOINT, machineId);
		HttpEntity<?> requestEntity = getRequestEntity();

		ResponseEntity<Void> response = getRestTemplate().exchange(url, HttpMethod.DELETE, requestEntity, Void.class);
		return response.getBody();
	}

}
