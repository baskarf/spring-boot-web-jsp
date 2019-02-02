package com.bitemii.delivery.model;

import java.sql.Date;

public class OrderDetail {

	private String orderId;
	
	private String orderUserId;
	private String orderRestaurentId;
	private String userPhoneNo;
	private Date orderDate;
	private String orderPaymentStatus;
	private String orderProcessStatus;
	private String deliveryStaffEmail; //order_deliverystaff_id
	private Double amount;
	public OrderDetail(String orderId, String orderUserId, String orderRestaurentId, String userPhoneNo, Date orderDate,
			String orderPaymentStatus, String orderProcessStatus, Double amount, String deliveryStaffEmail) {
		super();
		this.orderId = orderId;
		this.orderUserId = orderUserId;
		this.orderRestaurentId = orderRestaurentId;
		this.userPhoneNo = userPhoneNo;
		this.orderDate = orderDate;
		this.orderPaymentStatus = orderPaymentStatus;
		this.orderProcessStatus = orderProcessStatus;
		this.amount = amount;
		this.deliveryStaffEmail = deliveryStaffEmail;

	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderUserId() {
		return orderUserId;
	}
	public void setOrderUserId(String orderUserId) {
		this.orderUserId = orderUserId;
	}
	public String getOrderRestaurentId() {
		return orderRestaurentId;
	}
	public void setOrderRestaurentId(String orderRestaurentId) {
		this.orderRestaurentId = orderRestaurentId;
	}
	public String getUserPhoneNo() {
		return userPhoneNo;
	}
	public void setUserPhoneNo(String userPhoneNo) {
		this.userPhoneNo = userPhoneNo;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public String getOrderPaymentStatus() {
		return orderPaymentStatus;
	}
	public void setOrderPaymentStatus(String orderPaymentStatus) {
		this.orderPaymentStatus = orderPaymentStatus;
	}
	public String getOrderProcessStatus() {
		return orderProcessStatus;
	}
	public void setOrderProcessStatus(String orderProcessStatus) {
		this.orderProcessStatus = orderProcessStatus;
	}

	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	
	public String getDeliveryStaffEmail() {
		return deliveryStaffEmail;
	}
	public void setDeliveryStaffEmail(String deliveryStaffEmail) {
		this.deliveryStaffEmail = deliveryStaffEmail;
	}
	@Override
	public String toString() {
		return "OrderDetail [orderId=" + orderId + ", orderUserId=" + orderUserId + ", orderRestaurentId="
				+ orderRestaurentId + ", userPhoneNo=" + userPhoneNo + ", orderDate=" + orderDate
				+ ", orderPaymentStatus=" + orderPaymentStatus + ", orderProcessStatus=" + orderProcessStatus
				+ ", amount=" + amount + "]";
	}

	
}
