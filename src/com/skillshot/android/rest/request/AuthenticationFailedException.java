package com.skillshot.android.rest.request;

import com.octo.android.robospice.persistence.exception.SpiceException;

public class AuthenticationFailedException extends SpiceException {

	private static final long serialVersionUID = 47049554421214304L;

	public AuthenticationFailedException() {
		super("Authentication failed.");
	}
	
}
