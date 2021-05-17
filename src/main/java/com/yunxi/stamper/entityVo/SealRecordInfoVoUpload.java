package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.commons.other.DateUtil;
import com.yunxi.stamper.entity.SealRecordInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/17 0017 20:06
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class SealRecordInfoVoUpload extends SealRecordInfo {
	private Integer deviceID;//当前设备id
	private String uuid;//当前设备uuid
	private String identity;//用印人名称
	private Integer picUseId;//用印人id
	private Integer count;//当前使用记录对应的次数
	private Integer applicationID;//申请单的id
	private Integer isAudit;//0:该条记录是盖章上传创建的  1:该条记录是审计上传创建的'
	private String time;//真实盖章时间
	private Integer alarm;//0:正常 1:超次 2:防拆 3:超时  4:密码
	private String fileName;//上传文件名称
	private String fileupload;//图片base64的密文字符串

	//文件信息
	private Integer encryptionType;//加密类型 0:非加密文件 1:对称加密文件 2:非对称加密文件 3:量子加密文件

	//消息队列-使用记录类型 1:超次 2:防拆 3:超时  4:审计记录 5:指纹记录  6:申请单记录
	private int mqInfoType;

	private int deviceMode;//使用模式:1:申请单模式	2:指纹模式	3:锁定模式	4:装章模式	5:密码模式	6:OTA模式	7:休眠模式	8:产测模式	9:静默模式(摄像头关闭)

	/**
	 * 根据已有参数，解析表StamperPicture类型值
	 *
	 * @return 图片类型 0:使用记录图片 1:审计图片 2:超出申请单次数图片 3:长按报警图片 4:拆卸警告图片
	 */
	public int getPictureType() {
		if (alarm == null) {
			return 0;
		}
		if (alarm == 1) {
			return 2;
		} else if (alarm == 2) {
			return 4;
		} else if (alarm == 3) {
			return alarm;
		}

		if (isAudit == 1) {
			return 1;
		} else {
			return 0;
		}
	}

	public void setDeviceID(Integer deviceID) {
		this.deviceID = deviceID;
		super.setDeviceId(this.deviceID);
	}

	public void setIdentity(String identity) {
		//BUG:防止设备上传记录时，用印人名称过长导致记录无法入库的问题
		if (StringUtils.isNotBlank(identity) && identity.length() > 25) {
			identity = identity.substring(0, 25);
		}
		this.identity = identity;
		super.setUserName(this.identity);
	}

	public void setCount(Integer count) {
		this.count = count;
		super.setUseCount(this.count);
	}

	public void setApplicationID(Integer applicationID) {
		this.applicationID = applicationID;
		super.setApplicationId(this.applicationID);
	}

	@Override
	public void setIsAudit(Integer isAudit) {
		this.isAudit = isAudit;
		super.setIsAudit(this.isAudit);
	}

	public void setTime(String time) {
		this.time = time;
		if (StringUtils.isNotBlank(time)) {
			/*将实际戳转换为时间*/
			try {
				Date date = new Date(Long.parseLong(time));
				String format = DateUtil.format(date);

				//先做一下年份对比
				LocalDate now = LocalDate.now();//GMT
				int year = now.getYear();
				if (format.startsWith(year + "")) {
					//时间正确
					super.setRealTime(date);
					return;
				}
			} catch (Exception e) {
				log.error("出现异常\ttime:{}", time, e);
			}
		}

		//时间不正确,以当前时间为准
		super.setRealTime(new Date());
	}

	@Override
	public String toString() {
		String sb = "SealRecordInfoVoUpload{" + "deviceID=" + deviceID +
				", uuid='" + uuid + '\'' +
				", identity='" + identity + '\'' +
				", picUseId=" + picUseId +
				", count=" + count +
				", applicationID=" + applicationID +
				", isAudit=" + isAudit +
				", time='" + time + '\'' +
				", alarm=" + alarm +
				", fileName='" + fileName + '\'' +
				", fileUpload='" + (StringUtils.isBlank(fileupload) ? 0 : fileupload.length()) + '\'' +
				", encryptionType=" + encryptionType +
				", mqInfoType=" + mqInfoType +
				", deviceMode=" + deviceMode +
				'}';
		return sb + "\t" + super.toString();
	}
}
