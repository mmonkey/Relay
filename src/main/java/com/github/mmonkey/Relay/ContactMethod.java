package com.github.mmonkey.Relay;

import com.github.mmonkey.Relay.Utilities.ContactMethodTypes;

public class ContactMethod {

	private ContactMethodTypes type;
	private String address;
	private Carriers carrier = Carriers.EMAIL;
	private String activationKey;
	boolean isActivated = false;
	
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
	
	public String getActivationKey() {
		return this.activationKey;
	}
	
	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}
	
	public boolean isActivated() {
		return this.isActivated;
	}
	
	public void isActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}
	
	public ContactMethod(ContactMethodTypes type, String address, Carriers carrier, String activationKey) {
		this.type = type;
		this.address = address;
		this.carrier = carrier;
		this.activationKey = activationKey;
	}
	
}
