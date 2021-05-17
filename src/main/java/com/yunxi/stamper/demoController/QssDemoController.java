package com.yunxi.stamper.demoController;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.commons.other.DateUtil;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.other.RedisUtil;
import com.yunxi.stamper.commons.response.ResultVO;
import com.zengtengpeng.annotation.Lock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/11/4 15:51
 */
@RestController
@RequestMapping("/device/qss")
public class QssDemoController {

	private RedisUtil redisUtil;

	@Autowired
	public void setRedisUtil(RedisUtil redisUtil) {
		this.redisUtil = redisUtil;
	}

	@GetMapping("/message")
	@Lock(keys = "#deviceId", keyConstant = "_qss_demo_message")
	public ResultVO message(@RequestParam Integer deviceId) {
		//从缓存中取出key列表
		String redisKey = RedisGlobal.QSS_DEMO_KEY + deviceId;
		Set<String> keys = redisUtil.keys(redisKey + "*");

		//从缓存中取出value列表
		List<QssDemoEntity> messages = null;
		if (keys != null && keys.size() > 0) {
			messages = new ArrayList<>();
			for (String key : keys) {
				Object listJson = redisUtil.get(key);
				//删除缓存
				redisUtil.del(key);

				if (listJson == null || StringUtils.isBlank(listJson.toString())) {
					continue;
				}

				List<QssDemoEntity> qssDemoEntities = JSONObject.parseArray(listJson.toString(), QssDemoEntity.class);
				if (qssDemoEntities != null && !qssDemoEntities.isEmpty()) {
					messages.addAll(qssDemoEntities);
				}
			}
		}

		return ResultVO.OK(messages);
	}


}
