package com.loan.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loan.models.User;
import com.loan.services.iUserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired(required = true)
	iUserService userService;

	private Logger logger = Logger.getLogger(getClass());

	// Adding User by admin
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<User> addUser(@RequestBody User c) {
		return new ResponseEntity<User>(userService.addUser(c), HttpStatus.OK);
	}
	

}
