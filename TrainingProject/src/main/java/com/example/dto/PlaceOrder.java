package com.example.dto;

import com.example.model.PaymentMethod;

public class PlaceOrder {
	
	private Long userId;
	private Long productId;
	private Long addressId;
	private PaymentMethod paymentType;
	private int quantity;
	
	// New fields for payment options
	private String paymentOption;  // "PAY_NOW" or "CASH_ON_DELIVERY"
	private String paymentStatus;  // "PAID" or "PENDING" (sent from frontend)
	
	public int getQuantity() {
		return quantity;
	}

	public Long getUserId() {
		return userId;
	}
	
	public Long getProductId() {
		return productId;
	}
	
	public Long getAddressId() {
		return addressId;
	}
	
	public PaymentMethod getPaymentType() {
		return paymentType;
	}

	// Getters for new fields
	public String getPaymentOption() {
		return paymentOption;
	}
	
	public String getPaymentStatus() {
		return paymentStatus;
	}
	
	// Setters for all fields (optional, for flexibility)
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}
	
	public void setPaymentType(PaymentMethod paymentType) {
		this.paymentType = paymentType;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}
	
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
} 