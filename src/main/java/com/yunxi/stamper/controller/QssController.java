package com.yunxi.stamper.controller;


import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.OrgServe;
import com.yunxi.stamper.entity.Qss;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.OrgServeService;
import com.yunxi.stamper.service.QssService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/10 0010 20:22
 */
@Api(tags = "量子加密服务相关")
@RequestMapping("/auth/qss")
@RestController
public class QssController extends BaseController {

	@Autowired
	private QssService service;
	@Autowired
	private OrgServeService orgServeService;

	/**
	 * 查询所有量子加解密URL数组
	 */
	@RequestMapping("/getByArray")
	public ResultVO getByArray() {
		//查询该用户所属公司是否支持量子加密
		OrgServe qss = null;
		try {
			//用try catch 捕获异常,防止退出登录后,抛出异常
			UserToken token = getToken();
			qss = orgServeService.getByOrgAndCode(token.getOrgId(), "QSS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (qss == null) {
			//该公司没有量子加密
			return ResultVO.OK();
		}

		//有量子加密,查询加密URL数组
		List<String> qsses;
		if (isApp()) {
			//移动端
			qsses = service.getByArray(1);
		} else {
			//web端
			qsses = service.getByArray(0);
		}
		return ResultVO.OK(qsses);
	}

	/**
	 * 查询所有量子加解密URL列表
	 */
	@RequestMapping("/getAll")
	public ResultVO getAll() {
		boolean page = setPage();
		List<Qss> qsses = service.getAll();
		return ResultVO.Page(qsses, page);
	}

	/**
	 * 删除对应量子加解密URL
	 */
	@RequestMapping("/del")
	public ResultVO del(@RequestParam("qssId") Integer qssId) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		Qss qss = service.get(qssId);
		if (qss != null) {
			service.del(qss);
			return ResultVO.OK("删除成功");
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	/**
	 * 添加量子加密URL
	 */
	@RequestMapping("/add")
	public ResultVO add(String name, String remark, Integer type, String url) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		Qss qss = new Qss();
		qss.setName(name);
		qss.setRemark(remark);
		qss.setUrl(url);
		qss.setType(type);

		Qss urlQss = service.getByUrl(url, type);
		if (urlQss != null) {
			return ResultVO.FAIL("该URL已设置");
		}
		Qss nameQss = service.getByName(name);
		if (nameQss != null) {
			return ResultVO.FAIL("名称重复");
		}
		service.add(qss);
		return ResultVO.OK("添加成功");
	}

	/**
	 * 编辑量子加密URL
	 */
	@RequestMapping("/update")
	public ResultVO update(Integer id, String name, String remark, Integer type, String url) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		Qss update = service.get(id);
		if (update != null) {
			Qss urlQss = service.getByUrl(url);
			if (urlQss != null && urlQss.getId().intValue() != id) {
				return ResultVO.FAIL("该URL已设置");
			}
			Qss nameQss = service.getByName(name);
			if (nameQss != null && nameQss.getId().intValue() != id) {
				return ResultVO.FAIL("名称重复");
			}

			update.setName(name);
			update.setRemark(remark);
			update.setUrl(url);
			update.setType(type);

			service.update(update);
			return ResultVO.OK("更新成功");
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

}
