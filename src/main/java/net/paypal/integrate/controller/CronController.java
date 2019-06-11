package net.paypal.integrate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cron")
public class CronController {

	   @RequestMapping(value="/user/revenue", method = RequestMethod.GET)
	   public ResponseEntity<String> processUserRevenue() {
	       System.out.println("HELOOOOOOOOOOOOOOOOOOOOO");
		   
		   return ResponseEntity.ok("OK");
	   }
}
