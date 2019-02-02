package com.bitemii.delivery.model;

import java.sql.Date;

public class DeliveryStaff {
	private String userId;
	private String userEmail;
	private String orderId;
	private Date orderDate;

	public DeliveryStaff(String userId, String userEmail) {
		super();
		this.userId = userId;
		this.userEmail = userEmail;
	}
	public String getUserId() {
		return userId;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public String getOrderId() {
		return orderId;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public DeliveryStaff(String userId, String userEmail, String orderId, Date orderDate) {
		super();
		this.userId = userId;
		this.userEmail = userEmail;
		this.orderId = orderId;
		this.orderDate = orderDate;
	}
	
	
	@Override
	public String toString() {
		return "DeliveryStaff [userId=" + userId + ", userEmail=" + userEmail + ", orderId=" + orderId + ", orderDate="
				+ orderDate + "]";
	}
	
	

}
