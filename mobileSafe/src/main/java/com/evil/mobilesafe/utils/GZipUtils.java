package com.evil.mobilesafe.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {

	public static void main(String[] args) {
		/*try {
			compressFile("address.db", "address.zip");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		/*try {
			unCompressFile("address.zip", "address.db");
		} catch (IOException e) {
			
			e.printStackTrace();
		}*/
	}

	/**
	 * 压缩文件
	 * 
	 * @is 要压缩的文件输入流
	 * @os 压缩目标文件输出流
	 */
	public static void compressFile(InputStream is, OutputStream os)
			throws IOException {
		// 包装输出流
		GZIPOutputStream gos = new GZIPOutputStream(os);

		byte[] arr = new byte[8192];
		int len = -1;
		// 读取数据
		while ((len = is.read(arr)) != -1) {
			// 写出数据
			gos.write(arr, 0, len);
			gos.flush();
		}
		// 关流
		is.close();
		gos.close();
	}

	/** 
	 * 压缩文件
	 * 
	 * @file 要压缩的文件
	 * @zipFile 压缩目标文件
	 */
	public static void compressFile(File file, File zipFile) throws IOException {
		// 把文件转化为流
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(zipFile);

		compressFile(fis, fos);
	}

	/**
	 * 压缩文件
	 * 
	 * @path 要压缩的文件路径
	 * @zipPath 压缩目标文件路径
	 */
	public static void compressFile(String path, String zipPath)
			throws IOException {
		// 把路径转化为文件
		File file = new File(path);
		File zipFile = new File(zipPath);

		compressFile(file, zipFile);
	}
	
	/**
	 * 解压缩
	 * @param is 要解压的文件输入流
	 * @param os 要输出的目标文件的输出流
	 */
	public static void unCompressFile(InputStream is , OutputStream os) throws IOException {
		//包装输入流
		GZIPInputStream gis = new GZIPInputStream(is);
		
		byte[] arr = new byte[8192];
		int len;
		while((len = gis.read(arr) ) != -1)	{
			os.write(arr, 0, len);
			os.flush();
		}
		
		gis.close();
		os.close();
	}
	
	/**
	 * 解压缩
	 * @param zipFile 要解压的文件
	 * @param outFile 要输出的目标文件
	 */
	public static void unCompressFile(File zipFile , File outFile) throws IOException {
		FileInputStream fis = new FileInputStream(zipFile);
		FileOutputStream fos = new FileOutputStream(outFile);
		
		unCompressFile(fis, fos);
	}
	
	/**
	 * 解压缩
	 * @param zipFile 要解压的文件
	 * @param outFile 要输出的目标文件
	 */
	public static void unCompressFile(String zipPath , String outPath) throws IOException {
		File zipFile = new File(zipPath);
		File outFile = new File(outPath);
		
		unCompressFile(zipFile, outFile);
	}

}
