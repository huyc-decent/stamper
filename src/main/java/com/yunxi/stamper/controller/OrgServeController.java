package com.yunxi.stamper.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.OrgServeVo;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.*;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 17:24
 */
@Slf4j
@Api(tags = "集团<==>服务")
@RestController
@RequestMapping(value = "/auth/orgServe", method = {RequestMethod.POST, RequestMethod.GET})
public class OrgServeController extends BaseController {

	@Autowired
	private OrgServeService service;
	@Autowired
	private OrgService orgService;
	@Autowired
	private ServeService serveService;
	@Autowired
	private UserService userService;
	@Autowired
	private SysUserService sysUserService;

	/**
	 * 检查指定公司是否拥有指定的服务
	 *
	 * @param orgId 组织ID
	 * @param code  服务CODE
	 * @return true:有服务  false：没有服务
	 */
	@RequestMapping("/checkServer")
	public Boolean checkServer(@RequestParam("orgId") Integer orgId, @RequestParam("code") String code) {
		if (orgId != null && StringUtils.isNotBlank(code)) {
			OrgServe orgServe = service.getByOrgAndCode(orgId, code);
			return orgServe != null;
		}
		return false;
	}

	/**
	 * 分页查询平台公司服务列表状态
	 */
	@RequestMapping("/getAll")
	public ResultVO getAll(@RequestParam(required = false) String keyword,
						   @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
						   @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
						   @RequestParam(value = "page", required = false, defaultValue = "true") boolean isPage) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		if (isPage) {
			PageHelper.startPage(pageNum, pageSize);
		}
		List<Org> orgs = orgService.get(keyword);
		if (orgs == null || orgs.isEmpty()) {
			return ResultVO.OK();
		}

		PageInfo pageInfo = new PageInfo(orgs);

		List<Map<String, Object>> resMap = new ArrayList<>();
		for (Org org : orgs) {
			Integer orgId = org.getId();
			String name = org.getName();
			String code = org.getCode();

			Map<String, Object> map = new HashMap<>();
			map.put("id", orgId);
			map.put("code", code);
			map.put("name", name);
			map.put("createDate", org.getCreateDate());

			//属主
			Integer managerUserId = org.getManagerUserId();
			User user = userService.get(managerUserId);
			if (user != null) {
				map.put("username", user.getUserName());

				Integer sysUserId = user.getSysUserId();
				SysUser sysUser = sysUserService.get(sysUserId);
				if (sysUser != null) {
					map.put("phone", sysUser.getPhone());
				}
			}

			//量子服务是否开通?
			OrgServe qss = service.getByOrgAndCode(orgId, "QSS");
			map.put("qss", qss != null);

			//短信服务是否开通?
			OrgServe sms = service.getByOrgAndCode(orgId, "SMS");
			map.put("sms", sms != null);

			//基础服务是否开通?
			OrgServe base = service.getByOrgAndCode(orgId, "DEVICE");
			map.put("base", base != null);

			resMap.add(map);
		}
		pageInfo.setList(resMap);
		return ResultVO.OK(pageInfo);

//		List<OrgServeVo> osv = orgService.getByAll();
//		for (OrgServeVo os : osv) {
//			//短信服务是否开通?
//			OrgServe sms = service.getByOrgAndCode(os.getId(), "SMS");
//			os.setSMS(sms != null);
//			//基础服务是否开通?
//			OrgServe base = service.getByOrgAndCode(os.getId(), "DEVICE");
//			os.setBase(base != null);
//			//量子服务是否开通?
//			OrgServe qss = service.getByOrgAndCode(os.getId(), "QSS");
//			os.setQss(qss != null);
//			//云存储服务器是否开通?
//			OrgServe oos = service.getByOrgAndCode(os.getId(), "OOS");
//			os.setOos(oos != null);
//		}
//		return ResultVO.Page(osv, page);
	}

	/**
	 * 更新公司短信服务状态
	 */
	@RequestMapping("/updateSMS")
	public ResultVO updateSMS(@RequestParam("orgId") Integer orgId,
							  @RequestParam("isUse") boolean isUse) {

		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		//查询短信服务实例
		Serve smsServe = serveService.getByCode("SMS");
		if (smsServe == null) {
			return ResultVO.FAIL("该服务不存在");
		}
		//查询公司是否存在
		Org org = orgService.get(orgId);
		if (org == null) {
			return ResultVO.FAIL("该公司不存在");
		}
		if (isUse) {
			OrgServe os = new OrgServe();
			os.setOrgId(orgId);
			os.setServeId(smsServe.getId());
			service.add(os);
		} else {
			OrgServe orgServe = service.getByOrgAndCode(orgId, "SMS");
			service.del(orgServe);
		}
		return ResultVO.OK("已更新");
	}

	/**
	 * 更新公司量子服务状态
	 */
	@RequestMapping("/updateQSS")
	public ResultVO updateQSS(@RequestParam("orgId") Integer orgId,
							  @RequestParam("isUse") boolean isUse) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		//查询量子服务实例
		Serve qssServe = serveService.getByCode("QSS");
		if (qssServe == null) {
			return ResultVO.FAIL("该服务不存在");
		}
		//查询公司是否存在
		Org org = orgService.get(orgId);
		if (org == null) {
			return ResultVO.FAIL("该公司不存在");
		}
		if (isUse) {
			OrgServe os = new OrgServe();
			os.setOrgId(orgId);
			os.setServeId(qssServe.getId());
			service.add(os);
		} else {
			OrgServe orgServe = service.getByOrgAndCode(orgId, "QSS");
			service.del(orgServe);
		}
		return ResultVO.OK("已更新");
	}

	/**
	 * 更新公司基础服务状态
	 */
	@RequestMapping("/updateDevice")
	public ResultVO updateDevice(@RequestParam("orgId") Integer orgId, @RequestParam("isUse") boolean isUse) {
		//仅平台账户可用
		UserInfo userInfo = getUserInfo();
		if (userInfo.getType() == null || userInfo.getType() != 0) {
			return ResultVO.FAIL(Code.FAIL403);
		}

		//查询短信服务实例
		Serve deviceServe = serveService.getByCode("DEVICE");
		if (deviceServe == null) {
			return ResultVO.FAIL("该服务不存在");
		}
		//查询公司是否存在
		Org org = orgService.get(orgId);
		if (org == null) {
			return ResultVO.FAIL("该公司不存在");
		}
		if (isUse) {
			OrgServe os = new OrgServe();
			os.setOrgId(orgId);
			os.setServeId(deviceServe.getId());
			service.add(os);
		} else {
			OrgServe orgServe = service.getByOrgAndCode(orgId, "DEVICE");
			service.del(orgServe);
		}
		return ResultVO.OK("已更新");
	}
}
