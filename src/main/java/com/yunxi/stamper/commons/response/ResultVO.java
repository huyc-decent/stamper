package com.yunxi.stamper.commons.response;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunxi.quantum.utils.sm4.SM4Utils;
import com.yunxi.stamper.commons.other.Global;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.filter.xss.XssUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/23 0023 13:19
 */
@Slf4j
public class ResultVO implements Serializable {
	private Integer code;
	private String msg;
	private Object data;
	//默认未加密
	private Boolean encryption = false;
	/**
	 * 加密票据 使用这个票据加密和解密(前端传递来的,再带回去)
	 */
	private String ticket;

	public ResultVO() {
	}

	public ResultVO(int code, String msg, Object data) {
		this.code = code;
		this.msg = StringUtils.isNotBlank(msg) ? HtmlUtils.htmlEscape(msg, "utf-8") : msg;
		this.data = data != null ? XssUtils.xssObj(data) : null;
		isEncrypt(data);
	}

	public ResultVO(Code entity) {
		this.code = entity.getCode();
		this.msg = StringUtils.isNotBlank(entity.getMsg()) ? HtmlUtils.htmlEscape(entity.getMsg(), "utf-8") : entity.getMsg();
	}

	/**
	 * 检查是否需要加密响应体
	 */
	private void isEncrypt(Object data) {
		try {
			HttpServletRequest request = SpringContextUtils.getRequest();
			if (request != null) {
				if (data == null || StringUtils.isBlank(data.toString())) {
					//data为空时,不需要加密
					return;
				}

				this.ticket = request.getParameter(Global.ticket);
				if (StringUtils.isBlank(this.ticket) || "undefined".equalsIgnoreCase(this.ticket)) {
					//票据为空时不需要加密
					return;
				}

				//k开始加密
				Map<String, String> resMap = enctrpy(data);

				if (resMap != null && resMap.size() > 0) {
					//加密成功
					this.data = resMap.get("encmsg");
					this.encryption = true;
					this.ticket = request.getParameter(Global.sessionkeyid);
				} else {
					//响应成功,但是加密失败,给前台友好提示
					this.data = data;
					this.ticket = null;
					this.msg += "(响应成功,量子加密失败)";
				}
			}
		} catch (Exception e) {
			log.error("出现异常 ", e);
		}
	}

	private Map<String, String> enctrpy(Object data) throws UnsupportedEncodingException {
		try {
			Map<String, String> resMap = new HashMap<>();
			SM4Utils sm4 = new SM4Utils();
			sm4.setSecretKey(DatatypeConverter.parseHexBinary(ticket));
			sm4.setHexString(true);
			byte[] bytes = sm4.encryptData_ECB(JSONObject.toJSONString(data).getBytes("UTF-8"));
			resMap.put("flag", "true");
			resMap.put("encmsg", DatatypeConverter.printHexBinary(bytes));
			return resMap;
		} catch (UnsupportedEncodingException e) {
			log.error("出现异常 ", e);
		}
		return null;
	}

	public static ResultVO OK(Code code, Object data) {
		return new ResultVO(code.getCode(), code.getMsg(), data);
	}

	public static ResultVO OK(Object data) {
		return new ResultVO(Code.OK.getCode(), Code.OK.getMsg(), data);
	}

	public static ResultVO Page(List data, boolean page) {
		if (page) {
			if (data != null && data.size() > 0) {
				PageInfo pageInfo = new PageInfo<>(data);
				return ResultVO.OK(pageInfo);
			}
			return ResultVO.OK();
		} else {
			return ResultVO.OK(data);
		}
	}

	public static ResultVO OK(String msg, Object obj) {
		return new ResultVO(200, msg, obj);
	}

	public static ResultVO OK() {
		return new ResultVO(200, "成功", null);
	}

	public static ResultVO OK(Code code) {
		return new ResultVO(code.getCode(), code.getMsg(), null);
	}

	public static ResultVO OK(String msg) {
		return new ResultVO(Code.OK.getCode(), msg, null);
	}

	public static ResultVO FAIL(Code code) {
		return new ResultVO(code.getCode(), code.getMsg(), null);
	}

	public static ResultVO FAIL(String msg) {
		return new ResultVO(Code.FAIL400.getCode(), msg, null);
	}

	public static ResultVO FAIL(Code code, String msg) {
		return new ResultVO(code.getCode(), msg, null);
	}

	public static ResultVO ERROR(Code code) {
		return new ResultVO(code.getCode(), code.getMsg(), null);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public boolean isEncryption() {
		return encryption;
	}

	public void setEncryption(boolean encryption) {
		this.encryption = encryption;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	@Override
	public String toString() {
		return "ResultVO{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", data=" + data +
				", encryption=" + encryption +
				", ticket='" + ticket + '\'' +
				'}';
	}

}
