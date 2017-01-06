package com.telappliant.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {

	public static String getClassPathPropertyValue(String key){
		Properties prop = new Properties();
		InputStream input = null;

		try {
			String filename = "general-props.properties";
			input = PropertyLoader.class.getClassLoader().getResourceAsStream(filename);
			if(input==null){
				return null;
			}
			prop.load(input);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally{
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop.getProperty(key);
	}
	
	public static String getPropertyValue(String propKey) throws IOException {
	    Properties prop = new Properties();
	    InputStream input = null;
	 
	    try {
	        input = new FileInputStream("/local/etc/vocal.properties");
	         
	        // load the properties file
	        prop.load(input);
	 
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    } finally {
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    return prop.getProperty(propKey);
	}
	

}

