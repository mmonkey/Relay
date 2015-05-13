package com.github.mmonkey.Relay;

public class Gateway {

	private String name;
	private String emailAddress;
	private String username;
	private String password;
	private String host;
	private int port;
	private boolean ssl;
	
	protected String getName() {
		return this.name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	protected String getEmailAddress() {
		return this.emailAddress;
	}
	
	protected void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	protected String getUsername() {
		return this.username;
	}
	
	protected void setUsername(String username) {
		this.username = username;
	}
	
	protected String getPassword() {
		return this.password;
	}
	
	protected void setPassword(String password) {
		this.password = password;
	}
	
	protected String getHost() {
		return this.host;
	}
	
	protected void setHost(String host) {
		this.host = host;
	}
	
	protected int getPort() {
		return this.port;
	}
	
	protected void setPort(int port) {
		this.port = port;
	}
	
	protected boolean sslEnabled() {
		return this.ssl;
	}
	
	protected void sslEnabled(boolean enabled) {
		this.ssl = enabled;
	}
	
	protected boolean isValid() {
		
		if (this.name == null || this.username == null || this.password == null || this.host == null || this.port == 0) {
			return false;
		} else {
			return true;
		}
	
	}
	
	protected Gateway() {
	}
	
}
