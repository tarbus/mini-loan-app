package com.loan.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loan.dto.RepaymentDto;
import com.loan.models.Repayment;
import com.loan.services.iRepaymentService;

@RestController
@RequestMapping("/api/repayment")
public class RepaymentController {

	@Autowired(required = true)
	private iRepaymentService repaymentService;

	@PostMapping
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Repayment> addRepayment(@RequestBody RepaymentDto repaymentDto) {
		return new ResponseEntity<Repayment>(repaymentService.addTransaction(repaymentDto), HttpStatus.OK);
	}

	@GetMapping("/user/{userId}")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<List<Repayment>> getRepaymentsByUserId(@PathVariable int userId) {
		return new ResponseEntity<List<Repayment>>(repaymentService.getRepaymentsByUserId(userId), HttpStatus.OK);
	}
	
	@GetMapping("/loan/{loanId}")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<List<Repayment>> getRepaymentsByLoanId(@PathVariable int loanId) {
		return new ResponseEntity<List<Repayment>>(repaymentService.getRepaymentsByLoanId(loanId), HttpStatus.OK);
		
	}
}
