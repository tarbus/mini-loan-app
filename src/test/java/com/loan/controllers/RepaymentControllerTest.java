package com.loan.controllers;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
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
import com.loan.dao.RepaymentRepository;
import com.loan.dto.RepaymentDto;
import com.loan.enums.RepaymentStatus;
import com.loan.models.Loan;
import com.loan.models.Repayment;
import com.loan.services.iRepaymentService;

@WebMvcTest(RepaymentController.class)
@RunWith(SpringRunner.class)
class RepaymentControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	JwtService jwtService;
	
	@MockBean
	UserInfoUserDetailsService userInfoUserDetailsService;
	
	@MockBean
	iRepaymentService repaymentService;
	
	@MockBean
	RepaymentRepository repaymentDao;
	
	@MockBean
	LoanRepository loanDao;
	
	@Test
	@WithMockUser(username = "test@test.com", authorities = { "USER" })
	void testAddRepayment() throws Exception {
		
		RepaymentDto repay = new RepaymentDto();
		repay.setPaidAmount(BigDecimal.valueOf(35));
		repay.setRepaymentId(1);
		
		Repayment repayment = new Repayment();
		repayment.setStatus(RepaymentStatus.PENDING.name());
		Loan loan = new Loan();
		loan.setLoanId(123);
		repayment.setLoan(loan);
		Optional<Loan> loanEntity = Optional.of(loan);
		Mockito.when(loanDao.findById(any())).thenReturn(loanEntity);
		
		Optional<Repayment> repaymentEntity = Optional.of(repayment);
		Mockito.when(repaymentDao.findById(any())).thenReturn(repaymentEntity);
		
		Repayment responseRepayment = new Repayment();
		repayment.setStatus(RepaymentStatus.PAID.name());
		Mockito.when(repaymentDao.save(any())).thenReturn(responseRepayment);
		
		this.mvc.perform(
				post("/api/repayment")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.content(asJsonString(repay))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "test@test.com", authorities = { "USER" })
	void testGetRepaymentsByUserId() throws Exception {
		int userId=123;
		List<Repayment> repayments = new ArrayList<>();
		Repayment r1 = new Repayment();
		Repayment r2 = new Repayment();
		repayments.add(r1);
		repayments.add(r2);
		Mockito.when(repaymentDao.findRepaymentsByUserId(userId)).thenReturn(repayments);
		
		this.mvc.perform(
				get("/api/repayment/user/{user_id}", userId)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	
	@Test
	@WithMockUser(username = "test@test.com", authorities = { "USER" })
	void testGetRepaymentsByLoanId() throws Exception {
		int loanId=123;
		List<Repayment> repayments = new ArrayList<>();
		Repayment r1 = new Repayment();
		Repayment r2 = new Repayment();
		repayments.add(r1);
		repayments.add(r2);
		Mockito.when(repaymentDao.findRepaymentsByLoanId(loanId)).thenReturn(repayments);
		
		this.mvc.perform(
				get("/api/repayment/loan/{loan_id}", loanId)
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
