package com.yunxi.stamper.commons.dir;


import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.LocalDateTime;

/**
 * 目录文件处理工具
 */
public class DirFileUtils {

	//文件目录分离的算法(离散),/公司名/一级目录/二级目录
	public static String getFileDirectoryRandom(String orgName, String filename) {
		StringBuilder path = new StringBuilder();
		String separator = File.separator;
		//得到唯一文件名的哈希值
		int filenameHashCode = filename.hashCode();
		//得到一级目录
		int d1 = filenameHashCode & 0xf;
		//得到二级目录
		int d2 = (filenameHashCode >>> 4) & 0xf;
		//得到三级目录
		int d3 = (filenameHashCode >>> 4) & 0xf;
		path.append(orgName).append(separator + d1).append(separator + d2).append(separator + d3);
		return path.toString();
	}

	//文件目录分离的算法(离散),/年/月/日
	public static String getFileDirectoryDate() {
		StringBuilder path = new StringBuilder();
		String separator = File.separator;
		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();//年级目录
		int month = now.getMonthValue();//月级目录
		int day = now.getDayOfMonth();//天级目录
		path.append(separator + year).append(separator + month).append(separator + day + separator);
		return path.toString();
	}

	//文件目录分离的算法(离散),/年/月/日
	public static String getFileDirectoryDate(String orgName) {
		StringBuilder path = new StringBuilder();
		String separator = File.separator;
		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();//年级目录
		int month = now.getMonthValue();//月级目录
		int day = now.getDayOfMonth();//天级目录
		path.append(orgName).append(separator + year).append(separator + month).append(separator + day);// orgName/年/月/日
		return path.toString();
	}

	//  返回结果示例:/orgCode/年/月/日/
	public static String getFilePathV2(String secondePath) {
		StringBuilder path = new StringBuilder();
		String separator = File.separator;//分隔符
		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();//年级目录
		int month = now.getMonthValue();//月级目录
		int day = now.getDayOfMonth();//天级目录
		if (StringUtils.isNotBlank(secondePath)) {
			path.append(separator).append(secondePath);
		}
		path.append(separator).append(year).append(separator).append(month).append(separator).append(day).append(separator);
		return path.toString();
	}

	//根据原始文件名，生成唯一文件名
	public static String getUniqueFileName(String filename) {
		//TODO:文件名以什么规则生成?
		return "";
	}
}