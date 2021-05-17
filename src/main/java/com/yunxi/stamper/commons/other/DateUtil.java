package com.yunxi.stamper.commons.other;


import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/19 0019 10:33
 */
@Slf4j
public class DateUtil {
	private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);

	private static final String FORMAT_SHORT = "yyyy-MM-dd";
	private static final SimpleDateFormat shortSDF = new SimpleDateFormat(FORMAT_SHORT);

	/**
	 * 获取当天指定时间戳
	 *
	 * @param hour   小时
	 * @param mimute 分钟
	 * @param second 秒
	 * @return 1597075199877-->2020-08-10 23:59:59
	 */
	public static synchronized long getTime(int hour, int mimute, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, mimute);
		calendar.set(Calendar.SECOND, second);
		long time = calendar.getTime().getTime();
		return time;
	}

	/**
	 * 返回在指定时间(date)前几分钟(minute)的日期
	 */
	public static synchronized Date preTime(Date date, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - minute);
		return calendar.getTime();
	}

	/**
	 * 返回在指定时间(date)后几分钟(minute)的日期
	 */
	public static synchronized Date nextTime(Date date, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + minute);
		return calendar.getTime();
	}

	/**
	 * 获取指定时间Date对象的字符串表现形式
	 *
	 * @param date
	 * @return 2019-11-12 9:00:45
	 */
	public static synchronized String format(Date date) {
		if (date != null) {
			return sdf.format(date);
		}
		return null;
	}


	public static synchronized String distanceOfTimeInWords(Date fromTime, Date toTime) {
		return distanceOfTimeInWords(fromTime, toTime, "MM-dd HH:mm", 1);
	}

	public static synchronized String distanceOfTimeInWords(Date fromTime) {
		return distanceOfTimeInWords(fromTime, new Date());
	}

	/**
	 * 截止时间时间到起始时间间隔的时间描述
	 *
	 * @param fromTime 起始时间
	 * @param toTime   截止时间
	 * @param format   格式化
	 * @param days     超过此天数，将按format格式化显示实际时间
	 * @return
	 */
	public static synchronized String distanceOfTimeInWords(Date fromTime, Date toTime, String format, int days) {
		long distanceInMinutes = (toTime.getTime() - fromTime.getTime()) / 60000;
		String message = "";
		if (distanceInMinutes == 0) {
			message = "几秒钟前";
		} else if (distanceInMinutes >= 1 && distanceInMinutes < 60) {
			message = distanceInMinutes + "分钟前";
		} else if (distanceInMinutes >= 60 && distanceInMinutes < 1400) {
			message = (distanceInMinutes / 60) + "小时前";
		} else if (distanceInMinutes >= 1440 && distanceInMinutes <= (1440 * days)) {
			message = (distanceInMinutes / 1440) + "天前";
		} else {
			message = new SimpleDateFormat(format).format(fromTime);
		}
		return message;
	}
}
