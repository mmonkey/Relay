package com.github.mmonkey.Relay.Email;

public class EmailContent {
	
	public String contentTag;
	public String contentStyle;
	public String contentTextColor;
	public String content;
	public boolean isText = true;
	
	public void setTextColor(String textColor) {
		this.contentTextColor = textColor;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public EmailContent(EmailContentTypes type, String content) {
		
		this.contentTag = type.getTag();
		this.contentStyle = type.getStyle();
		this.content = content;
		
		if (type.equals(EmailContentTypes.IMAGE)) {
			this.isText = false;
		}
		
	}

}
