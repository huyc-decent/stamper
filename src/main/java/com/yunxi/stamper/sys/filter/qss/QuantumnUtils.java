package com.yunxi.stamper.sys.filter.qss;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.quantum.core.QuantumService;
import com.yunxi.quantum.utils.sm4.SM4Utils;
import com.yunxi.stamper.commons.other.Global;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class QuantumnUtils {

	private static QuantumService quantumService;

	@Autowired
	public void setQuantumService(QuantumService quantumService) {
		QuantumnUtils.quantumService = quantumService;
	}


	/**
	 * 量子解密工具
	 *
	 * @param encContentss
	 * @return
	 */
	public synchronized static Map<String, String> quantumnDecrpy(String tickets, String encContentss) {
		String keys = quantumService.getTicket(tickets);

		SM4Utils sm4 = new SM4Utils();
		sm4.setSecretKey(DatatypeConverter.parseHexBinary(keys));
		sm4.setHexString(true);

		byte[] dec = sm4.decryptData_ECB(DatatypeConverter.parseHexBinary(encContentss));
		String plainText = null;
		try {
			plainText = new String(dec, StandardCharsets.UTF_8);
		} catch (Exception e) {
			log.error("量子解密 出现异常", e);
		}
		Map<String, String> result = new HashMap<>();
		if (StringUtils.isNotBlank(plainText)) {
			try {
				HashMap hashMap = JSONObject.parseObject(plainText, HashMap.class);
				result.putAll(hashMap);
			} catch (Exception e) {
				log.error("量子解密 出现异常", e);
			}
		}
		result.put(Global.ticket, keys);
		return result;
	}
}
