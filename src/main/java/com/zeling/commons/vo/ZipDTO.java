package com.zeling.commons.vo;

import java.io.Serializable;

/**
 * zip数据传输对象
 * 
 * @author chenbd 2019年3月28日
 */
public class ZipDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * zip内容
	 */
	private byte[] content;
	
	/**
	 * zip文件名
	 */
	private String fileName;

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
