package com.github.mmonkey.Relay;

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
	
	public String parse(String template, Object model) {
		
		try {
			
			File temp = new File(this.templateDir, template);
			MustacheFactory mustacheFactory = new DefaultMustacheFactory();
			Mustache mustache = mustacheFactory.compile(new FileReader(temp), "fileReader");
			
			StringWriter writer = new StringWriter();
			mustache.execute(writer, model).flush();
				
			return writer.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
		
	}
	
	public HTMLTemplatingService() {
	}
}
