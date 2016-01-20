package com.core_sur.activity.impl;

import android.app.Activity;

import com.core_sur.activity.EActivity;
import com.core_sur.event.impl.UMPayEvent;
import com.core_sur.task.PayTaskManager;

public class PayLoadActivtiy  extends EActivity<UMPayEvent>{
	public PayLoadActivtiy(UMPayEvent messageContent) {
		super(messageContent);
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDestroy() {
		
	}

}
