package com.example.advicemethods;
import com.example.enums.Role;

public class IsAuthorized {
	
	public static boolean isAdmin(Role role) {
		
		return Role.ADMIN == role;
	}

}
