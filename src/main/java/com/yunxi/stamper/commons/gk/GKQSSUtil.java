package com.yunxi.stamper.commons.gk;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhf_10@163.com
 * @Description 国科量子接口工具类
 * @date 2020/4/14 10:01
 */
@Slf4j
public class GKQSSUtil {
	private static final String SERVER_KEY_INDEX = UUID.randomUUID().toString().toLowerCase().replace("-", "");

	/**
	 * 转密钥服务
	 *
	 * @param keyIndex   密钥索引（可以是通道标识、设备标识等）
	 * @param t_keyIndex 转加密设备密钥索引,（可以是通道标识、设备标识等）
	 * @param encryptKey 需要转加密的密钥
	 * @return
	 */
	public static ResponseEntity transCryption(String keyIndex, String t_keyIndex, String encryptKey) {
		if (StringUtils.isAnyBlank(keyIndex, t_keyIndex, encryptKey)) {
			throw new RuntimeException("参数有误");
		}
		String path = "/uk/transCryption";

		Map<String, Object> querys = new HashMap<>();
		querys.put("appId", CommonUtils.gqProperties.getAppId());
		querys.put("keyIndex", keyIndex);
		querys.put("serialIn", UUID.randomUUID().toString().toLowerCase().replace("-", ""));
		querys.put("t_keyIndex", t_keyIndex);
		querys.put("encryptData", encryptKey);

		String res = HttpUtils.postRequest(CommonUtils.gqProperties.getQssHost() + path, querys);
		ResponseEntity gkRes = JSONObject.parseObject(res, ResponseEntity.class);

		log.info("转秘钥服务\tkeyIndex:{}\tt_keyIndex:{}\tencryptKey:{}\tresult:{}", keyIndex, t_keyIndex, encryptKey, gkRes == null ? null : gkRes.toString());
		return gkRes;
	}

	/**
	 * 加密数据
	 *
	 * @param deviceType  设备类型 USBKEY设备 U ，USBKEY软算法  SU， 加密模块 M ，TF卡 T
	 * @param opMode      模式 01：ECB解密，02 CBC解密
	 * @param iv          IV向量值 使用CBC模式时，才需要输入
	 * @param msgType     返回数据类型 1：十六进制; 2: BASE64; 3:可显示的原来的明文
	 * @param encryptData 待加密数据，长度0-1024，明文数据
	 * @return
	 */
	public static ResponseEntity encrypt(String deviceType,
										 String keyIndex,
										 String opMode,
										 String iv,
										 int msgType,
										 String encryptData) throws Exception {
		String path = "/service/encryptData";

		Map<String, Object> querys = new HashMap<>();
		querys.put("appId", CommonUtils.gqProperties.getAppId());
		querys.put("keyIndex", keyIndex);
		querys.put("serialIn", UUID.randomUUID().toString().toLowerCase().replace("-", ""));
		querys.put("deviceType", deviceType);
		querys.put("opMode", opMode);
		querys.put("iv", iv);
		querys.put("msgType", msgType);
		querys.put("encryptData", encryptData);

		String res = HttpUtils.postRequest(CommonUtils.gqProperties.getQssHost() + path, querys);
		ResponseEntity gkRes = JSONObject.parseObject(res, ResponseEntity.class);
		return gkRes;
	}

	/**
	 * 更新密钥
	 *
	 * @param keyIndex 密钥索引（可以是通道标识、设备标识等，由安全管理平台统一分配）
	 * @param serialIn 输入流水号，32位
	 * @return
	 */
	public static ResponseEntity updateWk(String keyIndex, String serialIn) {
		String path = "/moudle/updateWk";

		Map<String, Object> querys = new HashMap<>();
		querys.put("appId", CommonUtils.gqProperties.getAppId());
		querys.put("keyIndex", keyIndex + "");
		querys.put("serialIn", serialIn);
		querys.put("deviceType", "SU");

		String res = HttpUtils.postRequest(CommonUtils.gqProperties.getQssHost() + path, querys);
		ResponseEntity gk_res = JSONObject.parseObject(res, ResponseEntity.class);
		return gk_res;
	}

	/**
	 * 初始化密钥
	 *
	 * @param keyIndex 密钥索引（可以是通道标识、设备标识等，由安全管理平台统一分配）
	 * @param serialIn 输入流水号，32位
	 * @return
	 */
	public static ResponseEntity initKey(String keyIndex, String serialIn) {
		String path = "/uk/initKey";

		Map<String, Object> querys = new HashMap<>();
		querys.put("appId", CommonUtils.gqProperties.getAppId());
		querys.put("keyIndex", keyIndex + "");
		querys.put("serialIn", serialIn);

		String res = HttpUtils.postRequest(CommonUtils.gqProperties.getQssHost() + path, querys);
		ResponseEntity gk_res = JSONObject.parseObject(res, ResponseEntity.class);
		return gk_res;
	}

