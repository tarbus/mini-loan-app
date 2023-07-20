package com.loan.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "loan")
@Data
@NoArgsConstructor
public class Loan implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int loanId;
	
	@Column(precision = 10, scale = 2)
	private BigDecimal loanAmt = BigDecimal.ZERO;
	
	private int term;
	private Timestamp requestedAt;
	private String status;
	
	@Column(precision = 10, scale = 2)
	private BigDecimal credit = BigDecimal.ZERO;
	
	private Timestamp startDate;

	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	
	@ManyToOne
	@JoinColumn(name = "approver_user_id")
	private User approvedBy;

	@JsonIgnore
	@OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Repayment> repayments = new ArrayList<Repayment>();
	
	public void addRepayment(Repayment repayment) {
		repayment.setLoan(this);
		this.getRepayments().add(repayment);
	}
}
