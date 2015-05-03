package com.github.mmonkey.Relay;

public enum Carriers {

	ALLTEL ("Alltel",
		new String[] {"@message.alltel.com"}),
			
	ATT ("AT&T",
		new String[] {"@txt.att.net"}),
	
	CINGULAR ("Cingular",
		new String[] {"@cingularme.com"}),
	
	CLARO ("Claro",
		new String[] {"@clarotorpedo.com.br"}),
	
	METRO_PCS ("Metro PCS",
		new String[] {"@MyMetroPcs.com"}),
	
	NEXTEL ("Nextel",
		new String[] {"@messagin.nextel.com"}),
	
	O2 ("O2",
		new String[] {"@o2imail.co.uk"}),
	
	OI ("Oi",
		new String[] {"@mms.oi.com.br"}),
	
	ORANGE ("Orange",
		new String[] {"@orange.net"}),
	
	POWERTEL ("Powertel",
		new String[] {"@ptel.net"}),
	
	SPRINT ("Sprint",
		new String[] {"@messaging.sprintpcs.com"}),
	
	SUNCOM ("SunCom",
		new String[] {"@tms.suncom.com"}),
	
	T_MOBILE ("T-Mobile",
		new String[] {"@tmomail.net"}),
	
	TIM ("Tim",
		new String[] {"@timnet.com"}),
	
	US_CELLULAR ("U.S. Cellular",
		new String[] {"@email.uscc.net"}),
	
	VERIZON ("Verizon",
		new String[] {"@vtext.com"}),
	
	VIRGIN_MOBILE ("Virgin Mobile",
		new String[] {"@vmobl.com"}),
	
	VIVO ("Vivo",
		new String[] {"@torbedoemail.com.br"});
	
	private final String displayName;
	private final String[] address;

	Carriers(String displayName, String[] address) {
		this.displayName = displayName;
		this.address = address;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String[] getAddresses() {
		return address;
	}
	
}
