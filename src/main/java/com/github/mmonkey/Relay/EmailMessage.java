package com.github.mmonkey.Relay;

import java.util.ArrayList;
import java.util.List;

public class EmailMessage {
	
	public String server;
	public String headline;
	public String subheadline;
	public List<Paragraph> paragraphs;
	public String iconUrl;
	public String imageUrl;
	
	public void setServer(String server) {
		this.server = server;
	}
	
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	
	public void setSubheadline(String subheadline) {
		this.subheadline = subheadline;
	}
	
	public void addParagraph(String paragraph) {
		Paragraph p = new Paragraph(paragraph);
		this.paragraphs.add(p);
	}
	
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public EmailMessage() {
		this.paragraphs = new ArrayList<Paragraph>();
	}
	
	public EmailMessage(String headline, String paragraph) {
		
		Paragraph p = new Paragraph(paragraph);
		this.paragraphs = new ArrayList<Paragraph>();
		this.paragraphs.add(p);
		this.headline = headline;
		
	}

}
