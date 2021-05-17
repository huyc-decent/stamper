package com.yunxi.stamper.commons.regex;


import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhf_10@163.com
 * @Description 正则校验工具
 * @date 2018/11/23 0023 23:50
 */
@Slf4j
public class RigexUtil {

    /**
     * 验证手机号码合法性
     * @param mobile
     * @return
     */
    public static boolean isMobileNO(  String mobile){
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String regex = "^[1]([3-9])[0-9]{9}$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(mobile);
        return m.matches();
    }
}
