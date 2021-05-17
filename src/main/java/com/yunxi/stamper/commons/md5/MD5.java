package com.yunxi.stamper.commons.md5;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 与Go项目中的加密方式统一:md5(md5($pass))
 */
@Slf4j
public class MD5 {
	public static String toMD5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte[] b = md.digest();
			md.reset();
			b = md.digest(b);//主要的一步，注释掉这一步就是正常的MD5加密
			int i;

			StringBuilder buf = new StringBuilder("");
			for (byte value : b) {
				i = value;
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			//32位加密
			return buf.toString();
			// 16位的加密
			//return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			log.error("出现异常 ", e);
			return null;
		}
	}

	/**
	 * 计算文件HASH
	 *
	 * @param file
	 * @return
	 */
	public static String md5HashCode(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		try (FileInputStream fis = new FileInputStream(file)) {
			return md5HashCode(fis);
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
		return null;
	}

	/**
	 * java获取文件的md5值
	 *
	 * @param fis 输入流
	 * @return
	 */
	public static String md5HashCode(InputStream fis) throws NoSuchAlgorithmException, IOException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = fis.read(buffer, 0, 1024)) != -1) {
				md.update(buffer, 0, length);
			}
			byte[] md5Bytes = md.digest();
			BigInteger bigInt = new BigInteger(1, md5Bytes);//1代表绝对值
			String resHash = bigInt.toString(16);//转换为16进制

			//如果数量不够，补0
			if (StringUtils.isNotBlank(resHash) && resHash.length() < 32) {
				int len = 32 - resHash.length();
				while (len >= 1) {
					len--;
					resHash = 0 + resHash;
				}
			}
			return resHash;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将文本转发md5
	 *
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static String md5HashCode(String text) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] buffer = text.getBytes();
		md.update(buffer);
		byte[] md5Bytes = md.digest();
		BigInteger bigInt = new BigInteger(1, md5Bytes);//1代表绝对值
		return bigInt.toString(16);//转换为16进制
	}
}
