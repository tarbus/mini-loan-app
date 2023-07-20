package com.loan.services.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtil {
	
	public static String getUserNameFromContext() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
	
	public static boolean verifyUserFromContext(String userEmail) {
		String contextUser = SecurityContextHolder.getContext().getAuthentication().getName();
		return userEmail.equals(contextUser);
	}

}
