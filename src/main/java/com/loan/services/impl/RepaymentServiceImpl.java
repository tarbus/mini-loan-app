package com.loan.services.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.loan.dao.LoanRepository;
import com.loan.dao.RepaymentRepository;
import com.loan.dao.UserRepository;
import com.loan.dto.RepaymentDto;
import com.loan.enums.LoanStatus;
import com.loan.enums.RepaymentStatus;
import com.loan.exceptions.LoanNotFoundException;
import com.loan.exceptions.TransactionFailedException;
import com.loan.exceptions.TransactionNotFoundException;
import com.loan.exceptions.UserNotFoundException;
import com.loan.models.Loan;
import com.loan.models.Repayment;
import com.loan.models.User;
import com.loan.services.iRepaymentService;
import com.loan.services.util.SecurityContextUtil;

@Service
@Primary
public class RepaymentServiceImpl implements iRepaymentService {

	@Autowired
	private LoanRepository loanDao;
	
	@Autowired
	private UserRepository userDao;

	@Autowired
	private RepaymentRepository repaymentDao;

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public Repayment addTransaction(RepaymentDto repaymentDto) {
		
		int repaymentId = repaymentDto.getRepaymentId();
		Repayment repayment = repaymentDao.findById(repaymentId).orElseThrow(() -> new TransactionNotFoundException("No Repayment transaction found for: " + repaymentId));
		
		if (RepaymentStatus.PAID.name().equals(repayment.getStatus())) {
			throw new TransactionFailedException("Repayment is already in PAID state");
		}
		if (!SecurityContextUtil.verifyUserFromContext(repayment.getUser().getEmail())) {
			throw new AccessDeniedException("Transction is of different user and not allowed");
		}
		
		repayment.setPaidDate(new Timestamp(System.currentTimeMillis()));
		repayment.setPaidAmount(repaymentDto.getPaidAmount());
		repayment.setStatus(RepaymentStatus.PAID.name());
		
		int loanId = repayment.getLoan().getLoanId();
		Loan loan = loanDao.findById(loanId).orElseThrow(() -> new LoanNotFoundException("Loan Not Found: " + loanId));
		BigDecimal credits = loan.getCredit();
		BigDecimal updatedCredits = credits.add( repayment.getPaidAmount().subtract(repayment.getRepayAmount()) );
		loan.setCredit(updatedCredits);
		
		loan.setStatus(LoanStatus.PAID.name());
		for(Repayment loanRepayment : loan.getRepayments()) {
			if (loanRepayment.getStatus().equals(RepaymentStatus.PENDING.name())) {
				loan.setStatus(LoanStatus.PENDING.name());
				break;
			}
		}
		
		try {
			return repaymentDao.save(repayment);
		} catch (Exception e) {
			throw new TransactionFailedException("Transaction Failed for LoanId: " + loanId);
		}
	}

	@Override
	public List<Repayment> getRepaymentsByUserId(int userId) {
		User user = userDao.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
		
		if (!SecurityContextUtil.verifyUserFromContext(user.getEmail())) {
			throw new AccessDeniedException("User in authroization context is different");
		}
		try {
			List<Repayment> repayments = repaymentDao.findRepaymentsByUserId(userId);
			return repayments;
		} catch (Exception e) {
			throw new TransactionNotFoundException("Transactions not Found for User Id: " + userId);
		}
	}

	@Override
	public List<Repayment> getRepaymentsByLoanId(int loanId) {
		Loan loan = loanDao.findById(Integer.valueOf(loanId))
				.orElseThrow(() -> new LoanNotFoundException("Loan not found: " + loanId));
		if (!SecurityContextUtil.verifyUserFromContext(loan.getUser().getEmail())) {
			throw new AccessDeniedException("User in authroization context is different");
		}
		try {
			List<Repayment> repayments = repaymentDao.findRepaymentsByLoanId(loanId);
			return repayments;
		} catch (Exception e) {
			throw new TransactionNotFoundException("Transactions not Found for Loan Id: " + loanId);
		}
	}
	
	

}
