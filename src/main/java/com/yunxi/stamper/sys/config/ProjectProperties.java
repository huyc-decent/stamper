package com.yunxi.stamper.sys.config;

import com.yunxi.stamper.commons.md5.MD5;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhf_10@163.com
 * @Description 项目文件相关配置
 * @create 2020-5-8 14:27:00
 */
@Data
@Component
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {
	//用户默认密码
	private String defaultPwd;

	public String getDefaultPwd() {
		return MD5.toMD5(defaultPwd);
	}

	//印章初始化注册组织ID
	private int defaultOrgId = -1;

	//报表申请频率,单位：次/分钟
	private int reportMinute = 10;

	//申请单提交频率，单位：次/人/分钟
	private int applicationMinute = 30;

	//是否发送个推消息
	private boolean push = true;

	//是否监听账号日志
	private boolean insertLogger = true;

	//短信分隔正则表达式
	private String smsSplitRegex = "\\$\\{.{4,5}\\}";

	//通知分隔正则表达式
	private String noticeSplitRegex = "【.{2}】";

	//日志相关路径
	private String logPath;

	//文件相关配置参数
	private File file = new File();

	//压缩图片相关参数
	private ReduceFile reduceFile = new ReduceFile();

	//消息队列相关参数
	private RabbitMq RabbitMq = new RabbitMq();

	//系统业务日志配置
	private Logger logger = new Logger();

	//阿里云短信配置
	private AliyunSms aliyunSms = new AliyunSms();

	/**
	 * 阿里云短信配置
	 */
	@Data
	public class AliyunSms {
		private boolean enabled = true;        //是否发送短信 默认:支持
		private boolean reissuedEnabled = true;    //发送失败是否补发  默认:补发
		private int verifyTotal = 10;    //短信验证码发送频率 数量/每个用户/每天
		private String accessKeyId = "LTAIhb8W3FQHxYK1";
		private String accessKeySecret = "q1Hqv2NvdExlsG9uIhuV4PgY23qDZW";
		private String signName = "互联云玺";
	}

	@Data
	public class Logger {
		private String exchange;
		private String queue;
		private boolean record;
	}

	/***rabbitmq相关配置*/
	@Data
	public class RabbitMq {
		private String queueOrder;//印章指令下发_队列
		private String exchangeOrder;//印章指令下发_交换机
		private String queueLogs;//用户操作日志_队列
		private String exchangeLogs;//用户操作日志_交换机
		private String queueDemo;//量子演示界面Websocket消息_队列
		private String exchangeDemo;//量子演示界面Websocket消息_交换机
	}

	/***压缩图相关配置*/
	@Data
	public class ReduceFile {
		private boolean enabled = true;    //是否使用图片压缩 默认:是
		private long maxSize = 51200;    //压缩图片最大容量
		private double scale = 0.5D;    //图片执行压缩比例
		private String prefix = "small_";    //压缩图片名称前缀
	}

	@Data
	public class File {
		private String protocol;//请求协议类型,如：http、https、socket等
		private String host;//文件下载、访问主机IP,如：localhost、127.0.0.1、117.50.31.205等
		private String port;//文件下载、访问主机port,如：9000
		private String filePath;//文件相关路径
		private int minute = 100;//文件上传频率 次/人/分钟
		private long maxSize = 52428800;//上传文件最大大小 50M  1024 * 1024 * 50

		//组装主机请求域名
		public String getDomain() {
			return protocol + "://" + host + ":" + port;
		}
	}
}
