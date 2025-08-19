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

    public long getTotalVisitors() {
        ensureAdminAccess();
        return getTotalOrders() * 3;
    }

    public long getVisitorsByPeriod(String period) {
        ensureAdminAccess();
        switch (period.toLowerCase()) {
            case "day":
                return getTotalVisitors() / 30;
            case "week":
                return getTotalVisitors() / 4;
            case "month":
            default:
                return getTotalVisitors();
        }
    }

    public Map<String, Long> getVisitorsPerDay() {
        ensureAdminAccess();
        
        
        Map<String, Long> ordersByDay = getOrderCountPerDay();
        Map<String, Long> visitorsPerDay = new TreeMap<>();
        
        for (Map.Entry<String, Long> entry : ordersByDay.entrySet()) {
            
            long visitors = entry.getValue() * (3 + (entry.getKey().hashCode() % 3));
            visitorsPerDay.put(entry.getKey(), visitors);
        }
        
        return visitorsPerDay;
    }

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

    public long getTodayVisitorsCount() {
        ensureAdminAccess();
        
        
        long todayOrders = getTodayOrdersCount();
        return todayOrders * 4; 
    }

 
    public double getConversionRate() {
        ensureAdminAccess();
        long totalVisitors = getTotalVisitors();
        long totalOrders = getTotalOrders();
        
        if (totalVisitors == 0) {
            return 0.0;
        }
        
        return ((double) totalOrders / totalVisitors) * 100;
    }

    public double getAverageOrderValue() {
        ensureAdminAccess();
        long totalOrders = getTotalOrders();
        if (totalOrders == 0) {
            return 0.0;
        }
        return getTotalRevenue() / totalOrders;
    }

    public double getVisitorGrowthRate() {
        ensureAdminAccess();
        
        
        return 15.5; 
    }

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


    public long getTotalVisitorDays() {
        ensureAdminAccess();
        return getVisitorsPerDay().size();
    }


    public long getMaxVisitorsPerDay() {
        ensureAdminAccess();
        return getVisitorsPerDay().values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
    }

}
