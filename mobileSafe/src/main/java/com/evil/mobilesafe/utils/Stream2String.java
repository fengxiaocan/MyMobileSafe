package com.evil.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Stream2String {
	
	
	/**
	 * 把输入流转化为字符串String
	 */
	public static String stream2String(InputStream input) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		
		return br.readLine();
	}
}
