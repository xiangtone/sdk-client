package com.epplus.publics;

import java.lang.reflect.Method;

import android.app.Application;

import com.epplus.statistics.ThreadUtil;
import com.epplus.utils.SDKUtils;

public class WalletApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * �̻�����ʱ��Ҫ���Լ����̵�application�м�������Ǯ����ʼ���Ĵ���
         */
        if(SDKUtils.checkBaidupay()){
		    if(SDKUtils.checkBaiduConfig()){
		    	
		    	try {
		    		Class clazz = Class.forName("com.baidu.wallet.api.BaiduWallet");
					Method instance = clazz.getMethod("getInstance");
					Object BaiduWallet = instance.invoke(null);
					
					Method initWallet = clazz.getDeclaredMethod("initWallet", android.content.Context.class);
					initWallet.invoke(BaiduWallet, this);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
		    	
		    	// BaiduWallet.getInstance().initWallet(this);
		    }
        }
    }
    
    @Override
    public void onTerminate() {
    	ThreadUtil.clearThreadsta();
        super.onTerminate();
       
    }
    

}