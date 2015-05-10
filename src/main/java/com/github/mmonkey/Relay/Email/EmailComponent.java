package com.github.mmonkey.Relay.Email;

public class EmailComponent {
	
	public String componentTag;
	public String componentStyle;
	public String componentTextColor;
	public String componentContent;
	public boolean isText = true;
	
	public void setTextColor(String textColor) {
		this.componentTextColor = textColor;
	}
	
	public void setContent(String content) {
		this.componentContent = content;
	}
	
	public EmailComponent(EmailComponentTypes type, String content) {
		this.componentTag = type.getTag();
		this.componentStyle = type.getStyle();
		this.componentContent = content;
	}

}
