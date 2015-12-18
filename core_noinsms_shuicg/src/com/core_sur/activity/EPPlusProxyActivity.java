package com.core_sur.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.core_sur.activity.impl.PNPayActivity;
import com.core_sur.activity.impl.PayCenterActivity;
import com.core_sur.event.impl.PayCenterEvent;
import com.core_sur.finals.CommonFinals;
import com.core_sur.interfaces.ProxyInterface;
import com.core_sur.tools.CheckLog;

public class EPPlusProxyActivity implements ProxyInterface {
	public Activity activity = null;
	private EActivity instance = null;

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	// TODO Auto-generated constructor stub
	public EPPlusProxyActivity() {
	}

	@Override
	public void onCreate(Intent intent) {
		// TODO Auto-generated method stub
		Bundle extras = intent.getExtras();
		if (extras == null) {
			finish();
			CheckLog.log("EPPlusProxyActivity", "onCreate",
					"创建 Acitivty 意图Error");
			return;
		}
		int type = extras.getInt("message_type");
		switch (type) {
		case CommonFinals.MESSAGE_TYPE_POSITIVEPAY_ACTIVITY:
			String msg = extras.getString("message");
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(msg);
				PayCenterEvent payCenterEvent = new PayCenterEvent(
						jsonObj.getString("payPoint"),
						jsonObj.getString("appName"),
						jsonObj.getString("payNumber"));
				instance = new PayCenterActivity(payCenterEvent);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CommonFinals.MESSAGE_TYPE_PNPAY_ACTIVITY:
			msg = extras.getString("message");
			try {
				jsonObj = new JSONObject(msg);
				PayCenterEvent payCenterEvent = new PayCenterEvent(
						jsonObj.getString("payPoint"),
						jsonObj.getString("appName"),
						jsonObj.getString("payNumber"));
				instance = new PNPayActivity(payCenterEvent);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
		if (instance == null) {
			CheckLog.log("EPPlusProxyActivity", "onCreate",
					"activity error instance==null");
			activity.finish();
			return;
		}
		instance.setContext(activity);
		instance.onCreate();
	}

	public void finish() {
		activity.finish();
	}

	@Override
	public void onDestroy() {
		if (instance == null) {
			return;
		}
		instance.onDestroy();
	}
}
