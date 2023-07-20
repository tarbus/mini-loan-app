package com.loan.controllers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ LoanControllerTest.class, RepaymentControllerTest.class, UserControllerTest.class })
public class AllTests {

}
