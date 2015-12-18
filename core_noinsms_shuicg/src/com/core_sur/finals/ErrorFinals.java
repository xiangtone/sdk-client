package com.core_sur.finals;

public interface ErrorFinals {

	int NOT_ADCONTENT = 1;
	int JSON_RESOLVE =2;
	int NOT_NEWWORK=3;                       
	int ERROR_INIT_MUSTPARAMS=20001;         //必须参数 获取失败
	int ERROR_INIT_DISPENSABLEPARAMS=20002; //不必要参数 获取失败 
	int ERROR_INIT_AGAINPARAMS=20003;  //重复	  初始化 错误
	int ERROR_INIT_RESPONSENULL=20004; //初始化  返回字符串为空
	int ERROR_INIT_RESPONSEJSONANALYZE=20005; //初始化  返回字符串 Json异常
	int ERROR_NOTIFICATION_NULL = 20010; //获得推送消息失败 可能因为网络 或 返回字符串为空导致
	int ERROR_NOTIFICATION_JSONANALYZE = 20011; //获得腿送字符串成功 解析失败；
	int ERROR_AD_BANNER_ADSHOWFAIL = 20020; //广告展示 失败
	int ERROR_AD_BANNER_ADPARAMSFAIL = 20021; //广告Json参数 失败
	int ERROR_AD_BANNER_ADRESSOURCEFAIL = 20022; //广告资源下载失败
	int ERROR_AD_FOLAT_ADSHOWFAIL = 20023; //广告展示 失败
	int ERROR_AD_FOLAT_ADPARAMSFAIL = 20024; //广告Json参数 失败
	int ERROR_AD_FOLAT_ADRESSOURCEFAIL = 20025; //广告资源下载失败
}
