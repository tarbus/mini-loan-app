package com.loan.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.loan.models.Repayment;

@Repository
public interface RepaymentRepository extends JpaRepository<Repayment, Integer> {

	@Query("select t from Repayment t inner join Loan l on l.loanId=t.loan.loanId where l.user.id=?1")
	List<Repayment> findRepaymentsByUserId(int userId);
	
	@Query("select t from Repayment t inner join Loan l on l.loanId=t.loan.loanId where l.id=?1")
	List<Repayment> findRepaymentsByLoanId(int loanId);

}
