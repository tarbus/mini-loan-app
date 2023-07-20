package com.loan.services.impl;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.loan.dao.UserRepository;
import com.loan.exceptions.UserAlreadyRegisteredException;
import com.loan.models.User;
import com.loan.services.iUserService;

@Service
@Primary
public class UserServiceImpl implements iUserService {

	@Autowired
	private UserRepository userDao;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public User addUser(User user) {
		Optional<User> existingUser = userDao.findUserByEmail(user.getEmail());
		if (existingUser.isPresent()) {
			throw new UserAlreadyRegisteredException("User Already Registered: " + user.getId());
		}
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userDao.save(user);
	}

}
