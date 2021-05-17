package com.yunxi.stamper;


import com.spring4all.swagger.EnableSwagger2Doc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import tk.mybatis.spring.annotation.MapperScan;

@Slf4j
@MapperScan(basePackages = "com.yunxi.stamper.mapper")
@ComponentScan(basePackages = {"com.yunxi.push","com.yunxi.stamper"})
@SpringBootApplication
@EnableAsync
@EnableSwagger2Doc
public class StamperApp {

	public static void main(String[] args) {
		SpringApplication.run(StamperApp.class, args);
	}
}
