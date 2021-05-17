package com.yunxi.stamper.sys.aop.annotaion;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WebLogger {

    String value() default "";

    int key() default -1;//需要获取方法的参数索引,仅>=0时有效
}
