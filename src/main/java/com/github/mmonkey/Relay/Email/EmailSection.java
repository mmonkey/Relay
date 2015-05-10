package com.github.mmonkey.Relay.Email;

public class EmailSection {

	public String sectionTextColor;
	public String sectionBackgroundColor;
	
	public String getSectionTextColor() {
		return this.sectionTextColor;
	}
	
	public void setSectionTextColor(String sectionTextColor) {
		this.sectionTextColor = sectionTextColor;
	}
	
	public String getSectionBackgroundColor() {
		return this.sectionBackgroundColor;
	}
	
	public void setSectionBackgroundColor(String sectionBackgroundColor) {
		this.sectionBackgroundColor = sectionBackgroundColor;
	}
	
	public EmailSection() {
		this.sectionTextColor = "#5F5F5F";
		this.sectionBackgroundColor = "#FFFFFF";
	}
	
	public EmailSection(String sectionTextColor, String sectionBackgroundColor) {
		this.sectionTextColor = sectionTextColor;
		this.sectionBackgroundColor = sectionBackgroundColor;
	}
}
