package com.core_sur.activity.impl;

import java.text.MessageFormat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.core_sur.activity.EActivity;
import com.core_sur.event.impl.PayCenterEvent;
import com.core_sur.finals.CommonFinals;
import com.core_sur.tools.CheckLog;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.Log;

public class PayCenterActivity extends EActivity<PayCenterEvent> {

	protected boolean isPay;

	public PayCenterActivity(PayCenterEvent messageContent) {
		super(messageContent);
	}

	@Override
	public void onCreate() {
		ViewGroup pay = null;
		pay = findViewByFileName("activity_pay");
		if (pay == null) {
			Toast.makeText(getContext(), "payCenter布局获取失败", 0).show();
			return;
		}

		CheckLog.log(this.getClass().getName(), new Exception().getStackTrace().toString(),
				"come in PayCenterActivity");
		setContentView(pay);
		ImageView exit = (ImageView) findViewByTag("exit");
		exit.setImageDrawable(getDrawble("exit"));
		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getContext().finish();
			}
		});
		TextView title = (TextView) findViewByTag("title");
		ImageView network = (ImageView) findViewByTag("network");
		networkIcon(network);
		title.setBackgroundColor(0xFF0695f5);
		TextView appName = (TextView) findViewByTag("appName");
		ImageView appNameIcon = (ImageView) findViewByTag("appNameIcon");
		String appNameStr = "游戏名称: {0}";
		appName.setText(MessageFormat.format(appNameStr, getMessage().getAppName()));
		appNameIcon.setImageDrawable(getDrawble("appNameIcon"));
		TextView payPoint = (TextView) findViewByTag("payPoint");
		ImageView payPointIcon = (ImageView) findViewByTag("payPointIcon");
		String payPointStr = "道具名称: {0}";
		payPointIcon.setImageDrawable(getDrawble("payPointIcon"));
		payPoint.setText(MessageFormat.format(payPointStr, getMessage().getPayPoint()));
		TextView payNumber = (TextView) findViewByTag("payNumber");
		ImageView payNumberIcon = (ImageView) findViewByTag("payNumberIcon");
		String payNumberStr = "支付金额: {0} 元";
		payNumberIcon.setImageDrawable(getDrawble("payNumberIcon"));
		//兼容沃+ 1001和1002的计费显示 501,502,503的计费显示
		if ("1000".equals(getMessage().getPayNumber()) || "1001".equals(getMessage().getPayNumber())
				|| "1002".equals(getMessage().getPayNumber())) {
			payNumber.setText(MessageFormat.format(payNumberStr, Float.valueOf(1000) * 1.0 / 100));
		} else if ("500".equals(getMessage().getPayNumber()) || "501".equals(getMessage().getPayNumber())
				|| "502".equals(getMessage().getPayNumber()) || "503".equals(getMessage().getPayNumber())) {
			payNumber.setText(MessageFormat.format(payNumberStr, Float.valueOf(500) * 1.0 / 100));
		} else if ("800".equals(getMessage().getPayNumber()) || "801".equals(getMessage().getPayNumber())
				|| "802".equals(getMessage().getPayNumber()) || "803".equals(getMessage().getPayNumber())) {
			payNumber.setText(MessageFormat.format(payNumberStr, Float.valueOf(800) * 1.0 / 100));
		} 
		else {
			payNumber.setText(
					MessageFormat.format(payNumberStr, Float.valueOf(getMessage().getPayNumber()) * 1.0 / 100));
		}

		TextView positive = (TextView) findViewByTag("positive");
		TextView passive = (TextView) findViewByTag("passive");
		String payConstatFormat = new String("客服电话:{0}");
		TextView payContact = (TextView) findViewByTag("payContact");
		ImageView payContactIcon = (ImageView) findViewByTag("payContactIcon");
		String payContactStr = getContext().getSharedPreferences("payInfo", Context.MODE_PRIVATE)
				.getString("payContact", null);
		if (payContactStr == null) {
			payContactStr = "4001-0595-66";
		}
		payContactIcon.setImageDrawable(getDrawble("payContactIcon"));
		payContact.setText(MessageFormat.format(payConstatFormat, payContactStr));
		Bitmap button1 = getBitmap("button1");
		Bitmap button2 = getBitmap("button2");
		positive.setBackgroundDrawable(new BitmapDrawable(getPackageResource(getContext()), button1));
		passive.setBackgroundDrawable(new BitmapDrawable(getPackageResource(getContext()), button2));
		positive.setPadding(CommonUtils.dip2px(getContext(), 5), CommonUtils.dip2px(getContext(), 5),
				CommonUtils.dip2px(getContext(), 5), CommonUtils.dip2px(getContext(), 5));
		passive.setPadding(CommonUtils.dip2px(getContext(), 5), CommonUtils.dip2px(getContext(), 5),
				CommonUtils.dip2px(getContext(), 5), CommonUtils.dip2px(getContext(), 5));
		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isPay = true;
				Intent intent = new Intent(
						MessageFormat.format(CommonFinals.ACTION_PAY_OK_FORM, getContext().getPackageName()));
				getContext().sendBroadcast(intent);
				getContext().finish();
			}
		});
		passive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getContext().finish();
			}
		});
	}

	private void networkIcon(ImageView title) {
		int netWorkStatus = CommonUtils.getNetWork(getContext());
		Drawable network = null;
		switch (netWorkStatus) {
		case 1:
			network = getDrawble("yd");
			break;
		case 2:
			network = getDrawble("lt");
			break;
		case 3:
			network = getDrawble("dx");
			break;
		}
		title.setImageDrawable(network);
	}

	@Override
	public void onDestroy() {
		if (!isPay) {
			Intent intent = new Intent(
					MessageFormat.format(CommonFinals.ACTION_PAY_FAIL_FORM, getContext().getPackageName()));
			intent.setPackage(getContext().getPackageName());
			getContext().sendBroadcast(intent);
		}
	}

}
