package com.loan.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RepaymentDto {
	
	private int repaymentId;
	private BigDecimal paidAmount;

}
