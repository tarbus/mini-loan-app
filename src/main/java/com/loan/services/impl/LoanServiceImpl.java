package com.loan.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.loan.dao.LoanRepository;
import com.loan.dao.UserRepository;
import com.loan.enums.LoanStatus;
import com.loan.enums.RepaymentStatus;
import com.loan.exceptions.LoanNotFoundException;
import com.loan.exceptions.LoanStatusException;
import com.loan.exceptions.UserNotFoundException;
import com.loan.models.Loan;
import com.loan.models.Repayment;
import com.loan.models.User;
import com.loan.services.iLoanService;
import com.loan.services.util.SecurityContextUtil;

@Service
@Primary
public class LoanServiceImpl implements iLoanService {

	@Autowired
	private LoanRepository loanDao;

	@Autowired
	private UserRepository userDao;
	
	@Value("${loan.repayments.intervalDays:7}")
	private int intervalDays;

	private Logger logger = Logger.getLogger(getClass());

	public Loan applyLoan(Loan loan) {
		String userName = SecurityContextUtil.getUserNameFromContext();
		Optional<User> userEntity = userDao.findUserByEmail(userName);
		User user = userEntity.get();
		loan.setRequestedAt(new Timestamp(System.currentTimeMillis()));
		loan.setStatus("PENDING");
		loan.setCredit(BigDecimal.ZERO);
		loan.setUser(user);
		
		//user.addLoan(loan);
		return loanDao.save(loan);
	}

	@Override
	public List<Loan> getLoansByUserId(int userId) {
		User user = userDao.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
		if (!SecurityContextUtil.verifyUserFromContext(user.getEmail())) {
			throw new AccessDeniedException("User is not allowed to view the loans for userId: " + userId);
		}
		return user.getLoans();
	}
	
	@Override
	public Loan getLoanById(int loanId) {
		Loan loan = loanDao.findById(Integer.valueOf(loanId))
				.orElseThrow(() -> new LoanNotFoundException("Loan not found: " + loanId));
		if (!SecurityContextUtil.verifyUserFromContext(loan.getUser().getEmail())) {
			throw new AccessDeniedException("User is not allowed to view the loan id: " + loanId);
		}
		return loan;
	}

	@Override
	public void closeLoan(int loanId) {
		Loan loan = loanDao.findById(loanId)
				.orElseThrow(() -> new LoanNotFoundException("Loan Not Found: " + loanId));
		loanDao.delete(loan);
	}

	@Override
	public Loan approveLoan(int loanId) {
		Loan loan = loanDao.findById(loanId).orElseThrow(() -> new LoanNotFoundException("Loan Not Found: " + loanId));
		if (LoanStatus.APPROVED.name().equals(loan.getStatus()) || LoanStatus.PAID.name().equals(loan.getStatus()) ) {
			throw new LoanStatusException("Loan is not in Pending state to be approved");
		}
		
		loan.setStatus(LoanStatus.APPROVED.toString());
		
		String approverUser = SecurityContextUtil.getUserNameFromContext();
		Optional<User> approverUserEntity = userDao.findUserByEmail(approverUser);
		User user = approverUserEntity.get();
		loan.setApprovedBy(user);
		
		loan.setStartDate(new Timestamp(System.currentTimeMillis()));
		
		int terms = loan.getTerm();
		if (terms<=0) {
			throw new IllegalArgumentException("Term value should be more than 0");
		}
		BigDecimal amountPerTerm = loan.getLoanAmt().divide(BigDecimal.valueOf(terms), 2, RoundingMode.HALF_UP);
		long intervalDaysInMilli = intervalDays*24*60*60*1000;
		List<Repayment> repayments = new ArrayList<>();
		for(int i=1; i<=terms; i++) {
			Repayment repayment = new Repayment();
			repayment.setRepayAmount(amountPerTerm);
			repayment.setRepayDate(new Timestamp(System.currentTimeMillis() + intervalDaysInMilli*i));
			repayment.setStatus(RepaymentStatus.PENDING.toString());
			repayment.setLoan(loan);
			repayment.setUser(loan.getUser());
			repayments.add(repayment);
		}
		loan.setRepayments(repayments);
		
		return loanDao.save(loan);
	}

}
