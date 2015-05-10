package com.github.mmonkey.Relay.Email;

public class EmailHeaderSection extends EmailSection {
	
	public String invisibleIntroduction = "";
	public String serverName;
	public String serverAddress;
	
	public void setInvisibleIntroduction(String invisibleIntroduction) {
		this.invisibleIntroduction = invisibleIntroduction;
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	public EmailHeaderSection(String serverName) {
		this.serverName = serverName;
	}

}
