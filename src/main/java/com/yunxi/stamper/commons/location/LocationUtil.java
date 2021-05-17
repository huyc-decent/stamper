package com.yunxi.stamper.commons.location;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhf_10@163.com
 * @Description 公共工具类
 * @date 2018/11/24 0024 23:51
 */
@SuppressWarnings("ALL")
public class LocationUtil {
	/**
	 * 解析地址中的省市区
	 *
	 * @param address
	 * @return
	 * @author lin
	 */
	public static Location addressResolution(String address) {
		try {
			String regex = "(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)(?<city>[^市]+自治州|.*?行政单位|.*?地区|.+盟|市辖区|.*?市|.*?新区|.*?县)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇|.*?街道)?(?<village>.*)";
			Matcher m = Pattern.compile(regex).matcher(address);
			String province = null, city = null, county = null, town = null, village = null;
			Location row= new Location();
			while (m.find()) {
				province = m.group("province");
				if (StringUtils.isNotBlank(province)) {
					province = province.replace("中国", "");
				}
				row.setProvince(province == null ? "" : province.trim());
				city = m.group("city");
				row.setCity(city == null ? "" : city.trim());
				county = m.group("county");
				row.setDistrict(county == null ? "" : county.trim());//县区
				town = m.group("town");
				village = m.group("village");
				row.setStreet(village == null ? "" : village.trim());//乡村街道
			}
			return row;
		} catch (Exception e) {
			return null;
		}
	}
}
