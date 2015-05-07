package com.github.mmonkey.Relay.Services;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class HTMLTemplatingService implements TemplatingService {

	private File templateDir;
	
	public void setTemplateDirectory(File templateDir) {
		this.templateDir = templateDir;
	}
	
	public String parse(String template, Object model) throws IOException {
		
		String result = "";
		
		File temp = new File(this.templateDir, template);
		MustacheFactory mustacheFactory = new DefaultMustacheFactory();
		Mustache mustache = mustacheFactory.compile(new FileReader(temp), "fileReader");
		
		StringWriter writer = new StringWriter();
		mustache.execute(writer, model).write(result);
		writer.flush();
		
		return result;
		
	}
	
	public HTMLTemplatingService() {
	}
}
