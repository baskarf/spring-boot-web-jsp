package com.bitemii.delivery.model;

public class RestaurantDetails {

	private Integer restId;
	
	private String restName;

	public RestaurantDetails(Integer restId, String restName) {
		super();
		this.restId = restId;
		this.restName = restName;
	}

	public Integer getRestId() {
		return restId;
	}

	public void setRestId(Integer restId) {
		this.restId = restId;
	}

	public String getRestName() {
		return restName;
	}

	public void setRestName(String restName) {
		this.restName = restName;
	}

	@Override
	public String toString() {
		return "RestaurantDetails [restId=" + restId + ", restName=" + restName + "]";
	}
	
	
}
