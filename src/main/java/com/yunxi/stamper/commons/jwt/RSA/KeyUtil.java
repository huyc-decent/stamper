package com.yunxi.stamper.commons.jwt.RSA;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * Key工具类
 */
@Component
public class KeyUtil {
	private static final Logger logger = LoggerFactory.getLogger(KeyUtil.class);
	private static BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();

	public String genPublicKey(String privateKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] decoded = Base64.getDecoder().decode(privateKeyString);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = kf.generatePrivate(keySpec);

		RSAPrivateCrtKey privk = (RSAPrivateCrtKey) privateKey;

		RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(privk.getModulus(), privk.getPublicExponent());

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
		return publicKeyStr;
	}

	public String encrypt(PublicKey publicKey, String context) {
		if (publicKey == null) {
			logger.error("KeyUtil.encrypt--context:{}, publicKey is null", context);
			return context;
		}
		try {
			Cipher cipher = Cipher.getInstance("RSA", bouncyCastleProvider);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(context.getBytes());
			return Base64.getEncoder().encodeToString(output);
		} catch (Exception e) {
			logger.error("KeyUtil.encrypt Exception:", e);
			return context;
		}
	}

	public String decrypt(PrivateKey privateKey, String context) {
		if (privateKey == null) {
			logger.error("KeyUtil.decrypt--context:{}, privateKey is null", context);
			return context;
		}
		try {
			Cipher cipher = Cipher.getInstance("RSA", bouncyCastleProvider);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(Base64.getDecoder().decode(context));
			return new String(output, StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.error("KeyUtil.decrypt Exception:", e);
			return context;
		}
	}
}
