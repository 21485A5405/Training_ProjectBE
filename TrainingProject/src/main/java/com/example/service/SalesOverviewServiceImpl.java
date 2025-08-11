//package com.example.service;
//
//import com.example.authentication.CurrentUser;
//import com.example.model.OrderItem;
//import com.example.model.OrderProduct;
//import com.example.model.User;
//import com.example.repo.OrderRepo;
//
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class SalesOverviewServiceImpl implements SalesOverviewService {
//
//    private final OrderRepo orderRepo;
//    private final CurrentUser currentUser;
//
//    public SalesOverviewServiceImpl(OrderRepo orderRepo, CurrentUser currentUser) {
//        this.orderRepo = orderRepo;
//        this.currentUser = currentUser;
//    }
//
//    private void ensureAdminAccess() {
//        User user = currentUser.getUser();
//        if (user == null || user.getUserRole() == null || !user.getUserRole().name().equals("ADMIN")) {
//            throw new SecurityException("Access denied: Admins only");
//        }
//    }
//    
//    public double getTotalRevenue() {
//        ensureAdminAccess();
//        List<OrderProduct> orders = orderRepo.findAll();
//        return orders.stream()
//                .filter(order -> {
//                    String status = order.getOrderStatus().name();
//                    return !(status.equalsIgnoreCase("CANCELLED") || status.equalsIgnoreCase("REFUNDED"));
//                })
//                .mapToDouble(OrderProduct::getTotalPrice)
//                .sum();
//    }
//
//    public long getTotalOrders() {
//        ensureAdminAccess();
//        return orderRepo.count();
//    }
//
//    public Map<String, Long> getOrderCountPerDay() {
//        ensureAdminAccess();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        return orderRepo.findAll().stream()
//                .collect(Collectors.groupingBy(
//                        o -> o.getOrderDate().format(formatter),
//                        Collectors.counting()
//                ));
//    }
//
//    public Map<Long, Long> getTopSellingProducts() {
//        ensureAdminAccess();
//        List<OrderProduct> orders = orderRepo.findAll();
//
//        Map<Long, Long> productSales = new HashMap<>();
//
//        for (OrderProduct order : orders) {
//            for (OrderItem item : order.getItems()) {
//                Long productId = item.getProduct().getProductId();
//                productSales.put(productId, productSales.getOrDefault(productId, 0L) + item.getQuantity());
//            }
//        }
//
//        return productSales.entrySet().stream()
//                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        (x, y) -> x,
//                        LinkedHashMap::new
//                ));
//    }
//    
//    public long getTodayOrdersCount() {
//        ensureAdminAccess();
//        return orderRepo.findAll().stream()
//                .filter(order -> order.getOrderDate().toLocalDate().isEqual(LocalDate.now()))
//                .count();
//    }
//
//
//    
//    public long getShippedOrdersCount() {
//    	ensureAdminAccess();
//        return orderRepo.findAll().stream()
//                .filter(o -> o.getOrderStatus().name().equalsIgnoreCase("SHIPPED"))
//                .count();
//    }
//    
//    public long getDeliveredOrdersCount() {
//    	ensureAdminAccess();
//        return orderRepo.findAll().stream()
//                .filter(o -> o.getOrderStatus().name().equalsIgnoreCase("DELIVERED"))
//                .count();
//    }
//
//    public long getCancelledOrdersCount() {
//    	ensureAdminAccess();
//        return orderRepo.findAll().stream()
//                .filter(o -> o.getOrderStatus().name().equalsIgnoreCase("CANCELLED"))
//                .count();
//    }
//
//    public Map<String, Double> getRevenuePerDay() {
//        ensureAdminAccess();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//        return orderRepo.findAll().stream()
//                .collect(Collectors.groupingBy(
//                        o -> o.getOrderDate().format(formatter),
//                        TreeMap::new,
//                        Collectors.summingDouble(OrderProduct::getTotalPrice)
//                ));
//    }
//
//	public long getRefundedOrdersCount() {
//		ensureAdminAccess();
//        return orderRepo.findAll().stream()
//                .filter(o -> o.getOrderStatus().name().equalsIgnoreCase("RETURNED"))
//                .count();
//	}
//
////}

package com.example.service;

import com.example.authentication.CurrentUser;
import com.example.model.OrderItem;
import com.example.model.OrderProduct;
import com.example.model.User;
import com.example.repo.OrderRepo;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SalesOverviewServiceImpl implements SalesOverviewService {

    private final OrderRepo orderRepo;
    private final CurrentUser currentUser;

    public SalesOverviewServiceImpl(OrderRepo orderRepo, CurrentUser currentUser) {
        this.orderRepo = orderRepo;
        this.currentUser = currentUser;
    }

    private void ensureAdminAccess() {
        User user = currentUser.getUser();
        if (user == null || user.getUserRole() == null || !user.getUserRole().name().equals("ADMIN")) {
            throw new SecurityException("Access denied: Admins only");
        }
    }
    
    public double getTotalRevenue() {
        ensureAdminAccess();
        List<OrderProduct> orders = orderRepo.findAll();
        return orders.stream()
                .filter(order -> {
                    String status = order.getOrderStatus().name();
                    return !(status.equalsIgnoreCase("CANCELLED") || status.equalsIgnoreCase("REFUNDED"));
                })
                .mapToDouble(OrderProduct::getTotalPrice)
                .sum();
    }

    public long getTotalOrders() {
        ensureAdminAccess();
        return orderRepo.count();
    }

    public Map<String, Long> getOrderCountPerDay() {
        ensureAdminAccess();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return orderRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOrderDate().format(formatter),
                        Collectors.counting()
                ));
    }

    public Map<Long, Long> getTopSellingProducts() {
        ensureAdminAccess();
        List<OrderProduct> orders = orderRepo.findAll();

        Map<Long, Long> productSales = new HashMap<>();

        for (OrderProduct order : orders) {
            for (OrderItem item : order.getItems()) {
                Long productId = item.getProduct().getProductId();
                productSales.put(productId, productSales.getOrDefault(productId, 0L) + item.getQuantity());
            }
        }

        return productSales.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (x, y) -> x,
                        LinkedHashMap::new
                ));
    }
    
    public long getTodayOrdersCount() {
        ensureAdminAccess();
        return orderRepo.findAll().stream()
                .filter(order -> order.getOrderDate().toLocalDate().isEqual(LocalDate.now()))
                .count();
    }

    public long getShippedOrdersCount() {
    	ensureAdminAccess();
        return orderRepo.findAll().stream()
                .filter(o -> o.getOrderStatus().name().equalsIgnoreCase("SHIPPED"))
                .count();
    }
    
    public long getDeliveredOrdersCount() {
    	ensureAdminAccess();
        return orderRepo.findAll().stream()
                .filter(o -> o.getOrderStatus().name().equalsIgnoreCase("DELIVERED"))
                .count();
    }

    public long getCancelledOrdersCount() {
    	ensureAdminAccess();
        return orderRepo.findAll().stream()
                .filter(o -> o.getOrderStatus().name().equalsIgnoreCase("CANCELLED"))
                .count();
    }

    public Map<String, Double> getRevenuePerDay() {
        ensureAdminAccess();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return orderRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOrderDate().format(formatter),
                        TreeMap::new,
                        Collectors.summingDouble(OrderProduct::getTotalPrice)
                ));
    }

	public long getRefundedOrdersCount() {
		ensureAdminAccess();
        return orderRepo.findAll().stream()
                .filter(o -> o.getOrderStatus().name().equalsIgnoreCase("RETURNED"))
                .count();
	}

    // ========================= VISITOR ANALYTICS METHODS =========================
    
    /**
     * Get total unique visitors count
     * Note: This is a placeholder implementation. 
     * You'll need to implement visitor tracking in your database first.
     */
    public long getTotalVisitors() {
        ensureAdminAccess();
        // TODO: Implement with visitor_analytics table
        // For now, return a sample calculation based on orders
        // This should be replaced with actual visitor count from visitor_analytics table
        return getTotalOrders() * 3; // Assuming 3 visitors per order (sample conversion rate ~33%)
    }

    /**
     * Get visitors by period (day, week, month)
     */
    public long getVisitorsByPeriod(String period) {
        ensureAdminAccess();
        // TODO: Implement with visitor_analytics table
        // For now, return sample data based on period
        switch (period.toLowerCase()) {
            case "day":
                return getTotalVisitors() / 30; // Daily average
            case "week":
                return getTotalVisitors() / 4;  // Weekly average
            case "month":
            default:
                return getTotalVisitors();
        }
    }

    /**
     * Get visitors per day for charts
     */
    public Map<String, Long> getVisitorsPerDay() {
        ensureAdminAccess();
        // TODO: Implement with visitor_analytics table
        // For now, generate sample data based on order patterns
        Map<String, Long> ordersByDay = getOrderCountPerDay();
        Map<String, Long> visitorsPerDay = new TreeMap<>();
        
        for (Map.Entry<String, Long> entry : ordersByDay.entrySet()) {
            // Sample calculation: assume 3-5 visitors per order
            long visitors = entry.getValue() * (3 + (entry.getKey().hashCode() % 3));
            visitorsPerDay.put(entry.getKey(), visitors);
        }
        
        return visitorsPerDay;
    }

    /**
     * Get comprehensive analytics summary
     */
    public Map<String, Object> getAnalyticsSummary() {
        ensureAdminAccess();
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("totalVisitors", getTotalVisitors());
        summary.put("dailyVisitors", getVisitorsByPeriod("day"));
        summary.put("weeklyVisitors", getVisitorsByPeriod("week"));
        summary.put("monthlyVisitors", getVisitorsByPeriod("month"));
        summary.put("uniqueVisitorsToday", getTodayVisitorsCount());
        
        return summary;
    }

    /**
     * Get today's visitor count
     */
    public long getTodayVisitorsCount() {
        ensureAdminAccess();
        // TODO: Implement with visitor_analytics table
        // For now, calculate based on today's orders
        long todayOrders = getTodayOrdersCount();
        return todayOrders * 4; // Sample: 4 visitors per order today
    }

    /**
     * Get conversion rate calculation
     */
    public double getConversionRate() {
        ensureAdminAccess();
        long totalVisitors = getTotalVisitors();
        long totalOrders = getTotalOrders();
        
        if (totalVisitors == 0) {
            return 0.0;
        }
        
        return ((double) totalOrders / totalVisitors) * 100;
    }

    /**
     * Get average order value
     */
    public double getAverageOrderValue() {
        ensureAdminAccess();
        long totalOrders = getTotalOrders();
        if (totalOrders == 0) {
            return 0.0;
        }
        return getTotalRevenue() / totalOrders;
    }

    /**
     * Get visitor growth rate (month over month)
     */
    public double getVisitorGrowthRate() {
        ensureAdminAccess();
        // TODO: Implement with actual previous period data
        // For now, return sample growth rate
        return 15.5; // Sample 15.5% growth
    }

    // ========================= HELPER METHODS FOR ENHANCED ANALYTICS =========================

    /**
     * Get peak visitor day
     */
    public String getPeakVisitorDay() {
        ensureAdminAccess();
        Map<String, Long> visitorsPerDay = getVisitorsPerDay();
        
        return visitorsPerDay.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    /**
     * Get average visitors per day
     */
    public double getAverageVisitorsPerDay() {
        ensureAdminAccess();
        Map<String, Long> visitorsPerDay = getVisitorsPerDay();
        
        if (visitorsPerDay.isEmpty()) {
            return 0.0;
        }
        
        return visitorsPerDay.values().stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Get total active days with visitors
     */
    public long getTotalVisitorDays() {
        ensureAdminAccess();
        return getVisitorsPerDay().size();
    }

    /**
     * Get maximum visitors in a single day
     */
    public long getMaxVisitorsPerDay() {
        ensureAdminAccess();
        return getVisitorsPerDay().values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
    }

    // ========================= NOTES FOR FUTURE IMPLEMENTATION =========================
    /*
     * TO IMPLEMENT REAL VISITOR TRACKING:
     * 
     * 1. Create visitor_analytics table:
     *    - id (PRIMARY KEY)
     *    - session_id (UNIQUE)
     *    - ip_address
     *    - user_agent
     *    - first_visit (TIMESTAMP)
     *    - last_visit (TIMESTAMP)
     *    - total_page_views
     * 
     * 2. Create VisitorAnalytics entity and repository
     * 
     * 3. Replace placeholder methods with actual database queries:
     *    - getTotalVisitors() -> count distinct visitors
     *    - getVisitorsPerDay() -> group by date
     *    - getVisitorsByPeriod() -> filter by date range
     * 
     * 4. Add visitor tracking endpoints in controller
     */
}
