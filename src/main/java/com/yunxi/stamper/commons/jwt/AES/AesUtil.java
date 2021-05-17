package com.yunxi.stamper.commons.jwt.AES;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/8/23 0023 15:53
 */
public class AesUtil {
	/**
	 * 算法名称
	 */
	private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
	private static final String SECRETKEY = "AES";


	/**
	 * content: 加密内容
	 * slatKey: 加密的盐，16位字符串
	 * vectorKey: 加密的向量，16位字符串
	 */
	public static String encrypt(String content, String slatKey) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);//CBC(有向量模式)和ECB(无向量模式),传入“AES/CBC/NoPadding”可进行AES加密，传入"DESede/CBC/NoPadding"可进行DES3加密
		SecretKey secretKey = new SecretKeySpec(slatKey.getBytes(), SECRETKEY);
		//偏移量
//		byte[] bb= new byte[]{0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1};
//		IvParameterSpec iv = new IvParameterSpec(bb, 0,16);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encrypted = cipher.doFinal(content.getBytes());
		return Base64.encodeBase64String(encrypted);
	}

	/**
	 * content: 解密内容(base64编码格式)
	 * slatKey: 加密时使用的盐，16位字符串
	 * vectorKey: 加密时使用的向量，16位字符串
	 */
	public static String decrypt(String base64Content, String slatKey) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);//CBC(有向量模式)和ECB(无向量模式),传入“AES/CBC/NoPadding”可进行AES加密，传入"DESede/CBC/NoPadding"可进行DES3加密
		SecretKey secretKey = new SecretKeySpec(slatKey.getBytes(), SECRETKEY);
		//偏移量
//		byte[] bb= new byte[]{0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1};
//		IvParameterSpec iv = new IvParameterSpec(bb, 0,16);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] content = Base64.decodeBase64(base64Content);
		byte[] encrypted = cipher.doFinal(content);
		return new String(encrypted, StandardCharsets.UTF_8);
	}

//	public static void main(String[] args) throws Exception {

//		File file = new File("D:\\upload\\YXCS\\2019\\7\\10\\1562757088351.jpg");
//		FileInputStream fis = new FileInputStream(file);
//		byte[] bytes = new byte[fis.available()];
//		fis.read(bytes);
//		BASE64Encoder encoder = new BASE64Encoder();
//		String png_base64 = encoder.encodeBuffer(bytes).trim();//转换成base64串
//		png_base64 = "data:image/png;base64,"+png_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
//
////		png_base64 = "12345678";
//
//		System.out.println("图片base64：" + png_base64);
//
//		String slatKey = RandomUtil.getRandomNum(16);
//		System.out.println("秘钥:" + slatKey);
//		long l = System.currentTimeMillis();
//		System.out.println("明文长度:" + png_base64.length());
//		String encrypt = encrypt(png_base64, slatKey);
//		System.out.println("密文:" + encrypt);
//		String decrypt = decrypt(encrypt, slatKey);
//		System.out.println("明文:" + decrypt);
//		System.out.println("运行耗时:" + (System.currentTimeMillis() - l) / 1000.0);
//
//		System.out.println("密文占用内存:"+encrypt.getBytes().length/1024 + "KB");

//		String decrypt = encrypt("测试1111111112222333abcddd", "4058741311854138");
//		System.out.println(decrypt);
//	}
}
