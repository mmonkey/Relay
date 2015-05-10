package com.github.mmonkey.Relay.Email;

import java.util.Calendar;

public class EmailFooterSection {
	
	public String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
	public String copyrightName;
	public String copyrightLink;
	
	public void setCopyrightName(String serverName) {
		this.copyrightName = serverName;
	}
	
	public void setCopyrightLink(String copyrightLink) {
		this.copyrightLink = copyrightLink;
	}
	
	public EmailFooterSection() {
	}

}