	/**
	 * 解密数据（待解密数据必须是使用UK控件加密的）
	 *
	 * @param decryptData 待解密数据
	 * @param keyIndex    密钥索引（可以是通道标识、设备标识等，由安全管理平台统一分配）
	 * @return
	 */
	public static ResponseEntity decrypt(String decryptData, String keyIndex) throws Exception {
		String path = "/service/decryptData";

		Map<String, Object> querys = new HashMap<>();
		querys.put("appId", CommonUtils.gqProperties.getAppId());
		querys.put("keyIndex", keyIndex);
		querys.put("serialIn", UUID.randomUUID().toString().toLowerCase().replace("-", ""));
		querys.put("deviceType", "SU");    //设备类型 USBKEY设备 U ，USBKEY软算法  SU， 加密模块 M ，TF卡 T
		querys.put("opMode", "01");    //模式 01：ECB解密，02 CBC解密
		querys.put("iv", null);        //IV向量值 使用CBC模式时，才需要输入
		querys.put("msgType", 3);        //返回数据类型 1：十六进制; 2: BASE64; 3:可显示的原来的明文
		querys.put("decryptData", decryptData);

		String res = HttpUtils.postRequest(CommonUtils.gqProperties.getQssHost() + path, querys);
		ResponseEntity gk_res = JSONObject.parseObject(res, ResponseEntity.class);
		return gk_res;
	}

	/**
	 * 产生随机数
	 *
	 * @param keyIndex 密钥索引（可以是通道标识、设备标识等，由安全管理平台统一分配）
	 * @param len      产生随机数的位数
	 * @return
	 */
	public static ResponseEntity genRandom(String keyIndex, Integer len) {
		if (StringUtils.isBlank(keyIndex)) {
			throw new RuntimeException("参数有误");
		}

		String path = "/service/genRandom";

		Map<String, Object> querys = new HashMap<>();
		querys.put("appId", CommonUtils.gqProperties.getAppId());
		querys.put("keyIndex", keyIndex);
		querys.put("serialIn", UUID.randomUUID().toString().toLowerCase().replace("-", ""));
		querys.put("len", len);

		String res = HttpUtils.postRequest(CommonUtils.gqProperties.getQssHost() + path, querys);
		ResponseEntity gk_res = JSONObject.parseObject(res, ResponseEntity.class);
		return gk_res;
	}

	/**
	 * 生成MAC
	 *
	 * @param keyIndex 密钥索引（可以是通道标识、设备标识等，由安全管理平台统一分配）
	 * @param message  计算的数据
	 * @return
	 */
	public static ResponseEntity genMac(String keyIndex, String message) {
		if (StringUtils.isAnyBlank(message, keyIndex)) {
			throw new RuntimeException("参数有误");
		}
		String path = "/service/genMac";

		Map<String, Object> querys = new HashMap<>();
		querys.put("appId", CommonUtils.gqProperties.getAppId());
		querys.put("keyIndex", keyIndex);
		querys.put("serialIn", UUID.randomUUID().toString().toLowerCase().replace("-", ""));
		/***设备类型 USBKEY设备 U ，加密模块 M  ，TF卡 T  */
		querys.put("deviceType", "SU");
		/***模式 1：十六进制; 2: BASE64; 3:可显示的原来的明文*/
		querys.put("msgType", 3);
		querys.put("message", message);
		String res = HttpUtils.postRequest(CommonUtils.gqProperties.getQssHost() + path, querys);
		ResponseEntity gk_res = JSONObject.parseObject(res, ResponseEntity.class);
		return gk_res;
	}

	/**
	 * 验证MAC
	 *
	 * @param keyIndex 密钥索引（可以是通道标识、设备标识等，由安全管理平台统一分配）
	 * @param mac      计算的MAC值
	 * @param srcMac   计算的数据
	 * @return
	 */
	public static ResponseEntity checkMac(String keyIndex, String mac, String srcMac) {
		if (StringUtils.isAnyBlank(keyIndex, mac, srcMac)) {
			throw new RuntimeException("参数有误");
		}
		String path = "/service/genMac";

		Map<String, Object> querys = new HashMap<>();
		querys.put("appId", CommonUtils.gqProperties.getAppId());
		querys.put("keyIndex", keyIndex);
		querys.put("serialIn", UUID.randomUUID().toString().toLowerCase().replace("-", ""));
		/***设备类型 USBKEY设备 U ，加密模块 M  ，TF卡 T  */
		querys.put("deviceType", "SU");
		/***模式 1：十六进制; 2: BASE64; 3:可显示的原来的明文*/
		querys.put("msgType", 3);
		querys.put("macCode", mac);
		querys.put("message", srcMac);

		String res = HttpUtils.postRequest(CommonUtils.gqProperties.getQssHost() + path, querys);
		ResponseEntity gk_res = JSONObject.parseObject(res, ResponseEntity.class);
		return gk_res;
	}
}
