package net.paypal.integrate.command.invoice;

public class Payee {
	private String fullName;
	private String address;
	private String countryCode;
	private String email;
		
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	//demo account
	public static Payee create(String email,String countryCode){
		Payee p=new Payee();
		p.setEmail(email);
		p.setFullName("Lucas Deroni");
		p.setCountryCode(countryCode);
		p.setAddress("Donatelo minicipality str 14, Minesota");	
		return p;
	}
 
 
 
 
 
}
