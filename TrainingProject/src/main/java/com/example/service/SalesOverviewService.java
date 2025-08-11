package com.example.service;

import java.util.Map;

public interface SalesOverviewService {
    
    // ========================= EXISTING SALES METHODS =========================
    
    double getTotalRevenue();
    
    long getTotalOrders();
    
    Map<String, Long> getOrderCountPerDay();
    
    Map<Long, Long> getTopSellingProducts();
    
    long getTodayOrdersCount();
    
    long getShippedOrdersCount();
    
    long getDeliveredOrdersCount();
    
    long getCancelledOrdersCount();
    
    Map<String, Double> getRevenuePerDay();
    
    long getRefundedOrdersCount();
    
    // ========================= NEW VISITOR ANALYTICS METHODS =========================
    
    /**
     * Get total unique visitors count
     */
    long getTotalVisitors();
    
    /**
     * Get visitors by period (day, week, month)
     */
    long getVisitorsByPeriod(String period);
    
    /**
     * Get visitors per day for charts
     */
    Map<String, Long> getVisitorsPerDay();
    
    /**
     * Get comprehensive analytics summary
     */
    Map<String, Object> getAnalyticsSummary();
    
    /**
     * Get today's visitor count
     */
    long getTodayVisitorsCount();
    
    /**
     * Get conversion rate calculation
     */
    double getConversionRate();
    
    /**
     * Get average order value
     */
    double getAverageOrderValue();
    
    /**
     * Get visitor growth rate (month over month)
     */
    double getVisitorGrowthRate();
    
    // ========================= HELPER METHODS FOR ENHANCED ANALYTICS =========================
    
    /**
     * Get peak visitor day
     */
    String getPeakVisitorDay();
    
    /**
     * Get average visitors per day
     */
    double getAverageVisitorsPerDay();
    
    /**
     * Get total active days with visitors
     */
    long getTotalVisitorDays();
    
    /**
     * Get maximum visitors in a single day
     */
    long getMaxVisitorsPerDay();
} 