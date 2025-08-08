
package com.example.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.dto.GetOrders;
import com.example.dto.PlaceOrder;
import com.example.enums.OrderStatus;
import com.example.enums.PaymentStatus;
import com.example.exception.*;
import com.example.model.*;
import com.example.repo.*;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final CartItemRepo cartItemRepo;
    private final AddressRepo addressRepo;
    private final CurrentUser currentUser;
    private final OrderItemRepo orderItemRepo;

    public OrderServiceImpl(UserRepo userRepo, OrderItemRepo orderItemRepo, CurrentUser currentUser,
                            CartItemRepo cartItemRepo, ProductRepo productRepo, OrderRepo orderRepo,
                            AddressRepo addressRepo) {
        this.userRepo = userRepo;
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.addressRepo = addressRepo;
        this.currentUser = currentUser;
        this.orderItemRepo = orderItemRepo;
    }
    
    @Transactional
    public ResponseEntity<ApiResponse<List<GetOrders>>> placeOrder(List<PlaceOrder> orderDetailsList) {
        User currUser = currentUser.getUser();
        if (currUser == null) {
            throw new UnAuthorizedException("Please Login");
        }

        // Ensure managed user entity
        User managedUser = userRepo.findById(currUser.getUserId())
            .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        if (orderDetailsList == null || orderDetailsList.isEmpty()) {
            throw new CustomException("Order list cannot be empty");
        }

        // 1. Use the first address from the list (assuming single address per order)
        Address address = addressRepo.findById(orderDetailsList.get(0).getAddressId())
            .orElseThrow(() -> new CustomException("Address not found with ID: " + orderDetailsList.get(0).getAddressId()));

        // 2. Create the OrderProduct
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setUser(managedUser);
        orderProduct.setOrderDate(LocalDateTime.now());
        orderProduct.setOrderStatus(OrderStatus.PENDING);
        orderProduct.setPaymentStatus(PaymentStatus.PENDING);
        orderProduct.setShippingAddress(address.getFullAddress());

        double totalOrderPrice = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (PlaceOrder placeOrder : orderDetailsList) {
            Product product = productRepo.findById(placeOrder.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + placeOrder.getProductId()));

            if (product.getProductQuantity() < placeOrder.getQuantity()) {
                throw new CustomException("Not enough stock for product: " + product.getProductName());
            }

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(orderProduct); // associate to main order
            orderItem.setProduct(product);
            orderItem.setQuantity(placeOrder.getQuantity());

            orderItems.add(orderItem);

            // Calculate total
            totalOrderPrice += product.getProductPrice() * placeOrder.getQuantity();

            // Deduct stock
            product.setProductQuantity(product.getProductQuantity() - placeOrder.getQuantity());
            productRepo.save(product);
            CartItem cart = cartItemRepo.findByUserAndProduct(managedUser.getUserId(), product.getProductId()).orElse(null);
            if (cart != null) {
                if (cart.getProductQuantity() == placeOrder.getQuantity()) {
                    cartItemRepo.delete(cart);
                } else if (cart.getProductQuantity() > placeOrder.getQuantity()) {
                    cart.setProductQuantity(cart.getProductQuantity() - placeOrder.getQuantity());
                    cartItemRepo.save(cart);
                } else {
                    throw new CustomException("Cart quantity is less than ordered quantity for product: " + product.getProductName());
                }
            }
        }
        orderProduct.setTotalPrice(totalOrderPrice);

        // Save main order and items
        orderRepo.save(orderProduct);
        orderItemRepo.saveAll(orderItems);
        
        // Prepare response
        GetOrders getOrders = new GetOrders(orderProduct, orderItems);
        List<GetOrders> orderResponseList = List.of(getOrders);

        ApiResponse<List<GetOrders>> response = new ApiResponse<>();
        response.setMessage("Order placed successfully");
        response.setData(orderResponseList);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<GetOrders>> getOrderByUser(Long userId) {
        User currUser = currentUser.getUser();
        if (currUser == null) throw new UnAuthorizedException("Please Login");

        if (!userRepo.findById(userId).isPresent()) {
            throw new UserNotFoundException("User Not Found");
        }

        List<OrderProduct> orders = orderRepo.findByUser(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<GetOrders>());
        }

        List<GetOrders> dtoList = new ArrayList<>();
        for (OrderProduct order : orders) {
            dtoList.add(new GetOrders(order, order.getItems()));
        }

        
        return ResponseEntity.ok(dtoList);
    }

    @Transactional
    public ResponseEntity<ApiResponse<OrderProduct>> cancelOrder(Long orderId) {
        User currUser = currentUser.getUser();
        if (currUser == null) throw new UnAuthorizedException("Please Login");

        OrderProduct order = orderRepo.findById(orderId)
            .orElseThrow(() -> new CustomException("Order Not Found"));

        if (!order.getUser().getUserId().equals(currUser.getUserId())) {
            throw new UnAuthorizedException("Not Authorized to Cancel This Order");
        }

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new CustomException("Order is Delivered");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setProductQuantity(product.getProductQuantity() + item.getQuantity());
            productRepo.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.REFUND_INITIATED);
        orderRepo.save(order);

        ApiResponse<OrderProduct> response = new ApiResponse<>();
        response.setMessage("Order cancelled successfully");

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<List<GetOrders>>> getAllOrders() {
        List<OrderProduct> orderList = orderRepo.findAll();
        if (orderList.isEmpty()) throw new CustomException("No Order Found");

        List<GetOrders> dtoList = new ArrayList<>();
        for (OrderProduct order : orderList) {
            dtoList.add(new GetOrders(order, order.getItems()));
        }

        ApiResponse<List<GetOrders>> response = new ApiResponse<>();
        response.setData(dtoList);
        response.setMessage("All Orders Details");

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<GetOrders>> getByOrderId(Long orderId) {
        User currUser = currentUser.getUser();
        if (currUser == null) throw new UnAuthorizedException("Please Login");

        OrderProduct order = orderRepo.findById(orderId)
            .orElseThrow(() -> new UserNotFoundException("Order Not Found with Given ID"));

        return ResponseEntity.ok(List.of(new GetOrders(order, order.getItems())));
    }


	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderStatus(OrderStatus status) {
		
		List<OrderProduct> orders = orderRepo.findAllByOrderStatus(status);
		if(orders.isEmpty()) {
			throw new CustomException("No Order Found with Order Status "+status);
		}
		
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orders);
		response.setMessage("Order Details with Order Status "+status);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderByPayment(PaymentStatus paymentStatus) {
		
		List<OrderProduct> orders = orderRepo.findAllByPaymentStatus(paymentStatus);
		if(orders.isEmpty()) {
			throw new CustomException("No Orders Found With Payment Status "+paymentStatus);
		}
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orders);
		response.setMessage("Order Details with Payment Status "+paymentStatus);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<OrderProduct>> updateOrderStatus(Long orderId, OrderStatus status) {
		
		Optional<OrderProduct> orderExists = orderRepo.findById(orderId);
		
		orderExists.get().setOrderStatus(status);
		orderRepo.save(orderExists.get());
		ApiResponse<OrderProduct> response = new ApiResponse<>();
		response.setData(orderExists.get());
		response.setMessage(" Order Status for"+" Order ID "+orderExists.get().getOrderId() +" Updated Sucessfully");
		return ResponseEntity.ok(response);
	}


	public ResponseEntity<ApiResponse<OrderProduct>> updatePaymentStatus(Long orderId, PaymentStatus status) {
		
		Optional<OrderProduct> orderExists = orderRepo.findById(orderId);
		
		orderExists.get().setPaymentStatus(status);
		orderRepo.save(orderExists.get());
		ApiResponse<OrderProduct> response = new ApiResponse<>();
		response.setData(orderExists.get());
		response.setMessage(" Payment Status for"+" Order ID "+orderExists.get().getOrderId() +" Updated Sucessfully");
		return ResponseEntity.ok(response);
	}

}