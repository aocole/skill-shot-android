package com.skillshot.android.rest.model;

import java.io.Serializable;

public class Location implements Serializable {
	private static final long serialVersionUID = -3102613690976343805L;
	private String id;
	private String name;
	private String address;
	private String city;
	private String postal_code;
	private float latitude;
	private float longitude;
	private String phone;
	private String url;
	private boolean all_ages;
	private int num_games;
	private Machine[] machines;
	public Machine[] getMachines() {
		return machines;
	}
	public void setMachines(Machine[] machines) {
		this.machines = machines;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPostal_code() {
		return postal_code;
	}
	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isAll_ages() {
		return all_ages;
	}
	public void setAll_ages(boolean all_ages) {
		this.all_ages = all_ages;
	}
	public int getNum_games() {
		return num_games;
	}
	public void setNum_games(int num_games) {
		this.num_games = num_games;
	}
}
