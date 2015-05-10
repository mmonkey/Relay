package com.github.mmonkey.Relay.Email;

public enum EmailComponentTypes {

	HEADLINE ("h1", "line-height:100%;font-family:Helvetica,Arial,sans-serif;font-size:35px;font-weight:normal;margin-bottom:5px;text-align:center;"),
	SECTION_HEADLINE ("h3", "line-height:125%;font-family:Helvetica,Arial,sans-serif;font-size:20px;font-weight:normal;margin-top:0;margin-bottom:3px;text-align:left;"),
	PARAGRAPH ("div", "text-align:left;font-family:Helvetica,Arial,sans-serif;font-size:15px;margin-bottom:0;margin-top:5px;line-height:135%;"),
	PARAGRAPH_CENTERED ("div", "text-align:center;font-family:Helvetica,Arial,sans-serif;font-size:15px;margin-bottom:0;margin-top:5px;line-height:135%;"),
	IMAGE ("img", "max-width:100%;");
	
	private final String tag;
	private final String style;
	
	public String getTag() {
		return this.tag;
	}
	
	public String getStyle() {
		return this.style;
	}

	EmailComponentTypes(String tag, String style) {
		this.tag = tag;
		this.style = style;
	}
	
}
