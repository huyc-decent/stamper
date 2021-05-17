package com.yunxi.stamper.controller;


import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.Org;
import com.yunxi.stamper.entity.StrategyPassword;
import com.yunxi.stamper.entity.User;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.OrgService;
import com.yunxi.stamper.service.StrategyPasswordService;
import com.yunxi.stamper.service.UserService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Api(tags = "密码策略相关")
@RestController
@RequestMapping(value = "/auth/strategypassword")
public class StrategyPasswordController extends BaseController {
	@Autowired
	private StrategyPasswordService service;
	@Autowired
	private UserService userService;
	@Autowired
	private OrgService orgService;

	//获取当前公司的密码规则
	@RequestMapping("/get")
	public ResultVO get() {
		StrategyPassword sp = service.getByOrg(properties.getDefaultOrgId());
		Map<String, Object> result = new HashMap<>();
		if (sp != null) {
			result.put("id", sp.getId());
			result.put("orgId", sp.getOrgId());
			result.put("len_min", sp.getLenMin());
			result.put("len_max", sp.getLenMax());
			result.put("firstChar", sp.getFirstChar());
			List<String> status = new ArrayList<>();
			Integer upperStatus = sp.getUpperStatus();
			if (upperStatus != null && upperStatus == 1) {
				status.add("upper_status");
			}

			Integer lowerStatus = sp.getLowerStatus();
			if (lowerStatus != null && lowerStatus == 1) {
				status.add("lower_status");
			}

			Integer numStatus = sp.getNumStatus();
			if (numStatus != null && numStatus == 1) {
				status.add("num_status");
			}

			Integer charStatus = sp.getCharStatus();
			if (charStatus != null && charStatus == 1) {
				status.add("char_status");
			}

			result.put("status", status);
		}
		return ResultVO.OK(result);
	}

