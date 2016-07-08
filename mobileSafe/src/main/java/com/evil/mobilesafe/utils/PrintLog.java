package com.evil.mobilesafe.utils;

public class PrintLog {
	
	private static boolean isPrint = false;
	
	public static void log(Object msg){
		if (isPrint) {
			System.out.println("evil : "+msg);
		}
	}
}
