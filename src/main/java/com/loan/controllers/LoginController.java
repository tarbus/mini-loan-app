package com.loan.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loan.config.service.JwtService;
import com.loan.dto.UserLoginDto;
import com.loan.exceptions.UserNotFoundException;
import com.loan.services.iUserService;

@RestController
@RequestMapping("/api/user")
public class LoginController {
	
	@Autowired(required = true)
	iUserService userService;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	// User Login
	@PostMapping("/login")
	public String doLogin(@RequestBody UserLoginDto user) {
		Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
		if (auth.isAuthenticated()) {
			return jwtService.generateToken(user.getEmail());
		} else {
			throw new UserNotFoundException("invalid user login");
		}
	}

}
