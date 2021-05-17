package com.yunxi.stamper.controller;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.gk.GKQSSUtil;
import com.yunxi.stamper.commons.gk.ResponseEntity;
import com.yunxi.stamper.commons.jwt.JwtUtil;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.other.HttpUtils;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.FileInfo;
import com.yunxi.stamper.entityVo.FileEntity;
import com.yunxi.stamper.service.FileInfoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/6/15 11:22
 */
@Slf4j
@Api(tags = "国科量子相关")
@RestController
@RequestMapping("/gkQss")
public class GkQssController extends BaseController {

	@Autowired
	private FileInfoService fileInfoService;

	@RequestMapping("/initKey")
	public ResultVO initKey(String uuid) {
		ResponseEntity key = GKQSSUtil.initKey(uuid, UUID.randomUUID().toString().toLowerCase().replace("-", ""));

		log.debug("初始化秘钥\tUUID:{}\tRES:{}", uuid, JSONObject.toJSONString(key));
		return ResultVO.OK(key);
	}

	@GetMapping("/transCryption")
	public ResultVO transCryption(String fileId) {
		FileEntity fileEntity = fileInfoService.getReduceImgURLByFileId(fileId);
		return ResultVO.OK(fileEntity);
	}

	@GetMapping("/transCryption1")
	public ResultVO transCryption1(String fileId, String tKeyIndex) {
		if (StringUtils.isAnyBlank(fileId, tKeyIndex)) {
			return ResultVO.FAIL(Code.FAIL402);
		}

		FileInfo fileInfo = fileInfoService.get(fileId);
		if (fileInfo == null) {
			return ResultVO.FAIL("图片不存在");
		}

		String secretKey = fileInfo.getSecretKey();
		String keyIndex = fileInfo.getKeyIndex();

		if (StringUtils.isAnyBlank(secretKey, keyIndex)) {
			return ResultVO.FAIL("该图片非国科量子密文");
		}

		ResponseEntity responseEntity = GKQSSUtil.transCryption(keyIndex, tKeyIndex, secretKey);
		String value = responseEntity.getValue();
		return ResultVO.OK(value);
	}

//	public static void main(String[] args) throws Exception {
//		String path = "/uk/initKey";
//
//		Map<String, Object> querys = new HashMap<>();
//		querys.put("appId", "000");
//		querys.put("keyIndex", "0X2B004D3238511239343734");
//		querys.put("serialIn", UUID.randomUUID().toString().toLowerCase().replace("-", ""));
//
//		UserToken token = new UserToken(-1, -1, "超级管理员");
//		String jwt = JwtUtil.createJWT(token, 100000000);
//		String path = "/uk/transCryption";
//
//		Map<String, Object> querys = new HashMap<>();
//		querys.put("appId", "000");
//		querys.put("keyIndex", "0X2B004D3238511239343734");
//		querys.put("serialIn", UUID.randomUUID().toString().toLowerCase().replace("-", ""));
//		querys.put("t_keyIndex", "0X2B004D3238511239343731");
//		querys.put("encryptData", "0X2B004D323851123934373422222222222222222222");


//		String res = HttpUtils.postRequest("http://60.173.247.191:9095" + path, querys);
//		ResponseEntity gk_res = JSONObject.parseObject(res, ResponseEntity.class);

//	}
}
