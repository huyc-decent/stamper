package com.yunxi.stamper.controller;

import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entityVo.UserInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/7/27 8:56
 */
@Slf4j
@Api(tags = "v2系统日志相关")
@RestController
@RequestMapping("/log")
public class TsLogController extends BaseController {
	/**
	 * 系统运行日志
	 *
	 * @param start 开始时间
	 * @param end   结束时间
	 * @return 结果
	 */
	@GetMapping("/logFiles")
	public ResultVO logFiles(@RequestParam Long start, @RequestParam Long end) throws ParseException {
		UserInfo userInfo = getUserInfo();
		Integer type = userInfo.getType();
		if (type != 0) {
			return ResultVO.FAIL("无权限");
		}

		File file = new File("logs");

		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return ResultVO.OK();
		}

		LinkedList<String> logFiles = new LinkedList<>();
		SimpleDateFormat shortSDF = new SimpleDateFormat("yyyy-MM-dd");
		for (File logFile : files) {
			/*过滤条件*/
			String updateTime = getUpdateTime(logFile);
			Date updateDate = shortSDF.parse(updateTime);
			long times = updateDate.getTime();
			if (times >= start && times <= end) {
				String fileName = logFile.getName();
				logFiles.addLast(fileName);
			}
		}
		return ResultVO.OK(logFiles);
	}


	/**
	 * 下载日志文件
	 */
	@GetMapping("/getLogFile")
	public void getLogFile(@RequestParam String fileName, HttpServletResponse response) {

		UserInfo userInfo = getUserInfo();
		Integer type = userInfo.getType();
		if (type != 0) {
			throw new RuntimeException("无权限");
		}

		File file = new File("logs");

		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}

		for (File logFile : files) {
			String name = logFile.getName();
			if (fileName.equals(name)) {
				byte[] buffer = new byte[1024];
				try (FileInputStream fis = new FileInputStream(logFile);
					 BufferedInputStream bis = new BufferedInputStream(fis)) {

					response.addHeader("Content-Disposition", "attachment;fileName=" + name);
					response.setContentType("multipart/form-data");
					response.setCharacterEncoding("UTF-8");
					OutputStream os = response.getOutputStream();
					int i = bis.read(buffer);
					while (i != -1) {
						os.write(buffer, 0, i);
						i = bis.read(buffer);
					}

				} catch (Exception e) {
					log.error("出现异常\tfileName:{}", fileName, e);
				}
			}
		}
	}

	private String getUpdateTime(File file) {
		if (file == null) {
			return null;
		}

		BasicFileAttributes attr = null;
		try {
			Path path = file.toPath();
			attr = Files.readAttributes(path, BasicFileAttributes.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (attr == null) {
			throw new RuntimeException();
		}
		// 更新时间
		Instant instant = attr.lastModifiedTime().toInstant();
		// 上次访问时间
		return DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(instant);
	}
}
