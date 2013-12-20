package com.skillshot.android.rest.model;

import java.io.Serializable;

public class Machine implements Serializable {
	private static final long serialVersionUID = 7324978864623420385L;
	private int id;
	private Title title;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Title getTitle() {
		return title;
	}
	public void setTitle(Title title) {
		this.title = title;
	}
	
	public String toString() {
		return this.title.getName();
	}

}
