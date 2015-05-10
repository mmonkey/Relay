package com.github.mmonkey.Relay.Email;

import java.util.ArrayList;
import java.util.List;

public class EmailBodySection extends EmailSection {

	public List<EmailComponent> components = new ArrayList<EmailComponent>();
	
	public void addComponent(EmailComponent component) {
		this.components.add(component);
	}
	
	public void addComponents(List<EmailComponent> components) {
		this.components.addAll(components);
	}
	
	public EmailBodySection() {
	}
	
	public EmailBodySection(EmailComponent component) {
		this.components.add(component);
	}
	
	public EmailBodySection(List<EmailComponent> components) {
		this.components.addAll(components);
	}
	
}
