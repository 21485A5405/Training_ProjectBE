package com.example.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.enums.OrderStatus;
import com.example.enums.PaymentStatus;
import com.example.model.OrderItem;
import com.example.model.OrderProduct;
import com.example.model.Product;

public class GetOrders {

    private Long orderId;
    private Long userId;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private String shippingAddress;
    private double totalPrice;
    private PaymentStatus paymentStatus;
    private List<ProductInfo> products;

    public GetOrders(OrderProduct orderProduct, List<OrderItem> orderItems) {
        this.orderId = orderProduct.getOrderId();
        this.userId = orderProduct.getUser().getUserId();
        this.orderDate = orderProduct.getOrderDate();
        this.orderStatus = orderProduct.getOrderStatus();
        this.paymentStatus = orderProduct.getPaymentStatus();
        this.shippingAddress = orderProduct.getShippingAddress();
        this.totalPrice = orderProduct.getTotalPrice();

        this.products = orderItems.stream()
            .map(item -> {
                Product product = item.getProduct();
                return new ProductInfo(
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductPrice(),
                    item.getQuantity()
                );
            })
            .toList();
    }

    public static class ProductInfo {
        private Long productId;
        private String productName;
        private double productPrice;
        private int quantity;

        public ProductInfo(Long productId, String productName, double productPrice,int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
            this.quantity = quantity;
        }

        // Getters and setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public double getProductPrice() { return productPrice; }
        public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    // Getters and setters for outer class
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public List<ProductInfo> getProducts() { return products; }
    public void setProducts(List<ProductInfo> products) { this.products = products; }
}
