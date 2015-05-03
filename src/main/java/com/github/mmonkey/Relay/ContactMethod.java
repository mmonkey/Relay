package com.github.mmonkey.Relay;

import com.github.mmonkey.Relay.Utilities.ContactMethodTypes;

public class ContactMethod {

	private ContactMethodTypes type;
	private String address;
	private Carriers carrier = Carriers.NO_CARRIER;
	
	public ContactMethodTypes getType() {
		return this.type;
	}
	
	public void setType(ContactMethodTypes type) {
		this.type = type;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public Carriers getCarrier() {
		return this.carrier;
	}
	
	public void setCarrier(Carriers carrier) {
		this.carrier = carrier;
	}
	
	public ContactMethod(ContactMethodTypes type, String address) {
		this.type = type;
		this.address = address;
	}
	
}
