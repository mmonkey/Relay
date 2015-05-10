package com.github.mmonkey.Relay.Email;

import java.util.ArrayList;
import java.util.List;

public class EmailBodySection extends EmailSection {

	List<EmailContent> contents = new ArrayList<EmailContent>();
	
	public void addContent(EmailContent content) {
		this.contents.add(content);
	}
	
	public void addContents(List<EmailContent> contents) {
		this.contents.addAll(contents);
	}
	
	public EmailBodySection() {
	}
	
	public EmailBodySection(EmailContent content) {
		this.contents.add(content);
	}
	
	public EmailBodySection(List<EmailContent> contents) {
		this.contents.addAll(contents);
	}
	
}
