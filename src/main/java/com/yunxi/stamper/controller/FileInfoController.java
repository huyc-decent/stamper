package com.yunxi.stamper.controller;

import com.yunxi.common.exception.file.CustomFileNotExistException;
import com.yunxi.common.exception.file.CustomFileTooBigException;
import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.fileUpload.FileEntity;
import com.yunxi.stamper.commons.fileUpload.FileUtil;
import com.yunxi.stamper.commons.jwt.UserToken;
import com.yunxi.stamper.commons.md5.MD5;
import com.yunxi.stamper.commons.other.CommonUtils;
import com.yunxi.stamper.commons.other.RedisGlobal;
import com.yunxi.stamper.commons.other.URLUtil;
import com.yunxi.stamper.commons.response.Code;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.FileInfo;
import com.yunxi.stamper.entity.Org;
import com.yunxi.stamper.entity.ReduceFileInfo;
import com.yunxi.stamper.service.FileInfoService;
import com.yunxi.stamper.service.OrgService;
import com.yunxi.stamper.service.ReduceFileInfoService;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/7/19 0019 15:09
 */
@Slf4j
@Api(tags = "文件相关")
@RestController
@RequestMapping(value = "/file/fileInfo", method = {RequestMethod.POST, RequestMethod.GET})
public class FileInfoController extends BaseController {
	@Autowired
	private FileInfoService service;
	@Autowired
	private OrgService orgService;
	@Autowired
	private ReduceFileInfoService reduceFileInfoService;

	@ApiOperation(value = "检查文件大小", notes = "检查文件大小", httpMethod = "GET")
	@ApiImplicitParam(name = "filesize", value = "文件大小", dataType = "long")
	@RequestMapping("/checkFilesize")
	public ResultVO checkFileSize(@RequestParam Long filesize) {
		if (filesize == 0) {
			return ResultVO.FAIL("空文件");
		}
		if (filesize > properties.getFile().getMaxSize()) {
			return ResultVO.FAIL("文件超过指定大小(" + (properties.getFile().getMaxSize() / (1024 * 1024)) + "M)");
		}
		return ResultVO.OK();
	}

	private void checkSize(Long filesize) throws Exception {
		if (filesize == 0) {
			throw new CustomFileNotExistException("文件不能为空");
		}
		if (filesize > properties.getFile().getMaxSize()) {
			throw new CustomFileTooBigException("文件超过指定大小(" + (properties.getFile().getMaxSize() / (1024 * 1024)) + "M)");
		}
	}

