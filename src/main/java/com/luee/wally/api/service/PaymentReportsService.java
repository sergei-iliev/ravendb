package com.luee.wally.api.service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Logger;

import com.luee.wally.admin.repository.PaymentReportsRepository;
import com.luee.wally.entity.PaymentAmount;

public class PaymentReportsService {
	private final Logger logger = Logger.getLogger(PaymentReportsService.class.getName());

	
	public PaymentAmount getPaymentReportForYesterday(){
		   ZonedDateTime now=ZonedDateTime.now();
		   ZonedDateTime yesterday=now.minusDays(1);
		   
		   ZonedDateTime yesterdayStart=yesterday.truncatedTo(ChronoUnit.DAYS);
		   ZonedDateTime yesterdayEnd=yesterdayStart.plusHours(24);
		   
		   PaymentReportsRepository paymentReportsRepository=new PaymentReportsRepository();
		   return  paymentReportsRepository.getPaymentReports(Date.from(yesterdayStart.toInstant()), Date.from(yesterdayEnd.toInstant()));
		
	}

}
