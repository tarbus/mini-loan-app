package com.loan.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repayment")
@Data
@NoArgsConstructor
public class Repayment implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int repaymentId;
	private Timestamp repayDate;
	private Timestamp paidDate;
	
	@Column(precision = 10, scale = 2)
	private BigDecimal repayAmount = BigDecimal.ZERO;
	
	@Column(precision = 10, scale = 2)
	private BigDecimal paidAmount = BigDecimal.ZERO;
	
	private String status;
	
	
	@ManyToOne
	@JoinColumn(name = "loan_id")
	private Loan loan;
	
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
}
