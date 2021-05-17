package com.yunxi.stamper.commons.other;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/7/6 11:18
 */
@Slf4j
public class WordUtil {

	public static boolean isWord2007(String originalFilename) {
		int lastIndex = originalFilename.lastIndexOf('.');
		//获取文件的后缀名 .jpg
		String suffix = originalFilename.substring(lastIndex);
		return ".docx".equals(suffix);
	}

	/**
	 * 添加页眉信息
	 *
	 * @param wordFile  word文件
	 * @param headerStr 页眉字符串
	 */
	public static void addHeader(File wordFile, String headerStr) throws Exception {
		if (wordFile == null || !wordFile.exists() || StringUtils.isBlank(headerStr)) {
			return;
		}

		try (FileInputStream fis = new FileInputStream(wordFile);
			 OutputStream os = new FileOutputStream(wordFile.getAbsolutePath())) {
			XWPFDocument docx = new XWPFDocument(fis);//文档对象

			CTP ctp = CTP.Factory.newInstance();
			ctp.addNewR().addNewT().setStringValue(headerStr);//设置页眉参数
			ctp.addNewR().addNewT().setSpace(SpaceAttribute.Space.PRESERVE);

			CTSectPr sectPr = docx.getDocument().getBody().isSetSectPr() ? docx.getDocument().getBody().getSectPr() : docx.getDocument().getBody().addNewSectPr();

			XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(docx, sectPr);
			XWPFParagraph paragraph = new XWPFParagraph(ctp, docx);//段落对象
			XWPFHeader header = policy.createHeader(STHdrFtr.DEFAULT, new XWPFParagraph[]{paragraph});
			header.setXWPFDocument(docx);

			docx.write(os);//输出到本地
		} catch (Exception e) {
			throw e;
		}
	}

	public static List<String> getHeader(File wordFile) throws IOException {
		try (FileInputStream fis = new FileInputStream(wordFile)) {
			XWPFDocument docx = new XWPFDocument(fis);

			List<String> headers = new ArrayList<>();
			List<XWPFHeader> headerList = docx.getHeaderList();
			for (XWPFHeader xwpfHeader : headerList) {
				headers.add(xwpfHeader.getText());
			}
			return headers;
		} catch (Exception e) {
			throw e;
		}
	}
}
