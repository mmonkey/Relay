package com.github.mmonkey.Relay.Email;

public class EmailHeaderSection extends EmailSection {
	
	String invisibleIntroduction = "";
	
	public void setInvisibleIntroduction(String invisibleIntroduction) {
		this.invisibleIntroduction = invisibleIntroduction;
	}
	
	public EmailHeaderSection(String invisibleIntroduction) {
		this.invisibleIntroduction = invisibleIntroduction;
	}
	
	public EmailHeaderSection() {
	}

}
