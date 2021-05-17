package com.yunxi.stamper.commons.jwt;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yunxi.stamper.commons.jwt.RSA.MyKeyFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/24 0024 17:44
 */
@Slf4j
public class JwtUtil {
	private static final String SECRET = "b151a446f32ab4aaa860f36080123f1a";

	/**
	 * 解析token中的值,该方法未经过验证,数据安全性不确定
	 *
	 * @param token
	 * @return
	 */
	public static UserToken getJWT(String token) {
		UserToken userToken = null;
		try {
			DecodedJWT jwt = JWT.decode(token);
			String entityJson = jwt.getClaim("PARAMS").asString();
			userToken = JSONObject.parseObject(entityJson, UserToken.class);
		} catch (JWTDecodeException e) {
			log.error("出现异常\ttoken:{}\terror:{}", token, e.getMessage());
		}
		return userToken;
	}

	/**
	 * 解密
	 *
	 * @param token 令牌
	 * @return
	 */
	public static UserToken parseJWT(String token) {
		UserToken userToken = null;
		if (validate(token)) {
			userToken = getJWT(token);
		}
		return userToken;
	}

	/**
	 * 不要将隐私信息放入（大家都可以获取到）
	 *
	 * @param entity      要添加的参数
	 * @param expireTimes 过期时间/毫秒
	 * @return
	 */
	public static String createJWT(TokenEntity entity, long expireTimes) throws Exception {
		Algorithm algorithm = Algorithm.HMAC256(SECRET);
		String token = JWT.create()
				.withClaim("PARAMS", JSONObject.toJSONString(entity))
				.withExpiresAt(new Date(System.currentTimeMillis() + expireTimes))
				.sign(algorithm);
		return token;
	}

	/**
	 * 创建申请单token
	 *
	 * @param applicationToken 申请单token calm参数列表实例
	 * @return
	 */
	public static String createJWT2(ApplicationToken applicationToken) {
		if (applicationToken != null) {
			Map<String, Object> claims = new HashMap<>();
			claims.put("application_id", applicationToken.getApplication_id());
			claims.put("status", applicationToken.getStatus());
			claims.put("is_qss", applicationToken.getIs_qss());

			try {
				return Jwts.builder().setClaims(claims).signWith(MyKeyFactory.getPrivateKey()).compact();
			} catch (InvalidKeyException e) {
				log.error("出现异常 ", e);
			}
		}
		return null;
	}

	/**
	 * 校验token
	 *
	 * @param token 令牌
	 * @return true:验证成功  false:验证失败
	 */
	public static boolean validate(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(SECRET);
			JWTVerifier verifier = JWT.require(algorithm).build();
			verifier.verify(token);
			return true;
		}
//		catch (UnsupportedEncodingException e) {
//			log.error("令牌解析出错 请重新登录获取新的令牌 TOKEN：【{}】 SECRET：【{}】", token, SECRET, e);
//		} catch (InvalidClaimException ex) {
//			log.error("令牌过期 请重新登录获取新的令牌 TOKEN：【{}】 SECRET：【{}】", token, SECRET, ex);
//		}
		catch (Exception e) {
			log.error("令牌解析异常\t请重新登录获取新的令牌 TOKEN：{}\tSECRET：{}\terror:{}", token, SECRET, e.getMessage());
		}
		return false;
	}

//	public static void main(String[] args) throws Exception {
//		String token = "eyJhbGciOiJIUzI1NiJ9.eyJQQVJBTVMiOiJ7XCJpZFwiOjIzMDM0LFwib3JnSWRcIjotMSxcInV1aWRcIjpcIjBYMjcwMDM1MzMzOTQ3MTUzMzMwMzQzN1wifSIsImV4cCI6MTYwMzA3NDkyM30.a7oilaUswCrEJSgHW56m37wsFUJrCVE46byVdLHOLAc";
//		boolean validate = validate(token);

//		String str = "安徽云玺科技专用秘钥001";
//		System.out.println(MD5.toMD5(str));
//		String jwt = createJWT(new UserToken(12, 13, "张三"), 1000 * 30);
//		System.out.println(jwt);
//		String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJQQVJBTVMiOiJ7XCJvcmdJZFwiOi0xLFwidXNlcklkXCI6OSxcInVzZXJOYW1lXCI6XCLlkajmtbfls7BcIn0iLCJleHAiOjE2MDEwMTU5MjF9.X9eC2IClluxvn5Co_RXtTO01j0zd0yRpfCZzpKAmbF8";
//
//		UserToken jwt1 = getJWT(jwt);
//		System.out.println(JSONObject.toJSONString(jwt1));
//		boolean validate1 = validate(jwt);
//		System.out.println(validate1);
//	}


}
