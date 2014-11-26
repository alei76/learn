package com.lin.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	/**
	 * 列出目录下的所有文件
	 * 
	 * @param directory
	 * @return
	 */
	public static List<File> listFile(File directory) {
		List<File> list = new ArrayList<File>();
		if (!directory.exists())
			return list;
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					list.add(file);
				} else if (file.isDirectory()) {
					list.addAll(listFile(file));
				}
			}
		}
		return list;
	}

	/**
	 * 获取文件内容
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String fileContent(File file) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		InputStream inputStream = new FileInputStream(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bytes = new byte[2048];
		int ch = 0;
		while ((ch = inputStream.read(bytes)) != -1) {
			baos.write(bytes);
		}

		baos.close();
		inputStream.close();
		return baos.toString();
	}
}
