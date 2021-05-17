package com.yunxi.stamper.controller;

import com.yunxi.stamper.base.BaseController;
import com.yunxi.stamper.commons.response.ResultVO;
import com.yunxi.stamper.entity.AppRotationImg;
import com.yunxi.stamper.entity.FileInfo;
import com.yunxi.stamper.entityVo.FileEntity;
import com.yunxi.stamper.service.AppRotationImgService;
import com.yunxi.stamper.service.FileInfoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description app轮播图控制层
 * @date 2019/5/28 0028 12:37
 */
@Slf4j
@Api(tags = "APP轮播图相关")
@RestController
@RequestMapping("/auth/appRotationImg")
public class AppRotationImgController extends BaseController {

	@Autowired
	private AppRotationImgService service;
	@Autowired
	private FileInfoService fileInfoService;

	/**
	 * 录播图列表
	 * @return 结果json
	 */
	@RequestMapping("/getList")
	public ResultVO getList() {
		boolean page = setPage();
		List<AppRotationImg> imgs = service.getList();
		return ResultVO.Page(imgs, page);
	}

	/**
	 * 录播图列表
	 * @return 结果json
	 */
	@RequestMapping("/getAll")
	public ResultVO getAll() {
		List<AppRotationImg> imgs = service.getList();
		if (imgs == null || imgs.isEmpty()) {
			return ResultVO.OK();
		}

		for (AppRotationImg img : imgs) {
			String fileId = img.getImgUrl();

			FileEntity fileEntity = fileInfoService.getReduceImgURLByFileId(fileId);
			if (fileEntity == null) {
				continue;
			}
			img.setImgUrl(fileEntity.getFileUrl() + "?1=1");
		}

		return ResultVO.OK(imgs);
	}

	/**
	 * 删除轮播图
	 * @param id 轮播图id
	 * @return 结果json
	 */
	@RequestMapping("/del")
	public ResultVO del(@RequestParam("id") Integer id) {
		AppRotationImg img = service.get(id);
		if (img == null) {
			return ResultVO.FAIL("提交参数有误");
		}
		service.del(img);
		return ResultVO.OK();
	}

	/**
	 * 添加轮播图
	 * @param uuid 图片ID
	 * @param orderNo 轮播图序号
	 * @return 结果json
	 */
	@RequestMapping("/add")
	public ResultVO add(@RequestParam("uuid") String uuid,
						@RequestParam("orderNo") Integer orderNo) {
		FileInfo fileInfo = fileInfoService.get(uuid);
		if (fileInfo == null) {
			return ResultVO.FAIL("轮播图不存在");
		}

		AppRotationImg img = service.getByOrderNo(orderNo);
		if (img != null) {
			return ResultVO.FAIL("轮播图已存在");
		}

		img = new AppRotationImg();
		img.setImgUrl(uuid);
		img.setOrderNo(orderNo);
		service.add(img);
		return ResultVO.OK();
	}
}
