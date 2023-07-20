package com.loan.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loan.models.Loan;
import com.loan.services.iLoanService;

@RestController
@RequestMapping("/api/loan")
public class LoanController {

	@Autowired(required = true)
	private iLoanService loanService;

	@PostMapping
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Loan> applyLoan(@RequestBody Loan loan) {
		return new ResponseEntity<Loan>(loanService.applyLoan(loan), HttpStatus.OK);
	}

	@GetMapping("/user/{id}")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<List<Loan>> getLoansByUserId(@PathVariable int id) {
		return new ResponseEntity<List<Loan>>(loanService.getLoansByUserId(id), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Loan> getLoanById(@PathVariable int id) {
		return new ResponseEntity<Loan>(loanService.getLoanById(id), HttpStatus.OK);
	}

	@DeleteMapping("/close/{loanId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<String> closeLoan(@PathVariable int loanId) {
		loanService.closeLoan(loanId);
		return new ResponseEntity<String>("Loan closed for Loan Id: " + loanId, HttpStatus.OK);
	}
	
	@PostMapping("/approve/{loanId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Loan> approveLoan(@PathVariable int loanId) {
		Loan loan = loanService.approveLoan(loanId);
		return new ResponseEntity<Loan>(loan, HttpStatus.OK);
	}

}