	/**
	 * 文件上传接口
	 */
	@RequestMapping("/uploadAndGetFile")
	public ResultVO uploadAndGetFile(MultipartFile file) throws Exception {
		if (file != null) {
			UserToken token = getToken();

			checkSize(file.getSize());

			//每个用户每分钟,最多上传100张
			String key = RedisGlobal.FILE_PREFIX_KEY + token.getUserId();
			Set<String> fileIds = redisUtil.keys(key + "*");
			if (fileIds != null && fileIds.size() > 0) {
				if (fileIds.size() > properties.getFile().getMinute()) {
					return ResultVO.FAIL("上传过于频繁,请稍后重试");
				}
			}

			FileInfo fileInfo = null;
			//查询hash值
			String hash = null;
			try (InputStream inputStream = file.getInputStream()) {
				hash = MD5.md5HashCode(inputStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (StringUtils.isNotBlank(hash)) {
				fileInfo = service.getByHash(hash);
			}

			if (fileInfo == null) {
				/*
				 * 开始上传
				 */
				FileEntity en = FileUtil.update(file, getOrgCode(token.getOrgId()));
				if (en != null) {
					//初始化实体
					fileInfo = new FileInfo();
					fileInfo.setAbsolutePath(en.getAbsolutePath());
					fileInfo.setRelativePath(en.getRelativePath());
					fileInfo.setFileName(en.getFileName());
					fileInfo.setScaling(en.isScaling() ? 0 : 1);
					fileInfo.setStatus(en.getIsIMG());
					fileInfo.setOriginalName(file.getOriginalFilename());
					fileInfo.setSize(en.getSize());
					String remoteHost = SpringContextUtils.getRemoteHost();//上传客户端host
					fileInfo.setUploadHost(remoteHost);
					fileInfo.setHash(en.getHash());
					fileInfo.setHost(properties.getFile().getHost());
					//存储数据库
					service.add(fileInfo);

				}
			}

			if (fileInfo != null) {
				//存缓存服务器60秒
				redisUtil.set(key + ":" + System.currentTimeMillis(), fileInfo.getId(), RedisGlobal.FILE_PREFIX_KEY_TIME_OUT);
				Map<String, Object> res = new HashMap<>();
				res.put("fileId", fileInfo.getId());
				res.put("fileName", file.getOriginalFilename());
				res.put("newFileName", fileInfo.getFileName());
				res.put("url", CommonUtils.generatorURL(fileInfo.getHost(), fileInfo.getRelativePath()));
				//返回文件UUID
				return new ResultVO(200, URLUtil.encodeURIComponent(fileInfo.getOriginalName()), res);
			} else {
				return ResultVO.FAIL("文件上传失败");
			}
		}
		return ResultVO.FAIL("文件不存在");
	}

	/**
	 * 从缓存Redis中查询指定公司ID的组织编码
	 */
	private String getOrgCode(Integer orgId) {
		//查询该设备所属公司信息
		String orgCode = null;
		Org org = orgService.get(orgId);
		if (org != null) {
			orgCode = org.getCode();
		}
		return orgCode;
	}

	/**
	 * 文件上传接口
	 */
	@RequestMapping("/uploadFile")
	public ResultVO uploadFile(MultipartFile file) throws Exception {
		if (file == null) {
			return ResultVO.FAIL("文件不存在");
		}

		UserToken token = getToken();

		checkSize(file.getSize());

		//每个用户每分钟,最多上传100张
		String key = RedisGlobal.FILE_PREFIX_KEY + token.getUserId();
		Set<String> fileIds = redisUtil.keys(key + "*");
		if (fileIds != null && fileIds.size() > 0) {
			if (fileIds.size() > properties.getFile().getMinute()) {
				return ResultVO.FAIL("上传过于频繁,请稍后重试");
			}
		}

		FileInfo fileInfo = null;
		//查询hash值
		String hash = null;
		try (InputStream inputStream = file.getInputStream()) {
			hash = MD5.md5HashCode(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (StringUtils.isNotBlank(hash)) {
			fileInfo = service.getByHash(hash);
		}
		if (fileInfo == null) {
			//真正上传文件逻辑
			FileEntity en = FileUtil.update(file, getOrgCode(token.getOrgId()));

			if (en != null) {
				//初始化实体
				fileInfo = new FileInfo();
				fileInfo.setAbsolutePath(en.getAbsolutePath());
				fileInfo.setRelativePath(en.getRelativePath());
				fileInfo.setFileName(en.getFileName());
				fileInfo.setScaling(en.isScaling() ? 0 : 1);
				fileInfo.setStatus(en.getIsIMG());
				fileInfo.setOriginalName(en.getOriginalName());
				fileInfo.setSize(en.getSize());
				fileInfo.setHash(en.getHash());
				//上传客户端host
				HttpServletRequest request = SpringContextUtils.getRequest();
				if (request == null) {
					throw new RuntimeException();
				}
				String remoteHost = request.getRemoteHost();
				fileInfo.setUploadHost(remoteHost);
				fileInfo.setHost(properties.getFile().getHost());
				fileInfo.setHash(hash);
				//存储数据库
				service.add(fileInfo);
			} else {
				return ResultVO.FAIL("文件上传失败");
			}
		}

		//存缓存服务器60秒
		redisUtil.set(key + ":" + System.currentTimeMillis(), fileInfo.getId(), RedisGlobal.FILE_PREFIX_KEY_TIME_OUT);

		//返回文件UUID
		ResultVO resultVO = new ResultVO(200, URLUtil.encodeURIComponent(fileInfo.getOriginalName()), fileInfo.getId());
		log.info("√\t文件上传成功\tfileInfo:{}", CommonUtils.objToJson(fileInfo));
		return resultVO;
	}

	/**
	 * 查询指定ID的本地路径
	 *
	 * @param fileId 文件ID
	 * @return 结果
	 */
	@RequestMapping("/getPathByFileId")
	public String getPathByFileId(@RequestParam(value = "fileId", required = false) String fileId) {
		if (StringUtils.isNotBlank(fileId)) {
			FileInfo fileInfo = service.get(fileId);
			if (fileInfo != null) {
				return fileInfo.getRelativePath();
			}
		}
		return null;
	}

	@ApiOperation(value = "查询指定文件的访问路径URL", notes = "查询指定文件的访问路径URL", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileId", value = "文件ID", dataType = "String")
	})
	@GetMapping("/getOriginalPictureURLByFileId")
	public ResultVO getOriginalPictureURLByFileId(@RequestParam("fileId") String reduceFileInfoId) {
		getUserInfo();

		String fileInfoId = reduceFileInfoId;

		/*查询缩略图对应的原图信息*/
		ReduceFileInfo reduceFileInfo = reduceFileInfoService.get(fileInfoId);
		if (reduceFileInfo != null) {
			fileInfoId = reduceFileInfo.getFileInfoId();
		}

		FileInfo fileInfo = service.get(fileInfoId);
		if (fileInfo != null) {
			if (StringUtils.isNotBlank(fileInfo.getHost())) {
				return new ResultVO(Code.OK.getCode(), Code.OK.getMsg(), CommonUtils.generatorURL(fileInfo.getHost(), fileInfo.getRelativePath()));
			}

			String relativePath = fileInfo.getRelativePath();
			if (StringUtils.isNotBlank(relativePath)) {
				String url = CommonUtils.generatorNginxFileURL(relativePath);
				return new ResultVO(Code.OK.getCode(), Code.OK.getMsg(), url);
			}
		}
		return ResultVO.FAIL("文件不存在");
	}
}
