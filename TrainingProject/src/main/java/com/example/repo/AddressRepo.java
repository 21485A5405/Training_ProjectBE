package com.example.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Address;

import jakarta.transaction.Transactional;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {

	    @Transactional
	    void deleteAllByUser_UserId( Long userId);
	 	
	 	List<Address> findAllByUser_UserId(Long userId);
}
