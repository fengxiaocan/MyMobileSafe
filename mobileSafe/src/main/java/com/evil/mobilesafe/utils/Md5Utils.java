package com.evil.mobilesafe.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件加密类
 */
public class Md5Utils {

	// 禁止实例化
	private Md5Utils() {
	} 

	public static void main(String[] args) {
		// System.out.println(codeMd5("你谈谈吗的啊大家说阿迪啊收到啊手机掉家啊是多久哦哀伤的就阿斯顿阿斯加德"));
		// 9BDBF50E8B1FAD63D5514C1A1E360FB5
		// 9BDBF50E8B1FAD63D5514C1A1E360FB5
	}

	/**
	 * @in 根据输入的流来获取md5值
	 * @return 返回md5值的输入流
	 */
	public static String getMd5(InputStream in) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] arr = new byte[1024 * 10];
			int len;
			while ((len = in.read(arr)) != -1) {
				md.update(arr, 0, len); // 计算输入流中的md5值
			}
			in.close();

			// 返回执行的结果
			StringBuffer sb = new StringBuffer();
			byte[] digest = md.digest();
			for (byte b : digest) {
				// 怎么把int值转成十六进制
				String hexString = Integer.toHexString(0x000000ff & b); // 把byte转换成16进制
				if (hexString.length() == 1) {
					sb.append(0).append(hexString); // 只有一位数的时候,补足两位,前面加0
				} else {
					sb.append(hexString); // 拼接md5值
				}
			}
			return sb.toString().toUpperCase(); // 返回md5值并转换成大写
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * @return 返回文件的md5值
	 * @file 要获取md5值的文件
	 */
	public static String getMd5(File file) {
		try {
			InputStream in = new FileInputStream(file);
			return getMd5(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @return 文件的md5值
	 * @filePath 要获取md5值的文件路径
	 */
	public static String getMd5(String filePath) {
		try {
			File file = new File(filePath);
			InputStream in = new FileInputStream(file);
			return getMd5(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @return 返回加密byte数组的md5值
	 * @msg 要获取md5值的byte数组信息
	 */
	public static String codeMd5(byte[] msg) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(msg); // 加密

			// 计算返回加密的md5值
			StringBuffer sb = new StringBuffer();
			byte[] digest = md.digest();
			for (byte b : digest) {
				String hexString = Integer.toHexString(0x000000ff & b); // 把byte转换成16进制
				if (hexString.length() == 1) {
					sb.append(0).append(hexString); // 只有一位数的时候,补足两位,前面加0
				} else {
					sb.append(hexString); // 拼接md5值
				}
				return sb.toString().toUpperCase(); // 转换成大写并返回
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @return 返回加密String的md5值
	 * @msg 要获取md5值的String数组信息
	 */
	public static String codeMd5(String msg) {
		byte[] bytes = msg.getBytes();
		return codeMd5(bytes);
	}
}
