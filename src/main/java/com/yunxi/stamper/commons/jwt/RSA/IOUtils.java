package com.yunxi.stamper.commons.jwt.RSA;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author zhf_10@163.com
 * @Description description: IO 工具类, 读写文件
 * @date 2019/1/23 0023 18:56
 */
public class IOUtils {

	static void writeFile(String data, File file) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(data.getBytes());
			out.flush();
		} finally {
			close(out);
		}
	}

	public static String readFile(File file) throws IOException {
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new FileInputStream(file);
			out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len = -1;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			out.flush();
			byte[] data = out.toByteArray();
			return new String(data, StandardCharsets.UTF_8);
		} finally {
			close(in);
			close(out);
		}
	}

	public static void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				// nothing
			}
		}
	}

}
