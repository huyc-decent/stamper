package com.yunxi.stamper.commons.other;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.yunxi.stamper.commons.device.MHPkg;
import com.yunxi.stamper.commons.gk.GQProperties;
import com.yunxi.stamper.entity.Department;
import com.yunxi.stamper.entityVo.OrganizationalTree;
import com.yunxi.stamper.entityVo.ParentCode;
import com.yunxi.stamper.sys.config.ProjectProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/23 0023 14:45
 */
@Slf4j
@Component
public class CommonUtils {

	public static ProjectProperties properties;

	public static GQProperties gqProperties;

	@Autowired
	public void setGqProperties(GQProperties gqProperties) {
		CommonUtils.gqProperties = gqProperties;
	}

	public static String listToString(Collection collection) {
		if (collection == null || collection.isEmpty()) {
			return null;
		}
		String print = Arrays.toString(collection.toArray());
		if (StringUtils.isBlank(print) || "[]".equalsIgnoreCase(print)) {
			return null;
		}
		return print.replace("[", "").replace("]", "");
	}

	public static String objToJson(Object obj) {
		return obj == null ? null : JSONObject.toJSONString(obj);
	}

	/**
	 * 忽略指定属性，将对象解析成json字符串
	 *
	 * @param obj     对解析的对象
	 * @param ignores 要忽略的属性
	 * @return json字符串
	 */
	public static String objJsonWithIgnoreFiled(Object obj, String... ignores) {
		if (obj == null) {
			return null;
		}
		String logJson = null;
		try {
			if (ignores == null || ignores.length == 0) {
				logJson = JSONObject.toJSONString(obj);
			} else {
				SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
				filter.getExcludes().addAll(Arrays.asList(ignores));
				logJson = JSONObject.toJSONString(obj, filter);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				logJson = JSONObject.toJSONString(obj);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		return logJson;
	}

	public static String generatorURL(String host, String filePath) {
		if (StringUtils.isAnyBlank(host, filePath)) {
			return null;
		}
		String url;

		//针对测试服务器
		if ("117.50.31.205".equalsIgnoreCase(host) || "yunxi.qstamper.com".equalsIgnoreCase(host)) {
			url = "https://yunxi.qstamper.com" + filePath;
		}

		//TODO:针对线上从机
		else if ("117.50.34.35".equalsIgnoreCase(host)) {
			url = "https://image2.qstamper.com" + filePath;
		}
		//针对主机
		else if (!"localhost".equals(host) && host.matches(".*?[a-zA-Z]+.*?")) {
			url = "https://" + host + filePath;
		}
		//针对IP
		else {
			url = "http://" + host + ":" + properties.getFile().getPort() + filePath;
		}
		//将所有的反斜杠'\'替换成斜杠'/',防止手机端图片无法展示
		url = url.replace("\\", "/");
		return url;
	}

	@Autowired
	public void setProperties(ProjectProperties properties) {
		CommonUtils.properties = properties;
	}

	public static ProjectProperties getProperties() {
		return properties;
	}

	/**
	 * 快速生成父节点ParenCode字段
	 * 如果父节点ParenCode不存在，生成新的
	 * 如果父节点ParenCode存在，追加
	 *
	 * @return 给子节点ParenCode使用的父ID链表字段
	 */
	public static String generatorParentCode(Department parent) {
		if (parent == null || parent.getId() == null) {
			return null;
		}

		String parentCode = parent.getParentCode();

		ParentCode parentJson = new ParentCode();
		parentJson.setId(parent.getId());
		parentJson.setLevel(parent.getLevel());

		if (StringUtils.isBlank(parentCode)) {
			List<ParentCode> parentCodes = new ArrayList<>();
			parentCodes.add(parentJson);

			parentCode = JSONObject.toJSONString(parentCodes);
		} else {

			List<ParentCode> parentCodes = JSONObject.parseArray(parentCode, ParentCode.class);
			parentCodes.add(parentJson);

			parentCode = JSONObject.toJSONString(parentCodes);

		}
		return parentCode;
	}

	/**
	 * 比较两个包装类的值是否相同
	 */
	public static boolean isEquals(Integer a, Integer b) {
		if (a == null && b != null) {
			return false;
		}
		if (a == null && b == null) {
			return true;
		}
		if (a != null && b == null) {
			return false;
		}
		if (a != null && b != null) {
			if (a == b.intValue()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将字符串以指定分隔符分隔,并转换成Integer集合返回
	 */
	public static List<Integer> splitToInteger(String splitStr, String seprator) {
		if (StringUtils.isNotBlank(splitStr)) {
			String[] str = splitStr.split(seprator);
			List<Integer> res = new ArrayList<>();
			for (String s : str) {
				if (StringUtils.isNotBlank(s)) {
					try {
						res.add(Integer.parseInt(s));
					} catch (Exception e) {
						log.error("出现异常", e);
					}
				}
			}
			return res;
		}
		return null;
	}

	public static String generatorNginxFileURL(String path) {
		if (StringUtils.isNotBlank(path) && !"null".equalsIgnoreCase(path)) {
			return CommonUtils.getProperties().getFile().getDomain() + path + "?1=1";
		}
		return null;
	}

	/**
	 * 从请求头中解析出参数列表
	 */
	public static String getArgsByReq(ProceedingJoinPoint joinPoint) {
		try {
			if (joinPoint != null) {
				Object[] args = joinPoint.getArgs();
				if (args != null && args.length > 0) {
					StringBuilder sb = new StringBuilder();
					for (Object arg : args) {
						if (arg != null) {
							if (arg instanceof String || arg instanceof Integer) {
								sb.append(arg);
							} else if (arg instanceof String[] || arg instanceof Integer[]) {
								Object[] objs = (Object[]) arg;
								sb.append(Arrays.toString(objs));
							} else if (arg instanceof MultipartFile) {
								sb.append(((MultipartFile) arg).getOriginalFilename());
							} else if (arg instanceof MultipartFile[]) {
								MultipartFile[] objs = (MultipartFile[]) arg;
								sb.append("[");
								for (MultipartFile obj : objs) {
									sb.append(obj.getOriginalFilename()).append(",");
								}
								sb.append("]");
							} else {
								sb.append(arg.toString());
							}
							sb.append(",");
						} else {
							sb.append("[],");
						}
					}
					return sb.toString();
				}
			}
		} catch (Exception e) {
			log.error("出现异常", e);
		}
		return null;
	}

	/**
	 * 将集合中的属性转换成通讯录格式{"letter":"A","date":"{},{}"},{"letter":"B","date":"{},{}"}...{"letter":"#","date":"{},{}"}
	 *
	 * @param src      要转换的集合
	 * @param fieldKey 以对象的哪个属性进行转换
	 */
	public static List<Map<String, Object>> getAddressList(List src, String fieldKey) {

		if (StringUtils.isBlank(fieldKey) || src == null || src.size() == 0) {
			return null;
		}

		//先将list集合遍历归类，变成这样:{"A":[{},{}]},{"B":[{},{}]}...{"#":[{},{}]}
		Map<String, LinkedList> tempMap = new HashMap<>(src.size());//临时存储容器
		Map<String, LinkedList> otherMap = new HashMap<>(src.size());//临时"#"的存储容器
		for (Object obj : src) {
			try {
				//获取对象指定属性值
				Class<?> clazz = obj.getClass();
				Field field = ReflectionUtils.findField(clazz, fieldKey);
				field.setAccessible(true);
				String fieldValue = ((String) field.get(obj)).trim();

				//如果这个人的姓名是空的话,就装到key为'#'的value中
				if (StringUtils.isBlank(fieldValue)) {
					LinkedList otherFieldValue = otherMap.get("#");
					if (otherFieldValue == null) {
						otherFieldValue = new LinkedList();
					}
					otherFieldValue.add(obj);
					otherMap.put("#", otherFieldValue);
					continue;
				}

				//获取值开头字母
				String firstStr = fieldValue.substring(0, 1);
				char[] firstName = PinyinUtil.getHeadByChar(firstStr.charAt(0), true);//非汉字原样返回
				String _firstName = firstName[0] + "";

				//是汉字开头
				if (!firstStr.equals(_firstName)) {
					LinkedList pinyinList = tempMap.get(_firstName);
					if (pinyinList == null) {
						pinyinList = new LinkedList();
					}
					pinyinList.add(obj);
					tempMap.put(_firstName, pinyinList);
				} else {     //可能是数字，英文字母，或者其他编码字符开头
					try {
						int ASCII = _firstName.toCharArray()[0];
						if ((ASCII >= 65 && ASCII <= 90) || (ASCII >= 97 && ASCII <= 122)) {//是英文开头
							_firstName = _firstName.toUpperCase();
							LinkedList enFieldValue = tempMap.get(_firstName);
							if (enFieldValue == null) {
								enFieldValue = new LinkedList();
							}
							enFieldValue.add(obj);
							tempMap.put(_firstName, enFieldValue);
						} else {                                        //可能是其他编码格式或者数字开头
							LinkedList otherFieldValue = otherMap.get("#");
							if (otherFieldValue == null) {
								otherFieldValue = new LinkedList();
							}
							otherFieldValue.add(obj);
							otherMap.put("#", otherFieldValue);
						}
					} catch (Exception ex) {                           //出错，将当前数据装到”#“中
						log.error("转换通讯录出现错误-->", ex);
						throw new RuntimeException();
					}
				}
			} catch (Exception e) {//出错，将当前数据装到”#“中
				log.error("出现错误", e);
				LinkedList otherFieldValue = otherMap.get("#");
				if (otherFieldValue == null) {
					otherFieldValue = new LinkedList();
				}
				otherFieldValue.add(obj);
				otherMap.put("#", otherFieldValue);
			}
		}
		//========================================================
		if (tempMap.isEmpty() && otherMap.isEmpty()) {
			return null;
		}

		//========================================================
		//再将处理好的Map变成需要的格式返回{"letter":"A","date":"{},{}"},{"letter":"B","date":"{},{}"}...{"letter":"#","date":"{},{}"}
		List<Map<String, Object>> result = new LinkedList<>();
		for (Map.Entry<String, LinkedList> en : tempMap.entrySet()) {
			String key = en.getKey();
			LinkedList value = en.getValue();
			Map<String, Object> param = new HashMap<>(2);
			param.put("letter", key);
			param.put("data", value);
			result.add(param);
		}
		//如果"#"存储容器中有东西,则最后加进去
		LinkedList linkedList = otherMap.get("#");
		if (linkedList != null && linkedList.size() > 0) {
			Map<String, Object> param = new HashMap<>(2);
			param.put("letter", "#");
			param.put("data", otherMap.get("#"));
			result.add(param);
		}

		return result;
	}


	/**
	 * 递归遍历,过滤指定树中包含的节点，删除不要的节点
	 *
	 * @param tree       要遍历的树
	 * @param includeIds 需要留下的节点列表
	 */
	public static void recursion_includeIds(OrganizationalTree tree, Collection<Integer> includeIds) {
		List<OrganizationalTree> childrens = tree.getChildrens();

		if (childrens != null && childrens.size() > 0) {

			for (int i = 0; i < childrens.size(); i++) {
				OrganizationalTree node = childrens.get(i);
				int id = node.getId();
				if (!includeIds.contains(id)) {
					List<OrganizationalTree> childrensByParent = node.getChildrens();
					if (childrensByParent != null && childrensByParent.size() > 0) {
						childrens.addAll(childrensByParent);
					}
					childrens.remove(i);
					i--;
				} else {
					recursion_includeIds(node, includeIds);
				}
			}
		}
	}


	public static String splitToString(List<Integer> departmentIds, String split) {
		if (departmentIds != null && departmentIds.size() > 0) {
			if (StringUtils.isBlank(split)) {
				split = ",";
			}
			StringBuilder sb = new StringBuilder();
			for (Integer departmentId : departmentIds) {
				sb.append(departmentId).append(split);
			}
			return sb.toString().substring(0, sb.toString().length() - 1);
		}
		return null;
	}

	public static int getCmd(String json) {
		int cmd;
		try {
			JSONObject jsonObject = JSONObject.parseObject(json);
			JSONObject head = jsonObject.getJSONObject("Head");
			cmd = head.getInteger("Cmd");
		} catch (Exception e) {
			log.error("获取协议号CMD出现异常\tjson:{}\terror:{}", json, e.getMessage());
			cmd = parseCmd(json);
		}
		return cmd;
	}

	/**
	 * 从消息体中解析出cmd 协议号
	 *
	 * @param message 消息体
	 * @return cmd
	 */
	private static int parseCmd(String message) {//"cmd":13,

		/*
		  json解析
		 */
		if (StringUtils.isNotBlank(message)) {
			try {
				MHPkg pkg = JSONObject.parseObject(message, MHPkg.class);
				return pkg.getHead().getCmd();
			} catch (Exception e) {
				log.error("消息体解析出错:{}\terror:{}", message, e.getMessage());
			}
		}

		/*
		  手动解析
		 */
		String cmd_str = null;
		String temp_str;
		message = message.replaceAll(" ", "");//处理掉空格
		if (message.contains("\"cmd\"")) {
			temp_str = "\"cmd\"";
		} else {
			temp_str = "\"Cmd\"";
		}
		int cmd_index = message.indexOf(temp_str);//Cmd字符串的索引
		if (cmd_index > 0) {
			String cmd_to_end_str = message.substring(cmd_index);
			int flag_index = cmd_to_end_str.indexOf(':');//最前面的冒号的索引
			int flag2_index = cmd_to_end_str.indexOf(',');//最前面的逗号的索引
			cmd_str = cmd_to_end_str.substring(flag_index + 1, flag2_index);//这个就是cmd的协议号字符串形式
		}
		if (StringUtils.isNotBlank(cmd_str)) {
			return Integer.parseInt(cmd_str);
		}
		return -1;
	}
}
