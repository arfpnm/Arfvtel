package com.telappliant;

import java.util.concurrent.Executors;
import java.util.logging.Logger;

//import org.apache.log4j.Logger;

import com.telappliant.tvoip.asterisk.Asterisk;
import com.telappliant.tvoip.asterisk.VoipService;

/**
 * Hello world!
 *
 */
public class VoipMainTest 
{
	
	private static  Logger log = Logger.getLogger(VoipMainTest.class.getName());
    public static void main1( String[] args )
    {
    	log.info("In MaIN");
    	processVoipCalls();
    }
    
    
 private static void processApiAuthetication(){
    	
    	Asterisk asterisk = new Asterisk(Executors.newCachedThreadPool(), 
    									// "vo-r2-01.cardassure.stage.telappliant.com", 
    									"vo-r2-01.cardassure.stage.telappliant.com",
    									 "77.240.59.249", 
    									  5038, 
    									  "vocal", 
    									  "vhD%ZjTmy^36", 
    									  false);
    	VoipService vs = new VoipService();
    	asterisk.addListener(vs);
    	log.info("Before Start");
    	asterisk.start();
    	log.info("After Start");
    	try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
        
    	
    }
    
    
    private static void processVoipCalls(){
    	
    	/**
    	 Username: vocal
		Secret: vhD%ZjTmy^36
		Hostname: vo-r2-01.cardassure.stage.telappliant.com
		IP: 77.240.59.249 
		Port: 5038
    	 
    	 */
    	log.info("Before Instantiation");
    	
    	Asterisk asterisk = new Asterisk(Executors.newCachedThreadPool(), 
    									 "vo-r2-01.cardassure.stage.telappliant.com", 
    									 "77.240.59.249", 
    									  5038, 
    									  "vocal", 
    									  "vhD%ZjTmy^36", 
    									  false);
    	VoipService vs = new VoipService();
    	asterisk.addListener(vs);
    	log.info("Before Start");
    	asterisk.start();
    	log.info("After Start");
    	try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
        
    	
    }
    
    
    
    
    
}
