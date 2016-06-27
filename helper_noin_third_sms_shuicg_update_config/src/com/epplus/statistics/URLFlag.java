package com.epplus.statistics;
/**  
   参数标志
  @author zgt
 */
public interface URLFlag {
	
	int PayGuiShow = 10,
	PayGuiCancel = 11,
	
	AlipayClick = 201 ,
	AlipayCancel =  202 ,
	AlipaySuccess = 203 ,
	AlipayFail  = 204 ,
	
	UnionpayClick =  301 ,
	UnionpayCancel =  302 ,
	UnionpaySuccess =  303 ,
	UnionpayFail =304 ,
	
	
	WeChatPayClick = 401 ,
	WeChatPayCancel =  402 ,
	WeChatpaySuccess=  403 ,
	WeChatpayFail =404 ,
	
	BaidupayClick =501 ,
	BaidupayCancel =502 ,
	BaidupaySuccess = 503 ,
	BaidupayFail  = 504 ,
	
	
	SmsClick =601 ,
	SmsCancel =602 ,
	SmsSuccess = 603 ,
	SmsFail  = 604 ,
	
	WxWapClick =701 ,
	WxWapCancel =702 ,
	WxWapSuccess = 703 ,
	WxWapFail  = 704 ;
	
	

}
