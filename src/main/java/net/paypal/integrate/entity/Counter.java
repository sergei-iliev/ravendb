package net.paypal.integrate.entity;

import java.io.Serializable;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Counter implements Serializable{
	  @Id
	  private Long id;
	  
	  @Index
	  private Key<PayPalUser> userKey;
	  
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Key<PayPalUser> getUserKey() {
			return userKey;
		}

		public void setUserKey(Key<PayPalUser> userKey) {
			this.userKey = userKey;
		}


}
