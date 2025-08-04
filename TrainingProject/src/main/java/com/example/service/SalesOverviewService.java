package com.example.service;

import java.util.Map;

public interface SalesOverviewService {
	
    double getTotalRevenue();
    
    long getTotalOrders();
    
    Map<String, Long> getOrderCountPerDay(); 
    
    Map<Long, Long> getTopSellingProducts();
	
    long getShippedOrdersCount();
	
    long getCancelledOrdersCount();
	
    Map<String, Double> getRevenuePerDay();
}
