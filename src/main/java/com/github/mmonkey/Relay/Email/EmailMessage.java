package com.github.mmonkey.Relay.Email;

import java.util.ArrayList;
import java.util.List;

public class EmailMessage {
	
	private EmailHeaderSection headerSection;
	private List<EmailBodySection> sections = new ArrayList<EmailBodySection>();
	private EmailFooterSection footerSeciton;
	
	private String emailBackground;
	private String bodyBackground;
	
	public EmailHeaderSection getHeaderSection() {
		return this.headerSection;
	}
	
	public void setHeaderSection(EmailHeaderSection headerSection) {
		this.headerSection = headerSection;
	}
	
	public void addBodySection(EmailBodySection bodySection) {
		this.sections.add(bodySection);
	}
	
	public void addBodySections(List<EmailBodySection> bodySections) {
		this.sections.addAll(bodySections);
	}
	
	public EmailFooterSection getFooterSection() {
		return this.footerSeciton;
	}
	
	public void setFooterSection(EmailFooterSection footerSection) {
		this.footerSeciton = footerSection;
	}
	
	public String getEmailBackground() {
		return this.emailBackground;
	}
	
	public void setEmailBackground(String emailBackground) {
		this.emailBackground = emailBackground;
	}
	
	public String getBodyBackground() {
		return this.bodyBackground;
	}
	
	public void setBodyBackground(String bodyBackground) {
		this.bodyBackground = bodyBackground;
	}
	
	public EmailMessage() {
		this.emailBackground = "#E1E1E1";
		this.bodyBackground = "#FFFFFF";
	}

}
