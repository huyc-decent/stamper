package com.yunxi.stamper.service.async;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunxi.stamper.commons.other.DateUtil;
import com.yunxi.stamper.commons.report.Excel;
import com.yunxi.stamper.commons.report.POIEntity;
import com.yunxi.stamper.entity.Application;
import com.yunxi.stamper.entity.ErrorType;
import com.yunxi.stamper.entity.Report;
import com.yunxi.stamper.entityVo.InfoEntity;
import com.yunxi.stamper.entityVo.SealRecordInfoVoSearch;
import com.yunxi.stamper.entityVo.UserInfo;
import com.yunxi.stamper.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/25 0025 16:54
 */
@Slf4j
@Service
public class IReportAsyncService implements ReportAsyncService {
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private ReportService reportService;
	@Autowired
	private ErrorTypeService errorTypeService;
	@Autowired
	private SealRecordInfoService sealRecordInfoService;

	@Async
	@Override
	public void downloadReport(SealRecordInfoVoSearch search, UserInfo info, Report report) {
		try {
			log.info("报表任务 发布人：" + info.getUserName());
			int pageNum = 1;
			/*
			 * 容器1:查询每页使用记录列表
			 */
			List<InfoEntity> infoEntities = new LinkedList<>();

			/*
			 * 容器2:查询总的申请单列表
			 */
			Map<Integer, Application> applications = new HashMap<>();

			int total = 0;
			do {
				/*
				 * 每次查询一遍后并追加完成后,清空容器1
				 */
				if (infoEntities != null) {
					infoEntities.clear();
				}

				//添加分页条件
				search.setPageNum(pageNum);
				search.setPageSize(1000);
				search.setPage(true);

				infoEntities = sealRecordInfoService.searchInfoListByKeyword(info.getOrgId(), info.isOwner() ? null : info.getId(), search.getStart(), search.getEnd(), search.getSignetName(), search.getTitle(), search.getError(), search.getType(), search.getDeviceType());
				if (infoEntities != null && infoEntities.size() > 0) {
					//查询使用记录对应的申请单列表,加入申请单列表容器中
					addApplicationToPool(infoEntities, applications);

					//对使用记录数据进行处理
					List<POIEntity> poiEntities = transInfos(infoEntities, applications);

					//追加到报表中
					appendToFile(poiEntities, report);

					//结束查询
					PageInfo<InfoEntity> pageInfo = new PageInfo<>(infoEntities);
					int pages = pageInfo.getPages();
					if (pages <= pageNum) {
						break;
					}
				}

				pageNum++;
				PageHelper.clearPage();

				//停500毫秒
				total = total + applications.size();
				log.info("报表任务\t发布人：" + info.getUserName() + "\t已导出：" + total);
				Thread.sleep(1000);

			} while (infoEntities != null && infoEntities.size() > 0);

			//修改报表记录状态
			report.setStatus(1);
		} catch (Exception e) {
			log.error("出现异常 ", e);
			report.setStatus(3);
			report.setError(e.getMessage());
		} finally {
			reportService.update(report);
		}
	}

	/*
	 * 将容器中的数据追加的报表中
	 */
	private void appendToFile(List<POIEntity> poiEntities, Report report) throws Exception {
		if (poiEntities != null
				&& poiEntities.size() > 0
				&& report != null
				&& StringUtils.isNotBlank(report.getAbsolutePath())) {

			FileInputStream in = null;
			FileOutputStream out = null;
			try {
				// 拿到Excel文件对象
				in = new FileInputStream(report.getAbsolutePath());  //获取本地XLs文件流
				XSSFWorkbook Workbook = new XSSFWorkbook(in);//得到文档对象
				XSSFSheet sheet = Workbook.getSheet("num1");  //根据name获取sheet表

				//填充数据
				appendDate(poiEntities, sheet);

				// 输出Excel文件
				out = new FileOutputStream(report.getAbsolutePath());  //向中写数据
				out.flush();
				Workbook.write(out);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				//关闭资源
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/*
	 * 向sheet也填充数据
	 */
	private void appendDate(List<POIEntity> poiEntities, XSSFSheet sheet) throws Exception {
		//最后一行
		int lastRowNum = sheet.getLastRowNum();

		//从下一行开始插入
		int startRowNum = lastRowNum + 1;

		//获取对象属性
		for (int i = 0; i < poiEntities.size(); i++) {
			POIEntity entity = poiEntities.get(i);
			XSSFRow row = sheet.createRow(i + startRowNum);

			//获取对象属性
			Class<? extends POIEntity> clazz = entity.getClass();
			Field[] declaredFields = clazz.getDeclaredFields();

			//该变量记录插入的单元格索引
			int cellIndex = 0;

			for (Field field : declaredFields) {
				field.setAccessible(true);
				Excel annotation = field.getAnnotation(Excel.class);
				if (annotation != null) {
					//获取添加了Excel注解的属性值
					Object fieldValue = field.get(entity);
					XSSFCell cell = row.createCell(cellIndex);

					//获得单元格样式
					cell.setCellValue(fieldValue == null ? "" : fieldValue.toString());
					++cellIndex;
				}
			}

			row.setHeightInPoints(24f);//设置行高 单位:像素
		}
	}

	/*
	 * 将容器中的使用记录对应的申请单添加到容器中
	 */
	private void addApplicationToPool(List<InfoEntity> res, Map<Integer, Application> applications) {
		if (res != null && res.size() > 0) {
			for (InfoEntity info : res) {
				Integer applicationId = info.getApplicationId();
				if (applicationId != null && !applications.containsKey(applicationId)) {
					//查询该申请单,并装入map容器中
					Application application = applicationService.get(applicationId);
					applications.put(applicationId, application);
				}
			}
		}
	}

	/*
	 * 将使用记录转换成报表需要的数据
	 */
	private List<POIEntity> transInfos(List<InfoEntity> res, Map<Integer, Application> applications) {
		if (res != null && res.size() > 0) {
			List<POIEntity> poiEntities = new LinkedList<>();
			for (InfoEntity info : res) {
				POIEntity en = new POIEntity();

				/*
				 * 申请单相关
				 */
				Integer applicationId = info.getApplicationId();
				Application application = applications.get(applicationId);
				if (application != null) {
					en.setApplicationId(applicationId);
					en.setTitle(application.getTitle());
					en.setContent(application.getContent());
					en.setUserCount(application.getUserCount());
					en.setUserName(application.getUserName());
				}

				/*
				 * 印章相关
				 */
				en.setDeviceId(info.getDeviceId());
				en.setDeviceName(info.getDeviceName());

				/*
				 * 使用记录相关
				 */
				Integer error = info.getError();
				if (error != null && (error == -1 || error == 1)) {
					//查询异常信息
					List<ErrorType> errorType = errorTypeService.getBySealRecordInfo(info.getId());
					if (errorType != null && errorType.size() > 0) {
						StringBuilder errorStr = new StringBuilder();
						int size = errorType.size();
						for (int j = 0; j < size; j++) {
							ErrorType et = errorType.get(j);
							errorStr.append(et.getName());
							if (j != size - 1) {
								errorStr.append(",");
							}
						}
						en.setError(errorStr.toString());
					}
				} else {
					en.setError("正常");
				}

				Integer type = info.getType();
				en.setType(type != null && type == 0 ? "申请单模式" : "指纹模式");
				en.setCount(info.getUseCount());
				en.setRealTime(DateUtil.format(info.getRealTime()));
				en.setIdentity(info.getUserName());
				en.setLocation(info.getLocation());

				poiEntities.add(en);
			}

			return poiEntities;
		}
		return null;
	}

}
