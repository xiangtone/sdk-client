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
         * 商户接入时需要在自己工程的application中加入以下钱包初始化的代码
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