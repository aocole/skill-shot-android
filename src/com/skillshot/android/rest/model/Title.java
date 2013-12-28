package com.skillshot.android.rest.model;

import java.io.Serializable;

public class Title implements Serializable {

	private static final long serialVersionUID = -2758106422219651868L;
	private int id;
	private String name;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}

}
