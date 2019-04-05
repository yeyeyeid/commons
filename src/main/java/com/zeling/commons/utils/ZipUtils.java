package com.zeling.commons.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.zeling.commons.vo.ZipDTO;

/**
 * zip文件工具类
 * 
 * @author chenbd 2018年7月31日
 */
public class ZipUtils {
	/**
	 * 缓冲区大小
	 */
	private static final int BUFFER_SIZE = 1024;
	
	/**
	 * zip 后缀
	 */
	public static final String ZIP_SUFFIX = ".zip";
	
	/**
	 * 压缩文件，zip格式
	 * 
	 * @param zipPath
	 *            待生成的zip文件全路径，必须以.zip结尾
	 * @param files
	 *            待压缩文件
	 * @param filePathCutOffStr
	 *            压缩时，截断路径，为空则按文件的目录结构进行压缩</br>
	 *            例 path:/user/data/test.xml filePathCutOffStr:data 则压缩时去掉/user/data的目录
	 * @return zip文件全路径
	 * @throws IOException
	 */
	public static String zip(String zipPath, List<File> files, String filePathCutOffStr) throws IOException {
		if (StringUtils.isBlank(zipPath)) {
			throw new IllegalArgumentException("zipPath参数不能为空");
		}
		File zipFile = new File(zipPath);
		return zip(zipFile.getParent(), zipFile.getName(), files, filePathCutOffStr);
	}
	
	/**
	 * 压缩文件，zip格式
	 * 
	 * @param zipParentPath
	 *            zip文件父级路径
	 * @param zipFileName
	 *            zip文件名
	 * @param files
	 *            待压缩文件
	 * @param filePathCutOffStr
	 *            压缩时，截断路径，为空则按文件的目录结构进行压缩</br>
	 *            path:/user/data/test.xml filePathCutOffStr:data 则压缩时去掉/user/data的目录
	 * @return 返回zip文件全路径
	 * @throws IOException
	 */
	public static String zip(String zipParentPath, String zipFileName, List<File> files, String filePathCutOffStr)
			throws IOException {
		if (StringUtils.isBlank(zipParentPath) || StringUtils.isBlank(zipFileName) || CollectionUtils.isEmpty(files)) {
			throw new IllegalArgumentException("zipParentPath|files|zipFileName参数不能为空");
		}
		if (!zipFileName.endsWith(ZIP_SUFFIX)) {
			zipFileName = zipFileName + ZIP_SUFFIX;
		}
		// 构造zip文件上级文件夹
		FileUtils.forceMkdir(new File(zipParentPath));
		File zipFile = new File(zipParentPath, zipFileName);

		// 压缩
		try (FileOutputStream out = new FileOutputStream(zipFile);
				ZipArchiveOutputStream zos = new ZipArchiveOutputStream(out);) {
			for (File file : files) {
				String filePath = file.getAbsolutePath();
				String entryName = null;
				if (StringUtils.isEmpty(filePathCutOffStr)) {
					entryName = filePath;
				} else {
					int cutOffIndex = filePath.indexOf(filePathCutOffStr) + filePathCutOffStr.length() + 1;
					entryName = filePath.substring(cutOffIndex);
				}
				zos.putArchiveEntry(new ZipArchiveEntry(file, entryName));
				try (FileInputStream fis = new FileInputStream(file);) {
					IOUtils.copy(fis, zos);
				}
				zos.closeArchiveEntry();
			}
		}
		return zipFile.getAbsolutePath();
	}
	
	/**
	 * 获取zip文件传输对象
	 * 
	 * @param zipFilePath 
	 * @return
	 * @author chenbd 2019年3月28日
	 * @throws IOException 
	 */
	public static ZipDTO getZipDTO(String zipFilePath) throws IOException {
		if (StringUtils.isBlank(zipFilePath)) {
			throw new IllegalArgumentException("zipFilePath参数不能为空");
		}
		if (!zipFilePath.endsWith(ZIP_SUFFIX)) {
			throw new IllegalArgumentException("zipFilePath必须以.zip为后缀");
		}
		ZipDTO zipDTO = new ZipDTO();
		byte[] content = FileUtils.readFileToByteArray(new File(zipFilePath));
		zipDTO.setContent(content);
		zipDTO.setFileName(new File(zipFilePath).getName());
		return zipDTO;
	}
	
	/**
	 * 根据zipDTO生成一个zip文件
	 * 
	 * @param zipDTO zip传输对象
	 * @param zipFilePath 生成的zip路径
	 * @return 生成的zip文件全路径
	 * @throws IOException
	 * @author chenbd 2019年3月28日
	 */
	public static String zipDTO2File(ZipDTO zipDTO, String zipFilePath) throws IOException {
		if (zipDTO == null || StringUtils.isBlank(zipFilePath)) {
			throw new IllegalArgumentException("zipDTO|zipFilePath参数不能为空");
		}
		if (!zipFilePath.endsWith(ZIP_SUFFIX)) {
			zipFilePath = zipFilePath + ZIP_SUFFIX;
		}
		File zipFile = new File(zipFilePath);
		FileUtils.forceMkdir(zipFile.getParentFile());
		FileUtils.writeByteArrayToFile(zipFile, zipDTO.getContent());
		return zipFile.getAbsolutePath();
	}

	/**
	 * 解压 zip 文件
	 * 
	 * @param zipFile zip压缩文件
	 * @param destDirPath zip压缩文件解压后保存的目录
	 * @return 返回解压后的文件名列表
	 * @throws IOException 
	 */
	public static List<String> unZip(File zipFile, String destDirPath) throws IOException {
		// 如果 destDir 为 null, 空字符串, 或者全是空格, 则解压到压缩文件所在目录
		if (StringUtils.isBlank(destDirPath)) {
			destDirPath = zipFile.getAbsoluteFile().getParent();
		}
		File destDir = new File(destDirPath);
		FileUtils.forceMkdir(new File(destDirPath));
		destDirPath = destDir.getAbsolutePath();

		// zip压缩文件里的文件名列表
		List<String> fileNames = new ArrayList<String>();
		try (ZipArchiveInputStream zis = new ZipArchiveInputStream(
				new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));) {
			ZipArchiveEntry entry = null;
			// 获取zip里面的文件
			while ((entry = zis.getNextZipEntry()) != null) {
				if (entry.isDirectory()) {
					// 文件夹处理
					File directory = new File(destDirPath, entry.getName());
					directory.mkdirs();
				} else {
					// 文件处理
					File file = new File(destDirPath, entry.getName());
					String parentDirPath = file.getAbsoluteFile().getParent();
					FileUtils.forceMkdir(new File(parentDirPath));
					try (OutputStream os = new BufferedOutputStream(
							new FileOutputStream(new File(destDirPath, entry.getName())), BUFFER_SIZE);) {
						IOUtils.copy(zis, os);
					}
					fileNames.add(file.getAbsolutePath());
				}
			}
		}
		return fileNames;
	}
	
	/**
	 * 解压 zip 文件
	 * 
	 * @param zipFile zip压缩文件
	 * @param destDirPath zip压缩文件解压后保存的目录
	 * @return 返回解压后的文件名列表
	 * @throws IOException
	 */
	public static List<String> unZip(String zipFilePath, String destDirPath) throws IOException {
		return unZip(new File(zipFilePath), destDirPath);
	}
	
	/**
	 * 测试
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		List<String> names = unZip("sdsd.zip", null);
		System.out.println(names);
	}
	
	private ZipUtils() {
		throw new AssertionError(ZipUtils.class.getName() + ": 禁止实例化");
	}
}