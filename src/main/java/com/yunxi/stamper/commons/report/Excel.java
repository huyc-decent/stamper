package com.yunxi.stamper.commons.report;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/4/8 0008 15:28
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Excel {
	String value() default "";
	String type() default "String";//写入到Excel中的方式  img:图片写入  String:字符串写入
}
