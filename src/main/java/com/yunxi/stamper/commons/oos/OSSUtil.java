//package com.yunxi.stamper.commons.oos;
//
//import com.alibaba.fastjson.JSONObject;
//import com.amazonaws.ClientConfiguration;
//import com.amazonaws.Protocol;
//import com.amazonaws.SDKGlobalConfiguration;
//import com.amazonaws.auth.PropertiesCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.S3ClientOptions;
//import com.amazonaws.services.s3.model.*;
//import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
//import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
//import com.yunxi.stamper.commons.other.DateUtil;
//import com.yunxi.stamper.commons.FileGlobal;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.util.FileCopyUtils;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.URL;
//import java.util.Date;
//
///**
// * @author zhf_10@163.com
// * @Description 天翼云控制层测试
// * @date 2019/9/24 0024 10:15
// */
//@Slf4j
//public class OSSUtil {
//
//	public static final String OOS_ENDPOINT = "oos-cn.ctyunapi.cn";//支持http请求
//	public static final String BUCKET_NAME = "yunxi.stamper.001";//容器名称
//
//	private static AmazonS3 ossClient;
//
//	static {
//		ossClient = getAmazonS3();
//	}
//
//	private static void getAWSSecurityTokenServiceClient(){
//
//		AWSSecurityTokenServiceClient stsClient = getAWSClient();
//		stsClient.setEndpoint(OOS_ENDPOINT);
//
//		GetSessionTokenResult sessionToken = stsClient.getSessionToken();
//		System.out.println(JSONObject.toJSONString(sessionToken));
//
////
////
////		GetSessionTokenRequest session_token_request = new GetSessionTokenRequest();
////		session_token_request.setDurationSeconds(7200); // optional.
////
////		GetSessionTokenResult session_token_result = stsClient.getSessionToken(session_token_request);
////
////		Credentials session_creds = session_token_result.getCredentials();
////
////		System.out.println(JSONObject.toJSONString(session_creds));
//	}
//
//	public static void main(String[] args) throws IOException {
//		getAWSSecurityTokenServiceClient();
//		//上传文件
////		File file = new File("D:\\image.jpg");
////		uploadFile("2019/10/10/", file);
//
////		下载文件
////		getObject("ceshi.png");
////
////		//生成共享连接
////		long start = System.currentTimeMillis();
////		String url = generatePresignedUrl("2019/10/新建文本文档2.txt");
////		System.out.println("共享链接:" + url);
//
//		//获取文件元数据
////		headObject("2010/10/");
//
//		//获取容器中所有对象信息
////		int i = 0;
////		while (i<=9) {
////			listObjects();
////			i++;
////		}
//		//设置跨域规则
////		putBucketCors();
////
////		//查询跨域规则
////		getBucketCors();
//	}
//
//	public static void getBucketCors() {
//		BucketCrossOriginConfiguration cors = ossClient.getBucketCrossOriginConfiguration(BUCKET_NAME);
//		System.out.println(JSONObject.toJSONString(cors));
//	}
//
//	public static void del(String fileName) {
//		ossClient.deleteObject(BUCKET_NAME, fileName);
//	}
//
//	/**
//	 * 设置容器跨域请求
//	 */
//	public static void putBucketCors() {
//		BucketCrossOriginConfiguration bucketCrossOriginConfiguration = new BucketCrossOriginConfiguration();
//
//		//允许的请求方法
////		List<CORSRule.AllowedMethods> allowedMethods = new ArrayList<CORSRule.AllowedMethods>();
////		allowedMethods.add(CORSRule.AllowedMethods.GET);
////		allowedMethods.add(CORSRule.AllowedMethods.POST);
//
//		//允许的源路径
////		List<String> allowedOrigins = new ArrayList();
////		allowedOrigins.add("http://localhost:8081");
//
//		//暴露的请求头
////		List<String> exposedHeaders = new ArrayList();
////		exposedHeaders.add("*");
//
//		//允许的请求头
////		List<String> allowedHeaders = new ArrayList();
////		allowedHeaders.add("*");
//
//		//跨域规则
//		CORSRule rule = new CORSRule();
//		rule.setId("myfirstRuleId");
////		rule.setAllowedMethods(allowedMethods);
////		rule.setAllowedOrigins(allowedOrigins);
////		rule.setExposedHeaders(exposedHeaders);
////		rule.setAllowedHeaders(allowedHeaders);
//		rule.setMaxAgeSeconds(1000);
//		rule.setAllowedHeaders("*");
//		rule.setAllowedMethods(CORSRule.AllowedMethods.values());
//		rule.setAllowedOrigins("*");
//		rule.setExposedHeaders("ETAG");
//
//		//跨域列表
//		List<CORSRule> rules = new ArrayList<CORSRule>();
//		rules.add(rule);
//		bucketCrossOriginConfiguration.setRules(rules);
//
//		ossClient.setBucketCrossOriginConfiguration(BUCKET_NAME, bucketCrossOriginConfiguration);
//
//	}
//
//	/**
//	 * 查询天翼云空间以prefix作为前缀的对象列表元数据
//	 *
//	 * @param prefix
//	 */
//	public static double listObjects(String prefix) {
//		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
//		listObjectsRequest.setBucketName(BUCKET_NAME);
//		listObjectsRequest.setMaxKeys(100);
//		listObjectsRequest.setPrefix(prefix);
//		ObjectListing list = ossClient.listObjects(listObjectsRequest);
//		List<S3ObjectSummary> objectSummaries = list.getObjectSummaries();
//
//		long size = 0;//单位B
//		if (objectSummaries != null && objectSummaries.size() > 0) {
//			for (int i = 0; i < objectSummaries.size(); i++) {
//				S3ObjectSummary summary = objectSummaries.get(i);
//				long len = summary.getSize();
//				size += len;
//			}
//		}
//
//		System.out.println(prefix + "容量:" + String.format("%.2f", size / 1024.0) + " KB");
//
//		return size / 1024.0 / 1024.0;
//	}
//
//	//下载指定对象
//	public static void getObject(String fileName) throws IOException {
//		S3Object object = ossClient.getObject(BUCKET_NAME, fileName);
//		S3ObjectInputStream objectContent = object.getObjectContent();
//		FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\test123.png"));
//		FileCopyUtils.copy(objectContent, fileOutputStream);
//	}
//
//	/**
//	 * Head操作用于获取对象的元数据信息，而不返回数据本身。当只希望获取对象的属性信息时，可以使用此操作。
//	 */
//	public static void headObject(String fileName) {
//		ObjectMetadata metadata = ossClient.getObjectMetadata(BUCKET_NAME, fileName);
//		System.out.println(JSONObject.toJSONString(metadata));
//	}
//
//	/**
//	 * 对于私有或只读Bucket，可以通过生成 Object 的共享链接的方式，将 Object 分享给 其他人，同时可以在链接中设置限速以对下载速度进行控制
//	 * 共享连接最大7天
//	 */
//	public static String generatePresignedUrl(String fileName) {
//		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_NAME, fileName);
//		//2019/03/10
//		Date afterSevenDays = DateUtil.getTomorrow(new Date(), 7);
//		request.setExpiration(afterSevenDays);
//		URL url = ossClient.generatePresignedUrl(request);
//		return JSONObject.toJSONString(url);
//	}
//
//	/**
//	 * 文件上传
//	 *
//	 * @param file
//	 */
//	public static void uploadFile(String preFilePath, File file) throws IOException {
//		if (file == null) {
//			log.info("上传失败，文件不存在");
//			return;
//		}
//		if (!file.exists()) {
//			log.info("上传错误，文件不存在");
//			return;
//		}
//		if (StringUtils.isBlank(file.getName())) {
//			log.info("上传取消，文件名不能为空");
//			return;
//		}
//		putObject(preFilePath, file);
//	}
//
//	/**
//	 * Put 操作用来向指定bucket 中添加一个对象，要求发送请求者对该bucket 有写权限，用户必须添加完整的对象。
//	 */
//	private static void putObject(String preFilePath, File file) throws IOException {
//		PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, preFilePath + file.getName(), file);
//		PutObjectResult putObjectResult = ossClient.putObject(request);
//		//计算文件MD5
////		String md5Hex = DigestUtils.md5Hex(new FileInputStream(file));
////		System.out.println("文件MD5：" + md5Hex);
//		System.out.println("putObjectResult:" + JSONObject.toJSONString(putObjectResult));
//	}
//
//
//	// 创建 AmazonS3Client对象
//	private static AmazonS3 getAmazonS3() {
//		ClientConfiguration clientConfig = new ClientConfiguration();
//		clientConfig.setConnectionTimeout(30 * 1000); //设置连接的超时时间，单位毫秒
//		clientConfig.setSocketTimeout(30 * 1000); //设置socket超时时间，单位毫秒
//		clientConfig.setProtocol(Protocol.HTTP); //设置http
//
//		//使用V4签名
//		System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");
//		 /* 创建client,其中OOSCredentials.properties中存放着用户名和密码*/
//		//负载是否参与签名、本例设置不参与
//		S3ClientOptions options = new S3ClientOptions();
//		options.setPayloadSigningEnabled(false);
//		AmazonS3 ossClient = null;
//		try {
////			ossClient = new AmazonS3Client(new PropertiesCredentials(new File(FileGlobal.OOS_FILE_PATH)), clientConfig);
//			ossClient = new AmazonS3Client(new PropertiesCredentials(new File(FileGlobal.path + File.separator + "host.properties")), clientConfig);
//		} catch (IOException e) {
//			log.error("出现异常 ", e);
//		}
//		// 设置endpoint
//		ossClient.setEndpoint(OOS_ENDPOINT);
//		return ossClient;
//	}
//
//	private static AWSSecurityTokenServiceClient getAWSClient(){
//		ClientConfiguration clientConfig = new ClientConfiguration();
//		clientConfig.setConnectionTimeout(30 * 1000); //设置连接的超时时间，单位毫秒
//		clientConfig.setSocketTimeout(30 * 1000); //设置socket超时时间，单位毫秒
//		clientConfig.setProtocol(Protocol.HTTP); //设置http
//
//		//使用V4签名
//		System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");
//		 /* 创建client,其中OOSCredentials.properties中存放着用户名和密码*/
//		//负载是否参与签名、本例设置不参与
//		S3ClientOptions options = new S3ClientOptions();
//		options.setPayloadSigningEnabled(false);
//		AWSSecurityTokenServiceClient ossClient = null;
//		try {
////			ossClient = new AmazonS3Client(new PropertiesCredentials(new File(FileGlobal.OOS_FILE_PATH)), clientConfig);
//			ossClient = new AWSSecurityTokenServiceClient(new PropertiesCredentials(new File(FileGlobal.path + File.separator + "host.properties")), clientConfig);
//		} catch (IOException e) {
//			log.error("出现异常 ", e);
//		}
//		// 设置endpoint
//		ossClient.setEndpoint(OOS_ENDPOINT);
//		return ossClient;
//	}
//}
