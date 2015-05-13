package com.github.mmonkey.Relay;


public class ContactMethod {

	private ContactMethodTypes type;
	private String address;
	private Carriers carrier = Carriers.EMAIL;
	private String activationKey;
	private boolean isActivated = false;
	
	protected ContactMethodTypes getType() {
		return this.type;
	}
	
	protected void setType(ContactMethodTypes type) {
		this.type = type;
	}
	
	protected String getAddress() {
		return this.address;
	}
	
	protected void setAddress(String address) {
		this.address = address;
	}
	
	protected Carriers getCarrier() {
		return this.carrier;
	}
	
	protected void setCarrier(Carriers carrier) {
		this.carrier = carrier;
	}
	
	protected String getActivationKey() {
		return this.activationKey;
	}
	
	protected void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}
	
	protected boolean isActivated() {
		return this.isActivated;
	}
	
	protected void isActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}
	
	protected ContactMethod(ContactMethodTypes type, String address, Carriers carrier, String activationKey) {
		this.type = type;
		this.address = address;
		this.carrier = carrier;
		this.activationKey = activationKey;
	}
	
}
