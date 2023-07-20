package com.loan.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
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
import com.loan.dao.LoanRepository;
import com.loan.dao.UserRepository;
import com.loan.enums.LoanStatus;
import com.loan.models.Loan;
import com.loan.models.User;
import com.loan.services.iLoanService;

@WebMvcTest(LoanController.class)
@RunWith(SpringRunner.class)
class LoanControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	JwtService jwtService;
	
	@MockBean
	UserInfoUserDetailsService userInfoUserDetailsService;
	
	@MockBean
	iLoanService loanService;
	
	@MockBean
	LoanRepository loanDao;
	
	@MockBean
	UserRepository userDao;
	
	@Test
	@WithMockUser(username = "test@test.com", authorities = { "USER" })
	void testApplyLoan() throws Exception {
		Loan loan = new Loan();
		loan.setLoanAmt(BigDecimal.valueOf(100));
		loan.setTerm(3);
		
		Loan responseLoan = new Loan();
		responseLoan.setLoanId(1);
		responseLoan.setStatus(LoanStatus.PENDING.name());
		
		Mockito.when(loanService.applyLoan(loan)).thenReturn(responseLoan);
		
		this.mvc.perform(
				post("/api/loan")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.content(asJsonString(loan))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.loanId").exists());
	}
	
	@Test
	@WithMockUser(username = "test@test.com", authorities = { "USER" })
	void testGetLoansByUserId() throws Exception {
		Loan loan1 = new Loan();
		loan1.setLoanAmt(BigDecimal.valueOf(50));
		loan1.setTerm(3);
		
		Loan loan2 = new Loan();
		loan2.setLoanAmt(BigDecimal.valueOf(100));
		loan2.setTerm(5);
		
		List<Loan> loanList = new ArrayList<>();
		loanList.add(loan1);
		loanList.add(loan2);
		
		int userId = 123;
		Mockito.when(loanService.getLoansByUserId(userId)).thenReturn(loanList);
		
		this.mvc.perform(
				get("/api/loan/user/{user_id}", userId)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(2)));
	}
	
	@Test
	@WithMockUser(username = "test@test.com", authorities = { "USER" })
	void testGetLoanById() throws Exception {
		Loan loan = new Loan();
		loan.setLoanId(123);
		loan.setLoanAmt(BigDecimal.valueOf(50));
		loan.setTerm(3);
		
		int loanId = 123;
		Mockito.when(loanService.getLoanById(loanId)).thenReturn(loan);
		
		this.mvc.perform(
				get("/api/loan/{loan_id}", loanId)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.loanId").value(123));
	}
	
	@Test
	@WithMockUser(username = "admin@test.com", authorities = { "ADMIN" })
	void testCloseLoan() throws Exception {
		int loanId = 123;
		Optional<Loan> loan = Optional.of(Mockito.mock(Loan.class));
		Mockito.when(loanDao.findById(loanId)).thenReturn(loan);
		
		this.mvc.perform(
				delete("/api/loan/close/{loan_id}", loanId)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username = "admin@test.com", authorities = { "ADMIN" })
	void testApproveLoan() throws Exception {
		int loanId = 123;
		Optional<Loan> loanEntity = Optional.of(new Loan());
		Loan loan = loanEntity.get();
		loan.setLoanId(loanId);
		loan.setTerm(3);
		loan.setLoanAmt(BigDecimal.valueOf(100));
		loan.setStatus(LoanStatus.PENDING.name());
		Mockito.when(loanDao.findById(loanId)).thenReturn(loanEntity);
		
		Optional<User> approverUser = Optional.of(Mockito.mock(User.class));
		
		Mockito.when(userDao.findUserByEmail(any())).thenReturn(approverUser);
		
		Optional<Loan> responseLoanEntity = Optional.of(new Loan());
		Loan responseLoan = responseLoanEntity.get();
		responseLoan.setStatus(LoanStatus.APPROVED.name());
		Mockito.when(loanDao.save(any())).thenReturn(loanEntity);
		
		this.mvc.perform(
				post("/api/loan/approve/{loan_id}", loanId)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	public static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}

}
