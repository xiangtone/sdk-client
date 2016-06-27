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
				//�ƷѼ۸��Է�Ϊ��λ
				String price = price = "200";
			
				//�Ʒѵ���
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


	
		// Ԥȡ�۸��б�������
		mSmsPay.prefetchPrice("01293912");
		
//		// �̻�ͨ�����ò�ѯ
		
		Toast.makeText(this, "Ԥȡ�۸��б�ʼ", Toast.LENGTH_SHORT).show();
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
		// ͳ����ǰȡ��������
//		mSmsPay.cancelPay();
	}
}
