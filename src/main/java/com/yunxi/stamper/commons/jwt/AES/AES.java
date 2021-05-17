package com.yunxi.stamper.commons.jwt.AES;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;

/**
 * @author ngh
 * AES128 算法
 * <p>
 * CBC 模式
 * <p>
 * PKCS7Padding 填充模式
 * <p>
 * CBC模式需要添加一个参数iv
 * <p>
 * 介于java 不支持PKCS7Padding，只支持PKCS5Padding 但是PKCS7Padding 和 PKCS5Padding 没有什么区别
 * 要实现在java端用PKCS7Padding填充，需要用到bouncycastle组件来实现
 */
public class AES {
	// 算法名称
	static final String KEY_ALGORITHM = "AES";
	// 加解密算法/模式/填充方式
	static final String algorithmStr = "AES/CBC/PKCS7Padding";
	//
	static private Key key;
	static private Cipher cipher;
	static boolean isInited = false;

	static byte[] iv = {0x30, 0x31, 0x30, 0x32, 0x30, 0x33, 0x30, 0x34, 0x30, 0x35, 0x30, 0x36, 0x30, 0x37, 0x30, 0x38};

	public static void init(byte[] keyBytes) {

		// 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
		int base = 16;
		if (keyBytes.length % base != 0) {
			int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
			byte[] temp = new byte[groups * base];
			Arrays.fill(temp, (byte) 0);
			System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
			keyBytes = temp;
		}
		// 初始化
		Security.addProvider(new BouncyCastleProvider());
		// 转化成JAVA的密钥格式
		key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
		try {
			// 初始化cipher
			cipher = Cipher.getInstance(algorithmStr, "BC");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 加密方法
	 *
	 * @param content  要加密的字符串
	 * @param keyBytes 加密密钥
	 * @return
	 */
	public static byte[] encrypt(byte[] content, byte[] keyBytes) {
		byte[] encryptedText = null;
		init(keyBytes);
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			encryptedText = cipher.doFinal(content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptedText;
	}

	/**
	 * 解密方法
	 *
	 * @param encryptedData 要解密的字符串
	 * @param keyBytes      解密密钥
	 * @return
	 */
	public static byte[] decrypt(byte[] encryptedData, byte[] keyBytes) {
		byte[] encryptedText = null;
		init(keyBytes);
		try {
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
			encryptedText = cipher.doFinal(encryptedData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptedText;
	}

//	public static void main(String[] args) {
//		String slatKey = RandomUtil.getRandomNum(16);
//		String message = "helloworld";
//		long l = System.currentTimeMillis();
//		System.out.println("明文长度:" + message.length());
//		byte[] encrypt = encrypt(message.getBytes(), slatKey.getBytes());
//		System.out.println("密文:" + new BASE64Encoder().encode(encrypt));
//
//		byte[] decrypt = decrypt(encrypt, slatKey.getBytes());
//		System.out.println("明文:" + new String(decrypt));
//		System.out.println("运行耗时:" + (System.currentTimeMillis() - l) / 1000.0);
//	}
}
