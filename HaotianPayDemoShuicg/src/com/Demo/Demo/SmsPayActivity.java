package com.Demo.Demo;


import com.Demoht_ep.Demo_ep_moliBird.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



@SuppressLint("NewApi")
public class SmsPayActivity extends Activity {
	private static final String tag = "[SmsPayActivity]";

	private StartSmsPay mSmsPay = null;

	private TextView mTextView = null;
	private boolean mUseAppUI = false;

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(tag, "SmsPayActivity onCreate");
		setContentView(R.layout.activity_smspay);
		mSmsPay = new StartSmsPay(this);
		mTextView = (TextView) findViewById(R.id.texttips);

		Button start = (Button) findViewById(R.id.start);
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//计费价格，以分为单位
				String price = price = "200";
			
				//计费点编号
				String payPoint = "1";
				
				mSmsPay.startPay(payPoint, price, mUseAppUI);
			}
		});

		Button prefetch = (Button) findViewById(R.id.prefetch);
		prefetch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(SmsPayActivity.this, mSmsPay.getPriceList(),
						Toast.LENGTH_SHORT).show();
			}
		});


	
		// 预取价格列表功能启动
		mSmsPay.prefetchPrice("01293912");
		
//		// 商户通用配置查询
		
		Toast.makeText(this, "预取价格列表开始", Toast.LENGTH_SHORT).show();
	}

	public void refreshResult(String retInfo) {
		if (mTextView != null) {
			mTextView.setText(mTextView.getText() + retInfo + "\n");
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 统计提前取消的数据
//		mSmsPay.cancelPay();
	}
}
