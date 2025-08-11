package com.example.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.service.SalesOverviewService;

@RestController
@RequestMapping("/sales")
public class SalesOverviewController {

    private final SalesOverviewService salesOverviewService;

    public SalesOverviewController(SalesOverviewService salesOverviewService) {
        this.salesOverviewService = salesOverviewService;
    }

    

    @GetMapping("/total-revenue")
    public double getTotalRevenue() {
        return salesOverviewService.getTotalRevenue();
    }

    @GetMapping("/total-orders")
    public long getTotalOrders() {
        return salesOverviewService.getTotalOrders();
    }

    @GetMapping("/orders-per-day")
    public Map<String, Long> getOrderCountPerDay() {
        return salesOverviewService.getOrderCountPerDay();
    }

    @GetMapping("/top-products")
    public Map<Long, Long> getTopSellingProducts() {
        return salesOverviewService.getTopSellingProducts();
    }
    
    @GetMapping("/orders-count/SHIPPED")
    public ResponseEntity<Long> getShippedCount() {
        try {
            long count = salesOverviewService.getShippedOrdersCount();
            return ResponseEntity.ok(count);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/orders-count/DELIVERED")
    public ResponseEntity<Long> getDeliveredCount() {
        try {
            long count = salesOverviewService.getDeliveredOrdersCount();
            return ResponseEntity.ok(count);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/orders-today-count")
    public ResponseEntity<Long> getTodayOrdersCount() {
        try {
            long count = salesOverviewService.getTodayOrdersCount();
            return ResponseEntity.ok(count);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/orders-count/CANCELLED")
    public ResponseEntity<Long> getCancelledCount() {
        try {
            long count = salesOverviewService.getCancelledOrdersCount();
            return ResponseEntity.ok(count);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/orders-count/RETURNED")
    public ResponseEntity<Long> getRefundedCount() {
        try {
            long count = salesOverviewService.getRefundedOrdersCount();
            return ResponseEntity.ok(count);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/daily-revenue")
    public Map<String, Double> getRevenuePerDay() {
        return salesOverviewService.getRevenuePerDay();
    }

    @GetMapping("/total-visitors")
    public ResponseEntity<Long> getTotalVisitors() {
        try {
            long totalVisitors = salesOverviewService.getTotalVisitors();
            return ResponseEntity.ok(totalVisitors);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    @GetMapping("/visitors-by-period/{period}")
    public ResponseEntity<Long> getVisitorsByPeriod(@PathVariable String period) {
        try {
            long visitors = salesOverviewService.getVisitorsByPeriod(period);
            return ResponseEntity.ok(visitors);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/visitors-per-day")
    public ResponseEntity<Map<String, Long>> getVisitorsPerDay() {
        try {
            Map<String, Long> visitorsPerDay = salesOverviewService.getVisitorsPerDay();
            return ResponseEntity.ok(visitorsPerDay);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/conversion-rate")
    public ResponseEntity<Double> getConversionRate() {
        try {
            double conversionRate = salesOverviewService.getConversionRate();
            return ResponseEntity.ok(conversionRate);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/average-order-value")
    public ResponseEntity<Double> getAverageOrderValue() {
        try {
            double aov = salesOverviewService.getAverageOrderValue();
            return ResponseEntity.ok(aov);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/visitor-growth-rate")
    public ResponseEntity<Double> getVisitorGrowthRate() {
        try {
            double growthRate = salesOverviewService.getVisitorGrowthRate();
            return ResponseEntity.ok(growthRate);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/visitors-today-count")
    public ResponseEntity<Long> getTodayVisitorsCount() {
        try {
            long count = salesOverviewService.getTodayVisitorsCount();
            return ResponseEntity.ok(count);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/peak-visitor-day")
    public ResponseEntity<String> getPeakVisitorDay() {
        try {
            String peakDay = salesOverviewService.getPeakVisitorDay();
            return ResponseEntity.ok(peakDay);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/average-visitors-per-day")
    public ResponseEntity<Double> getAverageVisitorsPerDay() {
        try {
            double avgVisitors = salesOverviewService.getAverageVisitorsPerDay();
            return ResponseEntity.ok(avgVisitors);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/total-visitor-days")
    public ResponseEntity<Long> getTotalVisitorDays() {
        try {
            long days = salesOverviewService.getTotalVisitorDays();
            return ResponseEntity.ok(days);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/max-visitors-per-day")
    public ResponseEntity<Long> getMaxVisitorsPerDay() {
        try {
            long maxVisitors = salesOverviewService.getMaxVisitorsPerDay();
            return ResponseEntity.ok(maxVisitors);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/analytics-summary")
    public ResponseEntity<Map<String, Object>> getAnalyticsSummary() {
        try {
            Map<String, Object> summary = salesOverviewService.getAnalyticsSummary();
            return ResponseEntity.ok(summary);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/dashboard-overview")
    public ResponseEntity<Map<String, Object>> getDashboardOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();
            
            
            overview.put("totalRevenue", salesOverviewService.getTotalRevenue());
            overview.put("totalOrders", salesOverviewService.getTotalOrders());
            overview.put("averageOrderValue", salesOverviewService.getAverageOrderValue());
            overview.put("todayOrdersCount", salesOverviewService.getTodayOrdersCount());
            
            
            overview.put("totalVisitors", salesOverviewService.getTotalVisitors());
            overview.put("conversionRate", salesOverviewService.getConversionRate());
            overview.put("visitorGrowthRate", salesOverviewService.getVisitorGrowthRate());
            overview.put("todayVisitorsCount", salesOverviewService.getTodayVisitorsCount());
            overview.put("peakVisitorDay", salesOverviewService.getPeakVisitorDay());
            overview.put("averageVisitorsPerDay", salesOverviewService.getAverageVisitorsPerDay());
            
            
            overview.put("shippedOrders", salesOverviewService.getShippedOrdersCount());
            overview.put("deliveredOrders", salesOverviewService.getDeliveredOrdersCount());
            overview.put("cancelledOrders", salesOverviewService.getCancelledOrdersCount());
            overview.put("refundedOrders", salesOverviewService.getRefundedOrdersCount());
            
            
            overview.put("ordersPerDay", salesOverviewService.getOrderCountPerDay());
            overview.put("visitorsPerDay", salesOverviewService.getVisitorsPerDay());
            overview.put("revenuePerDay", salesOverviewService.getRevenuePerDay());
            overview.put("topProducts", salesOverviewService.getTopSellingProducts());
            
            return ResponseEntity.ok(overview);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    

    /**
     * Track a new visitor
     * TODO: Implement actual visitor tracking in database
     */
    @PostMapping("/track-visitor")
    public ResponseEntity<Map<String, String>> trackVisitor(@RequestBody Map<String, Object> visitorData) {
        try {
            
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Visitor tracked successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to track visitor: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Track a page view
     * TODO: Implement actual page view tracking in database
     */
    @PostMapping("/track-page-view")
    public ResponseEntity<Map<String, String>> trackPageView(@RequestBody Map<String, Object> pageViewData) {
        try {
            
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Page view tracked successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to track page view: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/all-data")
    public ResponseEntity<Map<String, Object>> getAllData() {
        try {
            Map<String, Object> allData = new HashMap<>();
            
            
            allData.put("totalRevenue", getTotalRevenue());
            allData.put("totalOrders", getTotalOrders());
            allData.put("ordersPerDay", getOrderCountPerDay());
            allData.put("topProducts", getTopSellingProducts());
            allData.put("revenuePerDay", getRevenuePerDay());
            
            
            allData.put("totalVisitors", salesOverviewService.getTotalVisitors());
            allData.put("conversionRate", salesOverviewService.getConversionRate());
            allData.put("visitorsPerDay", salesOverviewService.getVisitorsPerDay());
            
            return ResponseEntity.ok(allData);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
} 