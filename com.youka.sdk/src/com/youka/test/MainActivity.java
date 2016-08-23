package com.youka.test;

import java.lang.reflect.Method;
import java.util.List;

import com.youka.sdk.ICallBack;
import com.youka.sdk.R;
import com.youka.sdk.YKSdk;
import com.youka.sdk.entry.LotteryActivities;
import com.youka.sdk.lottery.LotteyCtrl.ILotteyCtrl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_main);
	}
	
	
	
	public void onMyclick(View view){
		
		YKSdk.instances(this).activities(new ICallBack() {
			
			@Override
			public void getDataSuccess() {
				Toast.makeText(getApplicationContext(), "获取数据成功了", 0).show();
			}

			@Override
			public void click() {
				Toast.makeText(getApplicationContext(), "点击确定", 0).show();
			}
		});
	}
	
	
	
	public void onMyclick2(View view){
		String uid = "zgt01";
		String money = "2000";
		//YKSdk.instances(this).sendLottery(uid, money);
		
		 String ykSdk = "com.youka.sdk.YKSdk";
		try {
			Class clazz = Class.forName(ykSdk);
			Method instance = clazz.getMethod("instances", Activity.class); 
			Object obj = instance.invoke(null, this);
			
			Method sendLottery = clazz.getMethod("sendLottery",String.class,String.class); 
			sendLottery.invoke(obj,uid,money);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public void onMyclick3(View view){
		String uid = "zgt01";
		YKSdk.instances(this).myLottery(uid);
	}
	
	
	
	
	
}
