package com.github.mmonkey.Relay.Services;

import java.io.IOException;

public interface TemplatingService {

	public String parse(String file, Object model) throws IOException;
	
}
