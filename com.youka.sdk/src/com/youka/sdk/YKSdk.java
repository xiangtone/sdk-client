package com.youka.sdk;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.youka.sdk.dialog.LotteryActivitiesDialog;
import com.youka.sdk.dialog.MyLotteyDialog;
import com.youka.sdk.dialog.SendLotteyDialog;
import com.youka.sdk.dialog.YKDialogManger;

/**
 * 有卡sdk
 * @author zgt
 *
 */
public class YKSdk {
	
	
	private static YKSdk ykSdk;
	private Activity context;
	
	private ArrayList<Dialog> dialogs = new ArrayList<Dialog>();
	
	public static YKSdk  instances(Activity context){
		if(ykSdk==null){
			ykSdk = new YKSdk();
		}
		ykSdk.context=context;
		return ykSdk;
	}
	
	/**
	 * 获取彩票活动
	 * @param iCtrl
	 */
	public void activities(ICallBack callBack){
//		LotteryActivitiesDialog dialog = new LotteryActivitiesDialog(context,callBack);
//		dialog.show();
//		dialogs.add(dialog);
		YKDialogManger.instances(context).activities(callBack);		
		
		
	}
	
	
	
	
	/**
	 * 送彩票
	 * @param uid
	 * @param money
	 */
	public void sendLottery(String uid,String money){
		
		YKDialogManger.instances(context).sendLottery(uid, money);
		
//		SendLotteyDialog dialog = new SendLotteyDialog(uid, money, context);
//		dialog.show();
//		dialogs.add(dialog);
	}
	
	
	/**
	 * 我的彩票
	 * @param uid
	 */
	public void myLottery(String uid){
		
		YKDialogManger.instances(context).myLottery(uid);
		
//		MyLotteyDialog dialog = new MyLotteyDialog(uid, context);
//		dialog.show();
//		dialogs.add(dialog);
	}
	
	
	
	/**
	 * 取消dialog
	 */
	public void dismiss(){
		for(Dialog dialog:dialogs){
			if(dialog!=null){
				dialog.dismiss();
			}
		}
		dialogs.clear();
	}
	
	
	
  	

}
