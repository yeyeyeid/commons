package com.zeling.commons.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * servlet 相关工具类
 * 
 * @author chenbd 2019年4月5日
 */
public class HttpServletUtils {
	
	/**
	 * 下载 
	 * 
	 * @param response
	 * @param filePath 待下载文件路径
	 * @param downloadFileName 下载后显示文件名
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void download(HttpServletResponse response, String filePath, String downloadFileName)
			throws FileNotFoundException, IOException {
		// octet-stream 自动匹配文件类型
		response.setContentType("application/octet-stream");
		// 设置content-disposition响应头控制浏览器以下载的形式打开文件
		response.setHeader("content-disposition",
				"attachment;filename=" + URLEncoder.encode(downloadFileName, "UTF-8"));
		int len = 0;
		byte[] buffer = new byte[1024];
		try (InputStream in = new FileInputStream(filePath);
				ServletOutputStream out = response.getOutputStream();) {
			// 使用OutputStream流，避免使用PrintWriter流，因为OutputStream流是字节流，可以处理任意类型的数据，
			// 而PrintWriter流是字符流，只能处理字符数据，如果用字符流处理字节数据，会导致数据丢失
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.flush();
		}
	}
	
	
	private HttpServletUtils() {
		throw new AssertionError(HttpServletUtils.class.getName() + ": 禁止实例化");
	}
}
