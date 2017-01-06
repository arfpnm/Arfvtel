package com.telappliant;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestClass {
	
	private static String authToken;
	
	public static void main(String str[]){
		ExecutorService executor = Executors.newFixedThreadPool(10);
		Runnable task = () -> {
			//try {
				//uthToken = validateToken(authToken) == null ? createAuthentication() : authToken;
				System.out.println("Printing some stuff");
			//} catch (NoSuchAlgorithmException e) {
				//e.printStackTrace();
			//}
		};
		executor.submit(task);
		try {
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			//log.info("tasks interrupted");
		}
		finally {
			if (!executor.isTerminated()) {
				//log.info("Cancelling unfinished task");
			}
			executor.shutdownNow();
		}
	}
	

}
