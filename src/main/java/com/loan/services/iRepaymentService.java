package com.loan.services;

import java.util.List;

import com.loan.dto.RepaymentDto;
import com.loan.models.Repayment;

public interface iRepaymentService {

	public Repayment addTransaction(RepaymentDto repaymentDto);

	public List<Repayment> getRepaymentsByUserId(int custId);
	
	public List<Repayment> getRepaymentsByLoanId(int loanId);
}
