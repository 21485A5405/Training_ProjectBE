package com.example.service;

import java.util.List;
import org.springframework.http.ResponseEntity;

import com.example.controller.ApiResponse;
import com.example.dto.LoginDetails;
import com.example.dto.LoginDisplay;
import com.example.dto.RegisterUser;
import com.example.dto.UpdateUser;
import com.example.model.User;

public interface AdminService {
	
	public ResponseEntity<ApiResponse<User>> createAdmin(RegisterUser admin) ;

	public ResponseEntity<ApiResponse<User>> getAdminById(Long adminId);

	public ResponseEntity<ApiResponse<List<User>>> getAllAdmins();

	List<Long> getAllUserIds();

	public ResponseEntity<LoginDisplay> loginAdmin(LoginDetails details);

	public ResponseEntity<String> logOut();
	
	public ResponseEntity<UpdateUser> getDetails(Long userId);

}
