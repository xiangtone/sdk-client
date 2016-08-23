package com.example.testdemo;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.account.bean.UserInfo;
import com.account.util.AccountService;
import com.account.util.CallBack;
import com.epplus.publics.EPPayHelper;
import com.epplus.view.PayParams;

public class XTSDK {
	
	private static XTSDK xtsdk;
	
	private boolean isInit;
	
	private UserInfo userInfo;
	
	private Handler mHandler;
	
	private XTSDK(){
		isInit = false;
	} 
	
	/**
	 * ��ȡXTSDK
	 * @return
	 */
	public static XTSDK getInstance(){
		if(xtsdk==null){
			xtsdk = new XTSDK();
		}
		return xtsdk;
	}
	
	/**
	 * ��ʼ��
	 */
	public void init(final Activity ac,String payContact,Handler handler){
		this.mHandler = handler;
		EPPayHelper.getInstance(ac).initPay(true,payContact,"123","11");//"4001059566"
		EPPayHelper.getInstance(ac).setPayListen(handler);
		isInit = true;
		
		//����û���¼���Զ���½
		AtomicBoolean initialized = new AtomicBoolean(false);
		if(initialized.compareAndSet(false, true)){
		   //�Զ���½
			AccountService.getInstances().autoLogin(ac, new CallBack() {
				@Override
				public void loginSuccess(UserInfo arg0) {
					userInfo = arg0;
					Toast.makeText(ac, "�Զ���½", Toast.LENGTH_SHORT).show();
					Message msg = mHandler.obtainMessage();
					msg.what = 4011;
					msg.obj =arg0; 
					mHandler.sendMessage(msg);
				}
			});
		}
		
	}
	
	
	/**
	 * �û���¼
	 */
	public void login(final Activity ac){
		if(isInit){
		
		AccountService.getInstances().showWebDialog(ac, new CallBack() {
			
			@Override
			public void loginSuccess(UserInfo arg0) {
				userInfo = arg0;
				Toast.makeText(ac, "��½�ɹ�", Toast.LENGTH_SHORT).show();
				Message msg = mHandler.obtainMessage();
				msg.what = 4012;
				msg.obj =arg0; 
				mHandler.sendMessage(msg);
			}
		});
		}
	}
	
	
	/**
	 * ֧�� 
	 */
	public boolean pay(Activity ac,PayParams params){
		
		if(!ac.isFinishing()){
			if(userInfo!=null&&!TextUtils.isEmpty(userInfo.getUserID())&&isInit){
				params.setUid(userInfo.getUserID());
				EPPayHelper.getInstance(ac).pay(params);
				return true;
			}else {
				return false;
			}
		}
		return false;
		
	}
	
	/**
	 * �ǳ�
	 */
	public void logout(){
		userInfo = null;
		AccountService.getInstances().logout();
		
	}
	
	/**
	 * �˳�
	 */
	public void exit(Activity ac){
		try {
			EPPayHelper.getInstance(ac).exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ֧���ص����
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void payCallResult(Activity ac,int requestCode, int resultCode, Intent data){
		EPPayHelper.getInstance(ac).onActivityResult(requestCode, resultCode, data);
	}
	

}
