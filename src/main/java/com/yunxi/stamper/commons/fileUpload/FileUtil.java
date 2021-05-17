package com.yunxi.stamper.commons.fileUpload;

import cn.hutool.crypto.digest.DigestUtil;
import com.yunxi.stamper.commons.dir.DirFileUtils;
import com.yunxi.stamper.commons.md5.MD5;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.URLUtil;
import com.yunxi.stamper.sys.error.exception.FileSupportException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhf_10@163.com
 * @Description 文件上传工具
 * @date 2019/4/28 0028 13:29
 */
@Slf4j
public class FileUtil {

	/**
	 * 文件类型白名单
	 */
	private static final String[] FILE_TYPES = {"jpg", "png", "bin", "jpeg", "docx", "doc", "xlsx", "xls", "json", "apk", "pptx", "ppt", "pdf", "zip"};

	/**
	 * 图片类型白名单
	 */
	private static final String[] IMAGE_TYPES = {"jpg", "png", "jpeg"};

	/**
	 * 路径遍历匹配
	 */
	private static final Map<String, String> REG_REP = new HashMap<>(8);

	static {
		REG_REP.put("'([^']*)'", "‘$1’");
		REG_REP.put("`([^`]*)`", "~$1~");
		REG_REP.put("'([^ ]* )", " $1");
		REG_REP.put("(\\.\\.[\\\\/]+)+", "");
		REG_REP.put("'(.*--+)", "‘$1");
		REG_REP.put("\\\"([^\\\"]*)\\\"", "“$1”");
		REG_REP.put("\\(([^\\)]*)\\)", "（$1）");
		REG_REP.put("<([^>]+)>", "&lt;$1&gt;");
	}

	/**
	 * 检查文件名,并返回安全可用的文件名称
	 *
	 * @param fileName 源文件名
	 * @return 安全的文件名
	 */
	private static String checkFileName(String fileName) {
		// 文件参数
		String extension = FilenameUtils.getExtension(fileName);
		fileName = FilenameUtils.getName(fileName);

		if (StringUtils.isBlank(fileName)) {
			throw new FileSupportException("文件名不能为空");
		}

		// 文件类型白名单
		boolean isExtension = FilenameUtils.isExtension(fileName, FILE_TYPES);
		if (!isExtension) {
			throw new FileSupportException("不支持的文件类型:" + extension);
		}

		// 路径遍历
		fileName = fileName.replaceAll("." + extension, "");
		for (String key : REG_REP.keySet()) {
			String nval = fileName;
			fileName = fileName.replaceAll(key, REG_REP.get(key));
			while (!fileName.equals(nval)) {
				nval = fileName;
				fileName = fileName.replaceAll(key, REG_REP.get(key));
			}
		}
		return fileName + "." + extension;
	}

	/**
	 * 检查文件路径,并返回安全可用的文件路径
	 *
	 * @param filePath 源文件路径
	 * @return 安全的文件路径
	 */
	private static String checkFilePath(String filePath) {
		filePath = FilenameUtils.getPath(filePath);

		if (StringUtils.isBlank(filePath)) {
			throw new FileSupportException("文件路径不能为空");
		}

		// 路径遍历
		for (String key : REG_REP.keySet()) {
			String nval = filePath;
			filePath = filePath.replaceAll(key, REG_REP.get(key));
			while (!filePath.equals(nval)) {
				nval = filePath;
				filePath = filePath.replaceAll(key, REG_REP.get(key));
			}
		}

		return filePath;
	}

	/***
	 * 保存文件信息
	 * @param filePath          文件所在路径
	 * @param fileName          文件名称
	 * @param base64FileBytes    文件内容base64字节数组
	 * @return
	 */
	public synchronized static File uploadFile(String filePath, String fileName, byte[] base64FileBytes) throws Exception {
		if (base64FileBytes == null || base64FileBytes.length == 0 || StringUtils.isAnyBlank(filePath, fileName)) {
			return null;
		}

		// 文件名与文件路径安全检查
		fileName = checkFileName(fileName);
		filePath = checkFilePath(filePath);
		// 处理文件路径/文件名
		filePath = DigestUtil.md5Hex(filePath);

		// 创建路径
		File path = new File(filePath);
		if (!path.exists()) {
			path.mkdirs();
		}

		File file = new File(path, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				log.error("出现异常 ", e);
				return null;
			}
		}


		//把文件字节数组写到本地文件里面
		try (FileOutputStream fos = new FileOutputStream(file);) {
			fos.write(base64FileBytes);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}

	/**
	 * 单文件上传
	 *
	 * @param file 文件对象
	 * @return 上传出错则返回null, 此方法不进行图片压缩
	 */
	public synchronized static FileEntity update(MultipartFile file, String orgCode) {
		if (file == null || file.isEmpty()) {
			return null;
		}

		String oldFileName = URLUtil.encodeURIComponent(file.getOriginalFilename());
		// 文件名与文件路径安全检查
		oldFileName = checkFileName(oldFileName);

		FileEntity en = new FileEntity(false);

		//原文件信息
		en.setOriginalName(oldFileName);
		en.setSize(file.getSize());

		// 处理文件路径/文件名
		orgCode = checkFilePath(orgCode);
		// 处理文件路径/文件名
		orgCode = DigestUtil.md5Hex(orgCode);

		//新文件名 如: 时间戳-上传文件.txt
		String newFileName = checkFileName(oldFileName);
		en.setFileName(newFileName);

		//存储路径
		String secondPath = DirFileUtils.getFilePathV2(orgCode);
		en.setRelativePath("/upload" + secondPath + newFileName);
		en.setAbsolutePath(CommonUtils.getProperties().getFile().getFilePath() + secondPath + newFileName);

		//全路径 如:H://dfaf/upload/ahyx/2019/11/2/
		String s = CommonUtils.getProperties().getFile().getFilePath() + secondPath;
		String path = s.replaceAll("/", File.separator);

		//创建本地文件对象
		File filePath = new File(path);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		File absoluteFilePath = null;

		try {
			//创建一个文件对象,用来存放文件
			absoluteFilePath = new File(filePath, newFileName);
			if (!absoluteFilePath.exists()) {
				absoluteFilePath.createNewFile();
			}

			//真正开始上传
			file.transferTo(absoluteFilePath);
		} catch (IOException e) {
			log.error("出现异常 ", e);
			return null;
		}

		boolean isImage = checkImage(absoluteFilePath);
		en.setIsIMG(isImage ? 0 : 1);

		//计算hash值
		try (FileInputStream fileInputStream = new FileInputStream(absoluteFilePath)) {
			String hash = MD5.md5HashCode(fileInputStream);
			en.setHash(hash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return en;
	}

	/**
	 * 检查指定文件是否是图片
	 *
	 * @param localFile
	 * @return true:是图片 false:不是图片
	 */
	private static boolean checkImage(File localFile) {
		if (localFile == null || !localFile.exists()) {
			return false;
		}

		String name = localFile.getName();
		if (StringUtils.isBlank(name) || !name.contains(".")) {
			return false;
		}

		String fileType = name.substring(name.lastIndexOf('.'));
		if (StringUtils.isBlank(fileType)) {
			return false;
		}

		for (String imageType : IMAGE_TYPES) {
			if (fileType.equalsIgnoreCase(imageType)) {
				return true;
			}
		}
		return false;
	}
}
