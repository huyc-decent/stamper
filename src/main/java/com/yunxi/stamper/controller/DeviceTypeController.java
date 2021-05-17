package com.yunxi.stamper.controller;

import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.DeviceType;
import com.yunxi.stamper.service.DeviceTypeService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/29 0029 16:33
 */
@Api(tags = "设备类型相关")
@RestController
@RequestMapping(value = "/device/deviceType", method = {RequestMethod.POST, RequestMethod.GET})
public class DeviceTypeController extends BaseController {

	@Autowired
	private DeviceTypeService service;

	/**
	 * 查询公司类型列表
	 *
	 * @return 结果
	 */
	@RequestMapping("getByOrg")
	public ResultVO getByOrg() {
		UserToken token = getToken();
		List<DeviceType> dts = service.getByOrg(token.getOrgId());
		if (dts == null || dts.isEmpty()) {
			return ResultVO.OK(dts);
		}
		DeviceType dt = new DeviceType();
		dt.setName("所有章");
		dt.setOrgId(token.getOrgId());
		dts.add(0, dt);
		return ResultVO.OK(dts);
	}

	/**
	 * 查询公司类型列表(移动端`使用记录`面板类型)
	 *
	 * @return 结果
	 */
	@RequestMapping("getByOrgToApp")
	public ResultVO getByOrgToApp() {
		UserToken token = getToken();
		List<DeviceType> dts = service.getByOrg(token.getOrgId());
		if (dts == null || dts.isEmpty()) {
			return ResultVO.OK(dts);
		}
		for (int i = 0; i < dts.size(); i++) {
			DeviceType deviceType = dts.get(i);
			String name = deviceType.getName();
			if (StringUtils.isBlank(name) || "高拍仪".equalsIgnoreCase(name)) {
				dts.remove(i);
			}
		}

		DeviceType dt = new DeviceType();
		dt.setName("所有章");
		dt.setOrgId(token.getOrgId());
		dts.add(0, dt);
		return ResultVO.OK(dts);
	}


}
