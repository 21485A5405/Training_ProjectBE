
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
        if (currUser == null) throw new UnAuthorizedException("Please Login");

        List<GetOrders> placedOrderDTOs = new ArrayList<>();

        Map<Long, List<PlaceOrder>> groupedOrders = orderDetailsList.stream()
            .collect(Collectors.groupingBy(PlaceOrder::getAddressId));

        for (Map.Entry<Long, List<PlaceOrder>> entry : groupedOrders.entrySet()) {
            Long addressId = entry.getKey();
            List<PlaceOrder> group = entry.getValue();

            Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new CustomException("Address Not Found"));

            if (!address.getUser().getUserId().equals(currUser.getUserId())) {
                throw new CustomException("Address Not Matched");
            }

            List<OrderItem> orderItems = new ArrayList<>();
            double totalPrice = 0;

            OrderProduct order = new OrderProduct();
            order.setUser(currUser);
            order.setOrderDate(LocalDateTime.now());
            order.setShippingAddress(address.getFullAddress());
            order.setOrderStatus(OrderStatus.PENDING);
            order.setPaymentStatus(PaymentStatus.PENDING);

            for (PlaceOrder details : group) {
                Product product = productRepo.findById(details.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product Not Available"));

                CartItem cartItem = cartItemRepo.findByUserAndProduct(details.getUserId(), details.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Please Add Product into Cart to place Order"));

                if (!currUser.getUserId().equals(details.getUserId())) {
                    throw new UnAuthorizedException("Not Authorized to Place Order with Another User ID");
                }

                if (cartItem.getProductQuantity() < details.getQuantity()) {
                    throw new CustomException("Selected Quantity is Greater Than Your Cart Quantity");
                }

                List<PaymentInfo> payment = currUser.getPaymentDetails();
                if (payment == null || payment.isEmpty()) {
                    throw new CustomException("Payment Method Cannot be Empty");
                }

                boolean isValid = payment.stream()
                    .anyMatch(info -> info.getPaymentMethod() == details.getPaymentType());

                if (!isValid) {
                    throw new UnAuthorizedException("Selected Payment Method Not Available. Available: "
                        + currUser.displayPayments());
                }

                int newStock = product.getProductQuantity() - details.getQuantity();
                if (newStock < 0) throw new CustomException("Out Of Stock.");
                product.setProductQuantity(newStock);
                productRepo.save(product);

                int remainingQty = cartItem.getProductQuantity() - details.getQuantity();
                if (remainingQty <= 0) {
                    cartItemRepo.delete(cartItem);
                } else {
                    cartItem.setProductQuantity(remainingQty);
                    cartItem.setTotalPrice(remainingQty * product.getProductPrice());
                    cartItemRepo.save(cartItem);
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(details.getQuantity());

                orderItems.add(orderItem);
                totalPrice += details.getQuantity() * product.getProductPrice();
            }

            order.setItems(orderItems);
            order.setTotalPrice(totalPrice);
            orderRepo.save(order);

            placedOrderDTOs.add(new GetOrders(order, orderItems));
        }

        ApiResponse<List<GetOrders>> response = new ApiResponse<>();
        response.setData(placedOrderDTOs);
        response.setMessage("Orders Placed Successfully");

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
            throw new UserNotFoundException("No Order Details Found with Given User ID");
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
    
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getByUserIdAndProductId(Long userId, Long productId) {
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		List<OrderProduct> orders = orderRepo.findAllByUserAndProduct(userId, productId);

		if(orders.isEmpty()) {
			throw new ProductNotFoundException("Orders Not Found With This UserID "+userId+" and ProductID "+productId);
		}
		return ResponseEntity.ok(response);
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