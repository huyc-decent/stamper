package com.yunxi.stamper.commons.jwt.RSA;

import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/1/23 0023 18:55
 */
public class MyKeyFactory {
	public static String str_pubK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgUJisBVFRUHIaHG7swKN0elX/HuFNiYbs5dL2ziQjbgF8LoQ3mCgJ6Q78c9ucIi2avQ7cBvZVfLSEEIXI1elOt7q1hHa4RmqjCbGjocnDlDDqGjb9UI3Ur/Ok0i0hzTbc2lihFgfWppJRp5CnjXlRft1b0nnRqpxoD8cxypKf9NYjVHMWf/jiu3ujhVANQVD4CyijkB3JJYmkSOBGFz3TTEsHkU8C9NUw+4L76X6Y9RtFGUIwBO6pKMR10QFe4ck/sRvMktU6/ywCDV/hZbWv26Umn6JEP4S/DI+j0EUfeC458RHvZTPGHWiq/UWx3sw9pwSVFfKOsYKsChbZ6WhNwIDAQAB";
	public static String str_priK = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCBQmKwFUVFQchocbuzAo3R6Vf8e4U2Jhuzl0vbOJCNuAXwuhDeYKAnpDvxz25wiLZq9DtwG9lV8tIQQhcjV6U63urWEdrhGaqMJsaOhycOUMOoaNv1QjdSv86TSLSHNNtzaWKEWB9amklGnkKeNeVF+3VvSedGqnGgPxzHKkp/01iNUcxZ/+OK7e6OFUA1BUPgLKKOQHckliaRI4EYXPdNMSweRTwL01TD7gvvpfpj1G0UZQjAE7qkoxHXRAV7hyT+xG8yS1Tr/LAINX+Flta/bpSafokQ/hL8Mj6PQRR94LjnxEe9lM8YdaKr9RbHezD2nBJUV8o6xgqwKFtnpaE3AgMBAAECggEBAIDWVZ2jJe95OTN5oZg1BHzlM/ESgV3OsC/arx5sDBFmCm2+WE//ScMZfTJyCmearRCALbp517BGnsDbz0pH8wZx6OrE00EpHwghIiowZmprcAotsoiMnq4ZRuMhRee6dL5dnXfCikX5oO4Fkus1VzjhAlWR+TdDboxGO/38llM/PWUJ5eUYj2QEztzKy/IsGguZ3WkeNS7lFj9XyZ/j+7SicWv99VxBUqraA3v9tsQkX5nFysuuUxslTPgDTKVHRtougXrHfYw1ASkk1gvGtK52DIfC4ViruqvNegHCxdnnGXyyQv0C2FJw8a5Gk3nTkcyt0WSAbp2lZRpM31akvXECgYEAzDnhISl+iHW+DWrOVHH68834V42pahNRFV4p72RlN3HJT4pCJketUBuGz3sLHwT12PGHH7ImUfzPMNZ8NaqUh8N79M3TpU8tVRQtgNbn9e/ueABJmSmLjNdEzxWEJ9d1A2/CMiGdyshn3cIyTKgGQS5BJ+CjYU6vnAIP4yjRFU8CgYEAogc39RH7gTpYmSLE1sqfkws5k2LVLHRILaU2lYBzRwpDs29YMjqd53v0krdfNfxohClt+lrZSiD1Qajjah/CQ995u7aH/tsLlbLCsNRLa5nPtdblBU2zhuMDcfqVWRv8k50LJne4BpGlE68UtYfrzw/hz54O0whL5GlbTTaCi5kCgYBuRQoz163EjJ6TrAnAOtLfdWUUER9acReky/Uklza3my6xTdutw6Hm0RYXTT6R/yGng7IMASsDtddBbW4fo/0S2RBC/Ce86GV3vK9dE6yndGd0T+NtWatJ3qn+joWO5Zz+wAdA/jmu1kqOyF5UWZ0W8JyppXdSASR8vfhKFS3frQKBgHA6c8XqKLy//zJC5Pip7JHbSowN/v4FpSEIuKAhErf4IiCdVQellk4Ki8M8BFTOek5gq+6nEq7H2VkbdDnDublth1JAAj7C1mlgIn587aigJ0EakhN0WI9rmq1OFjhcrDxKoLiKYAscwqy5rqx2cx0/MPev0TDXJEoXt8fpo86xAoGAVIVqC6Tc1S1uuLg7XSbyoATzKRC0ibdEH+Cr4KF6Fu7qSIQKfhH9/Px5GA1dsZ8YcLGuqkIl+om4NazBj+RgytyAYXlZs/aEQkWZbz7qQpxjG9dCf0s/81jcSjnwYBxsWsbl85rmW6S7qjnAf3SNIN77zWX46qTrjyy3SCAo6gA=";

	/**
	 * 实例化私钥
	 */
	public static PrivateKey getPrivateKey() {
		PrivateKey privateKey = null;
		PKCS8EncodedKeySpec priPKCS8;
		try {
			priPKCS8 = new PKCS8EncodedKeySpec(new BASE64Decoder().decodeBuffer(str_priK));
			java.security.KeyFactory keyf = java.security.KeyFactory.getInstance("RSA");
			privateKey = keyf.generatePrivate(priPKCS8);
		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return privateKey;
	}

	/**
	 * 实例化公钥
	 *
	 * @return
	 */
	public static PublicKey getPubKey() {
		PublicKey publicKey = null;
		try {
			// 自己的公钥(测试)
			X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(str_pubK));
			// RSA对称加密算法
			KeyFactory keyFactory;
			keyFactory = KeyFactory.getInstance("RSA");
			// 取公钥匙对象
			publicKey = keyFactory.generatePublic(bobPubKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return publicKey;
	}
}