	//获取本公司的密码规则
	@RequestMapping(value = "/getTips", produces = "application/json;UTF-8")
	public ResultVO getTips() {
		UserInfo user = getUserInfo();
		//缓存没有,从数据库查询
		StrategyPassword sp = service.getByOrg(properties.getDefaultOrgId());
		if (sp != null) {
			List<Object> result = new LinkedList<>();
			boolean flag = false;
			//长度
			Integer lenMin = sp.getLenMin();
			Integer lenMax = sp.getLenMax();
			if (lenMax != null || lenMin != null) {
				flag = true;
				if (lenMax != null && lenMin != null) {
					//最小最大都有
					result.add("长度必须在 " + lenMin + "~" + lenMax + " 之间");
				} else {
					//进来的肯定有一个不是空
					String str = (lenMin == null) ? ("密码有最大长度" + lenMax) : ("密码有最小长度" + lenMin);
					result.add(str);
				}
			}

			//首字母
			Integer firstChar = sp.getFirstChar();
			if (firstChar != null) {
				String tipKey = null;
				switch (firstChar) {
					case 0:
						tipKey = "首字母必须是数字";
						break;
					case 1:
						tipKey = "首字母必须是大写字母";
						break;
					case 2:
						tipKey = "首字母必须是小写字母";
						break;
					case 3:
						tipKey = "首字母必须是特殊字符";
						break;
					default:
				}
				if (StringUtils.isNotBlank(tipKey)) {
					flag = true;
					result.add(tipKey);
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append("必须包含");
			Integer upperStatus = sp.getUpperStatus();
			Integer lowerStatus = sp.getLowerStatus();
			//判断大小写字母是否启用，初始时两者都是null
			if (upperStatus != null || lowerStatus != null) {
				//两者其中一个必须不为空
				if (upperStatus != null && lowerStatus != null) {
					//都不为空：判断值
					String str = upperStatus == 1 ? "大写字母" : (lowerStatus == 1 ? "小写字母" : "");
					if (StringUtils.isNotBlank(str)) {
						flag = true;
						sb.append(str);
					}
					if (upperStatus == 1 && lowerStatus == 1) {
						str = "大写和小写字母";
						sb.append(str);
					}
				} else {
					//其中一个肯定是null
					String str;
					if (upperStatus != null) {
						str = upperStatus == 1 ? "大写字母" : "";
						if (StringUtils.isNotBlank(str)) {
							flag = true;
							sb.append(str);
						}
					} else {
						//lo==null
						str = lowerStatus == 1 ? "小写字母" : "";
						if (StringUtils.isNotBlank(str)) {
							flag = true;
							sb.append(str);
						}
					}
				}
			}
			Integer numStatus = sp.getNumStatus();
			if (numStatus != null) {
				String str = numStatus == 1 ? " 数字" : "";
				if (StringUtils.isNotBlank(str)) {
					flag = true;
					sb.append(str);
				}
			}
			Integer charStatus = sp.getCharStatus();
			if (charStatus != null) {
				String str = charStatus == 1 ? " 特殊字符" : "";
				if (StringUtils.isNotBlank(str)) {
					flag = true;
					sb.append(str);
				}
			}
			if (!"必须包含".equals(sb.toString())) {
				result.add(sb.toString());
			}
			if (flag) {
				return ResultVO.OK(result);
			}
		}
		return ResultVO.OK();
	}

	//用户注册时，通过组织编码获取密码策略
	@RequestMapping("/getTipsByCode")
	public ResultVO getTipsByCode(String code) {
		if (StringUtils.isNotBlank(code)) {
			Org byCode = orgService.getByCode(code);
			if (byCode != null) {
				StrategyPassword sp = service.getByOrg(properties.getDefaultOrgId());
				if (sp != null) {
					List<Object> result = new LinkedList<>();
					boolean flag = false;
					//长度
					Integer lenMin = sp.getLenMin();
					Integer lenMax = sp.getLenMax();
					if (lenMax != null || lenMin != null) {
						flag = true;
						if (lenMax != null && lenMin != null) {
							//最小最大都有
							result.add("长度必须在 " + lenMin + "~" + lenMax + " 之间");
						} else {
							//进来的肯定有一个不是空
							String str = (lenMin == null) ? ("密码有最大长度" + lenMax) : ("密码有最小长度" + lenMin);
							result.add(str);
						}
//                    result.add("长度必须在 " + lenMin + "~" + lenMax + " 之间");
					}

					//首字母
					Integer firstChar = sp.getFirstChar();
					if (firstChar != null) {
						String tipKey = null;
						switch (firstChar) {
							case 0:
								tipKey = "首字母必须是数字";
								break;
							case 1:
								tipKey = "首字母必须是大写字母";
								break;
							case 2:
								tipKey = "首字母必须是小写字母";
								break;
							case 3:
								tipKey = "首字母必须是特殊字符";
								break;
							default:
						}
						if (StringUtils.isNotBlank(tipKey)) {
							flag = true;
							result.add(tipKey);
						}
					}

					StringBuilder sb = new StringBuilder();
					sb.append("必须包含");
					Integer upperStatus = sp.getUpperStatus();
					Integer lowerStatus = sp.getLowerStatus();
					//判断大小写字母是否启用，初始时两者都是null
					if (upperStatus != null || lowerStatus != null) {
						//两者其中一个必须不为空
						if (upperStatus != null && lowerStatus != null) {
							//都不为空：判断值
							String str = upperStatus == 1 ? "大写字母" : (lowerStatus == 1 ? "小写字母" : "");
							if (StringUtils.isNotBlank(str)) {
								flag = true;
								sb.append(str);
							}
							if (upperStatus == 1 && lowerStatus == 1) {
								str = "大写和小写字母";
								sb.append(str);
							}
						} else {
							//其中一个肯定是null
							String str;
							if (upperStatus != null) {
								str = upperStatus == 1 ? "大写字母" : "";
								if (StringUtils.isNotBlank(str)) {
									flag = true;
									sb.append(str);
								}
							} else {
								//lo==null
								str = lowerStatus == 1 ? "小写字母" : "";
								if (StringUtils.isNotBlank(str)) {
									flag = true;
									sb.append(str);
								}
							}
						}
					}
					Integer numStatus = sp.getNumStatus();
					if (numStatus != null) {
						String str = numStatus == 1 ? " 数字" : null;
						if (StringUtils.isNotBlank(str)) {
							flag = true;
							sb.append(str);
						}
					}
					Integer charStatus = sp.getCharStatus();
					if (charStatus != null) {
						String str = charStatus == 1 ? " 特殊字符" : null;
						if (StringUtils.isNotBlank(str)) {
							flag = true;
							sb.append(str);
						}
					}
					if (!"必须包含".equals(sb.toString())) {
						result.add(sb.toString());
					}
					if (flag) {
						return ResultVO.OK(result);
					} else {
						return ResultVO.OK();
					}
				}
				return ResultVO.OK();
			}
			return ResultVO.FAIL("当前组织不存在");
		}
		return ResultVO.FAIL(Code.FAIL402);
	}

	//更新密码规则
	@RequestMapping("/update")
	public ResultVO update(@RequestParam(required = false) Integer firstChar,
						   @RequestParam(required = false) Integer lenMin,
						   @RequestParam(required = false) Integer lenMax,
						   @RequestParam(required = false) Integer upperStatus,
						   @RequestParam(required = false) Integer lowerStatus,
						   @RequestParam(required = false) Integer numStatus,
						   @RequestParam(required = false) Integer charStatus) {
		//参数校验
		if (lenMin == null || lenMax == null) {
			return ResultVO.FAIL("密码长度不能为空");
		}
		if (lenMin > lenMax) {
			return ResultVO.FAIL("密码长度不正确");
		}
		if (lenMin < 6) {
			return ResultVO.FAIL("密码长度不能少于6位");
		}
		if (lenMax > 20) {
			return ResultVO.FAIL("密码长度不能大于20位");
		}
		UserToken token = getToken();
		Integer userId = token.getUserId();
		User user = userService.get(userId);
		if (user != null) {
			StrategyPassword sp = service.getByOrg(properties.getDefaultOrgId());
			if (sp == null) {
				sp = new StrategyPassword();
				sp.setCreatorname(user.getUserName());
				sp.setCreatorid(user.getId());
				sp.setOrgId(user.getOrgId());
				sp.setCreatedate(new Date());
				service.add(sp);
			}
			sp = service.getByOrg(properties.getDefaultOrgId());
			sp.setLenMax(lenMax);
			sp.setLenMin(lenMin);
			sp.setFirstChar(firstChar);
			if (upperStatus != null && upperStatus == 1) {
				sp.setUpperStatus(1);
			} else {
				sp.setUpperStatus(null);
			}
			if (lowerStatus != null && lowerStatus == 1) {
				sp.setLowerStatus(1);
			} else {
				sp.setLowerStatus(null);
			}
			if (numStatus != null && numStatus == 1) {
				sp.setNumStatus(1);
			} else {
				sp.setNumStatus(null);
			}
			if (charStatus != null && charStatus == 1) {
				sp.setCharStatus(1);
			} else {
				sp.setCharStatus(null);
			}
			service.update(sp);
			return ResultVO.OK("密码规则已更新");
		}
		return ResultVO.FAIL("该用户不存在");
	}
}
