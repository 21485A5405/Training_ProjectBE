package com.example.service;

import java.util.Map;

public interface SalesOverviewService {
	
    double getTotalRevenue();
    
    long getTotalOrders();
    
    Map<String, Long> getOrderCountPerDay(); 
    
    Map<Long, Long> getTopSellingProducts();
    
    long getShippedOrdersCount();
	
    long getDeliveredOrdersCount();
    
    public long getTodayOrdersCount();
    
    long getCancelledOrdersCount();
	
    Map<String, Double> getRevenuePerDay();

	long getRefundedOrdersCount();
}
