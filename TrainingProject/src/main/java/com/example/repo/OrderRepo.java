package com.example.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.enums.OrderStatus;
import com.example.enums.PaymentStatus;
import com.example.model.OrderProduct;

@Repository
public interface OrderRepo extends JpaRepository<OrderProduct, Long> {


    @Query("SELECT o FROM OrderProduct o WHERE o.user.userId = :userId")
    List<OrderProduct> findByUser(@Param("userId") Long userId);

    void deleteAllByUser_UserId(Long userId);


    @Query("SELECT o FROM OrderProduct o WHERE o.orderStatus = :status")
    List<OrderProduct> findAllByOrderStatus(@Param("status") OrderStatus status);

    @Query("SELECT o FROM OrderProduct o WHERE o.paymentStatus = :paymentStatus")
    List<OrderProduct> findAllByPaymentStatus(@Param("paymentStatus") PaymentStatus paymentStatus);


}
