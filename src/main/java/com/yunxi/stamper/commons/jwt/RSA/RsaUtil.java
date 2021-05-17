package com.yunxi.stamper.commons.jwt.RSA;

import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description Rsa加解密工具类
 * @date 2019/1/23 0023 18:59
 */
public class RsaUtil {

	/**
	 * 算法名称
	 */
	private static final String ALGORITHM = "RSA";

	/**
	 * 密钥长度
	 */
	private static final int KEY_SIZE = 2048;

	public static byte[] DEFAULT_SPLIT = "#PART#".getBytes();    // 当要加密的内容超过bufferSize，则采用partSplit进行分块加密
	public static int DEFAULT_BUFFERSIZE = (KEY_SIZE / 8) - 11;// 当前秘钥支持加密的最大字节数

	/**
	 * 随机生成密钥对（包含公钥和私钥）
	 */
	public static KeyPair generateKeyPair() throws Exception {
		// 获取指定算法的密钥对生成器
		KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHM);
		// 初始化密钥对生成器（指定密钥长度, 使用默认的安全随机数源）
		gen.initialize(KEY_SIZE);
		// 随机生成一对密钥（包含公钥和私钥）
		return gen.generateKeyPair();
	}

	/**
	 * 将 公钥/私钥 编码后以 Base64 的格式保存到指定文件
	 */
	public static void saveKeyForEncodedBase64(Key key, File keyFile) throws IOException {
		if (key == null || keyFile == null) {
			return;
		}
		// 获取密钥编码后的格式
		byte[] encBytes = key.getEncoded();
		// 转换为 Base64 文本
		String encBase64 = new BASE64Encoder().encode(encBytes);
		// 保存到文件
		IOUtils.writeFile(encBase64, keyFile);
	}

	/**
	 * 根据公钥的 Base64 文本创建公钥对象
	 */
	public static PublicKey getPublicKey(String pubKeyBase64) throws Exception {
		if (StringUtils.isBlank(pubKeyBase64)) {
			return null;
		}
		// 把 公钥的Base64文本 转换为已编码的 公钥bytes
		byte[] encPubKey = new BASE64Decoder().decodeBuffer(pubKeyBase64);
		// 创建 已编码的公钥规格
		X509EncodedKeySpec encPubKeySpec = new X509EncodedKeySpec(encPubKey);
		// 获取指定算法的密钥工厂, 根据 已编码的公钥规格, 生成公钥对象
		return KeyFactory.getInstance(ALGORITHM).generatePublic(encPubKeySpec);
	}

	/**
	 * 根据私钥的 Base64 文本创建私钥对象
	 */
	public static PrivateKey getPrivateKey(String priKeyBase64) throws Exception {
		if (StringUtils.isBlank(priKeyBase64)) {
			return null;
		}
		// 把 私钥的Base64文本 转换为已编码的 私钥bytes
		byte[] encPriKey = new BASE64Decoder().decodeBuffer(priKeyBase64);
		// 创建 已编码的私钥规格
		PKCS8EncodedKeySpec encPriKeySpec = new PKCS8EncodedKeySpec(encPriKey);
		// 获取指定算法的密钥工厂, 根据 已编码的私钥规格, 生成私钥对象
		return KeyFactory.getInstance(ALGORITHM).generatePrivate(encPriKeySpec);
	}

	/**
	 * 公钥加密数据
	 */
	public static byte[] encrypt(byte[] plainData, PublicKey pubKey) throws Exception {
		if (plainData == null || plainData.length == 0 || pubKey == null) {
			return null;
		}
		// 获取指定算法的密码器
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		// 初始化密码器（公钥加密模型）
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		// 加密数据, 返回加密后的密文
		return cipher.doFinal(plainData);
	}

	/**
	 * 私钥解密数据
	 */
	public static byte[] decrypt(byte[] cipherData, PrivateKey priKey) throws Exception {
		if (cipherData == null || cipherData.length == 0 || priKey == null) {
			return null;
		}
		// 获取指定算法的密码器
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		// 初始化密码器（私钥解密模型）
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		// 解密数据, 返回解密后的明文
		return cipher.doFinal(cipherData);
	}

	/**
	 * 用公钥对字符串进行分段加密
	 */
	public static byte[] encryptByPublicKeyForSpilt(byte[] data, PublicKey publicKey) throws Exception {
		if (data == null || data.length == 0 || publicKey == null) {
			return null;
		}
		int dataLen = data.length;
		if (dataLen <= DEFAULT_BUFFERSIZE) {
			return encrypt(data, publicKey);
		}
		List<Byte> allBytes = new ArrayList<Byte>(2048);
		int bufIndex = 0;
		int subDataLoop = 0;
		byte[] buf = new byte[DEFAULT_BUFFERSIZE];
		for (int i = 0; i < dataLen; i++) {
			assert buf != null;
			buf[bufIndex] = data[i];
			if (++bufIndex == DEFAULT_BUFFERSIZE || i == dataLen - 1) {
				subDataLoop++;
				if (subDataLoop != 1) {
					for (byte b : DEFAULT_SPLIT) {
						allBytes.add(b);
					}
				}
				byte[] encryptBytes = encrypt(buf, publicKey);
				for (byte b : encryptBytes) {
					allBytes.add(b);
				}
				bufIndex = 0;
				if (i == dataLen - 1) {
					buf = null;
				} else {
					buf = new byte[Math.min(DEFAULT_BUFFERSIZE, dataLen - i - 1)];
				}
			}
		}
		byte[] bytes = new byte[allBytes.size()];
		{
			int i = 0;
			for (Byte b : allBytes) {
				bytes[i++] = b.byteValue();
			}
		}
		return bytes;
	}

	/**
	 * 使用私钥分段解密
	 */
	public static byte[] decryptByPrivateKeyForSpilt(byte[] encrypted, PrivateKey privateKey) throws Exception {
		if (encrypted == null || encrypted.length == 0 || privateKey == null) {
			return null;
		}
		int splitLen = DEFAULT_SPLIT.length;
		if (splitLen <= 0) {
			return decrypt(encrypted, privateKey);
		}
		int dataLen = encrypted.length;
		List<Byte> allBytes = new ArrayList<Byte>(1024);
		int latestStartIndex = 0;
		for (int i = 0; i < dataLen; i++) {
			byte bt = encrypted[i];
			boolean isMatchSplit = false;
			if (i == dataLen - 1) {
				// 到data的最后了
				byte[] part = new byte[dataLen - latestStartIndex];
				System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
				byte[] decryptPart = decrypt(part, privateKey);
				for (byte b : decryptPart) {
					allBytes.add(b);
				}
				latestStartIndex = i + splitLen;
				i = latestStartIndex - 1;
			} else if (bt == DEFAULT_SPLIT[0]) {
				// 这个是以split[0]开头
				if (splitLen > 1) {
					if (i + splitLen < dataLen) {
						// 没有超出data的范围
						for (int j = 1; j < splitLen; j++) {
							if (DEFAULT_SPLIT[j] != encrypted[i + j]) {
								break;
							}
							if (j == splitLen - 1) {
								// 验证到split的最后一位，都没有break，则表明已经确认是split段
								isMatchSplit = true;
							}
						}
					}
				} else {
					// split只有一位，则已经匹配了
					isMatchSplit = true;
				}
			}
			if (isMatchSplit) {
				byte[] part = new byte[i - latestStartIndex];
				System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
				byte[] decryptPart = decrypt(part, privateKey);
				for (byte b : decryptPart) {
					allBytes.add(b);
				}
				latestStartIndex = i + splitLen;
				i = latestStartIndex - 1;
			}
		}
		byte[] bytes = new byte[allBytes.size()];
		{
			int i = 0;
			for (Byte b : allBytes) {
				bytes[i++] = b.byteValue();
			}
		}
		return bytes;
	}

	/**
	 * 私钥加密数据
	 */
	public static byte[] encryptE(byte[] plainData, PrivateKey privateKey) throws Exception {
		if (plainData == null || plainData.length == 0 || privateKey == null) {
			return null;
		}
		// 获取指定算法的密码器
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		// 初始化密码器（公钥加密模型）
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		// 加密数据, 返回加密后的密文
		return cipher.doFinal(plainData);
	}

	/**
	 * 私钥解密数据
	 */
	public static byte[] decryptE(byte[] cipherData, PublicKey publicKey) throws Exception {
		if (cipherData == null || cipherData.length == 0 || publicKey == null) {
			return null;
		}
		// 获取指定算法的密码器
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		// 初始化密码器（私钥解密模型）
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		// 解密数据, 返回解密后的明文
		return cipher.doFinal(cipherData);
	}
	//***********************以下是以文件格式存储公私钥进行加解密******************************************

	/**
	 * 客户端加密, 返回加密后的数据
	 *
	 * @param plainData 加密的字符串字节数组
	 * @param pubFile   公钥文件位置
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] plainData, File pubFile) throws Exception {
		if (plainData == null || plainData.length == 0 || pubFile == null) {
			return null;
		}
		// 读取公钥文件, 创建公钥对象
		PublicKey pubKey = RsaUtil.getPublicKey(IOUtils.readFile(pubFile));
		// 用公钥加密数据
		byte[] cipher = RsaUtil.encrypt(plainData, pubKey);
		return cipher;
	}

	/**
	 * 服务端解密, 返回解密后的数据
	 *
	 * @param cipherData 密文字节数组
	 * @param priFile    私钥文件位置
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] cipherData, File priFile) throws Exception {
		if (cipherData == null || cipherData.length == 0 || priFile == null) {
			return null;
		}
		// 读取私钥文件, 创建私钥对象
		PrivateKey priKey = RsaUtil.getPrivateKey(IOUtils.readFile(priFile));
		// 用私钥解密数据
		byte[] plainData = RsaUtil.decrypt(cipherData, priKey);
		return plainData;
	}

//	public static void main(String[] args) throws Exception {
//		String message = "abc";
//		/**
//		 * 1.签名:私钥加密,公钥解密
//		 */
//
//		//私钥加密
//		byte[] encrypt = encryptE(message.getBytes(), com.yunxi.stamper.commons.jwt.RSA.MyKeyFactory.getPrivateKey());
//		System.out.println("私钥加密密文:" + Base64Utils.encodeToString(encrypt));
//
//		//公钥解密
//		byte[] bytes = decryptE(encrypt, com.yunxi.stamper.commons.jwt.RSA.MyKeyFactory.getPubKey());
//		System.out.println("公钥加密明文:" + new String(bytes));
//
//		/**
//		 * 2.加密:公钥加密,私钥解密
//		 */
//		String message = "1222222222222222222";
//		long l = System.currentTimeMillis();
//		System.out.println("明文长度:" + message.length());
//		//公钥加密
//		byte[] encryptE = encryptByPublicKeyForSpilt(message.getBytes(), com.yunxi.stamper.commons.jwt.RSA.MyKeyFactory.getPubKey());
//		System.out.println("公钥加密密文:" + Base64Utils.encodeToString(encryptE));
//
////		byte[] bytes = Base64Utils.decodeFromString("aL95KU4Wx1eZmS4sFvS8SbGvgaz1O7KXxqMJWPrw2qw/BJDzpoC2VQRu20QTwyHKREMEWXorlN7xX4OMhZMM8Pwc8Jy8zRfC1LLZ1ADv/iaqWQvjKVMe3Ahb8wbUJ3+QIB80w9Nz2DGOfVUOW/uLV7lKMgfdYJsHqxLjM5MUQynrHRT7nx2MaZpqTkw/Y23yb27L8oZPNiX1LNrHtsAL1sy+O4cDr2uMn9x6FxPB+rR+sVYkBQCOZZATIBQLBVs7BIi9Y4jkB0R+b0GYk2+acLh0mlAIGWzAet7SWhbVGTtYAZgP/mlIbOjbUwKqwjf9P4QD7ZoLDwxC8RxvwSDKuyKd108mzcAxkmSjT9go4GLk6Suvwk6V2xKshDqmgUx0WIngShC5U23IF714NFpzeVxDLH9zsOL7FCgFg7rplmMI59EGDrtqKehAP3oSjYrHGuN3jW1bawAvGZnE1za7TyjjXYsFgSKgj1iZdx2fQg7EUm+DlTHqIq/49lCMoMDbc+Ygj7xWj+nNgdGugvvRIiOwQNbyN4sLkB2FKWQn1G+YbPfTMoGdqKM6241CQh8Cm2YjDPJBk3fBGko8rH9pDjWhjGdMfqDS6R6TGtQGrueJ+5IWa9OVn5CgW+df4w8hnJUQP1fSe19MWxMHFBkwlokKrTdk9wQT8aXT/fwdQBUCJNBvUS/AIfsxeH/bIIjsyW+B1xu99e3Uz4RaJP8Psesra7K/3QcaHGF+yMmJC7I4Etw8R5u31At3cv6NjR9cYo3ZuuVk935WCzLoyY5iNw/BxjtFMd2KWsdwrmSA5BquO/s2Q2ZCsFryNFpVnHicfC8nYVAo3dVcbgZgAroRVoJdnzw07MfjfECSnM58hHySAHkkkHHCjqPPQCi4ze12PiA8BaGGtPsF/p1TKdFdsWuwRvjY7Fsj6gzpV/uvfG6/wmNs7TJv1ugUHUYpLbxiAmQCwlsWSb9Ba3DIgYiaEMz+CeAyI51U69HZ4C0y7Jfv2bFoNsvhZaK6dj67ttNI");
//
//		//私钥解密
//		byte[] decryptE = decryptByPrivateKeyForSpilt(encryptE, com.yunxi.stamper.commons.jwt.RSA.MyKeyFactory.getPrivateKey());
//		System.out.println("私钥解密明文:" + new String(decryptE));

//		String message = "1222222222222222222";
//		JSONObject jsonObject = JSONObject.parseObject(message);

//		System.out.println("运行耗时:" + (System.currentTimeMillis() - l) / 1000.0);
//	}
}
