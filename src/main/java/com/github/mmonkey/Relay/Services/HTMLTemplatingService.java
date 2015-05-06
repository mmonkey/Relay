package com.github.mmonkey.Relay.Services;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class HTMLTemplatingService implements TemplatingService {

	private File templateDir;
	private MustacheFactory mustacheFactory;
	private Mustache mustache;
	
	public void setTemplateDirectory(File templateDir) {
		this.templateDir = templateDir;
		this.mustacheFactory = new DefaultMustacheFactory(this.templateDir.getPath());
	}
	
	public String parse(String file, Object model) throws IOException {
		
		String result = "";
		this.mustache = this.mustacheFactory.compile(file);
		this.mustache.execute(new StringWriter(), model).write(result);
		
		return result;
		
	}
	
	public HTMLTemplatingService() {
	}
	
	public HTMLTemplatingService(File templateDir) {
		this.templateDir = templateDir;
		this.mustacheFactory = new DefaultMustacheFactory(this.templateDir.getPath());
	}
}
