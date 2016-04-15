package com.epplus.publics;

import android.app.Application;

import com.baidu.wallet.api.BaiduWallet;
import com.epplus.statistics.ThreadUtil;
import com.epplus.utils.ConfigUtils;
import com.epplus.utils.SDKUtils;

public class WalletApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * �̻�����ʱ��Ҫ���Լ����̵�application�м�������Ǯ����ʼ���Ĵ���
         */
        if(SDKUtils.checkBaiduConfig()){
        	 BaiduWallet.getInstance().initWallet(this);
        }
    }
    
    @Override
    public void onTerminate() {
    	ThreadUtil.clearThreadsta();
        super.onTerminate();
       
    }
    

}