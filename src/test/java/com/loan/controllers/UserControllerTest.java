package com.loan.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.config.service.JwtService;
import com.loan.config.service.UserInfoUserDetailsService;
import com.loan.models.User;
import com.loan.services.iUserService;

@WebMvcTest(UserController.class)
@RunWith(SpringRunner.class)
public class UserControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	JwtService jwtService;
	
	@MockBean
	UserInfoUserDetailsService userInfoUserDetailsService;
	
	@MockBean
	iUserService userService;
	
	
	@Test
	@WithMockUser(username = "admin@test.com", authorities = { "ADMIN" })
	public void testAddUser() throws Exception {
		User user = new User();
		user.setName("Test User");
		user.setEmail("user@test.com");
		user.setPassword("password");
		user.setRole("USER");
		
		User userResponse = new User();
		userResponse.setId(1);
		userResponse.setEmail("user@test.com");
		
		Mockito.when(userService.addUser(user)).thenReturn(userResponse);
		
		this.mvc.perform(
				post("/api/user/add")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.content(asJsonString(user))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void testAddUserUnAuthorized() throws Exception {
		User user = new User();
		
		User userResponse = new User();
		
		Mockito.when(userService.addUser(user)).thenReturn(userResponse);
		
		this.mvc.perform(
				post("/api/user/add")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.content(asJsonString(user))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
	
	public static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}

}
