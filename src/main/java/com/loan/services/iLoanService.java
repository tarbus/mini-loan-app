package com.loan.services;

import java.util.List;

import com.loan.models.Loan;

public interface iLoanService {

	public Loan applyLoan(Loan l);

	public List<Loan> getLoansByUserId(int custId);
	
	public Loan getLoanById(int loanId);

	public void closeLoan(int loanId);
	
	public Loan approveLoan(int loanId);

}
