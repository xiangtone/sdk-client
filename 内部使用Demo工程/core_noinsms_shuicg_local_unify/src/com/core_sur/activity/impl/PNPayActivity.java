package com.core_sur.activity.impl;

import java.text.MessageFormat;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chinaMobile.udata.charge.mini.e;
import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.activity.EActivity;
import com.core_sur.bean.RevBean;
import com.core_sur.event.impl.PayCenterEvent;
import com.core_sur.finals.CommonFinals;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.mlog;

/**
 * 输入手机号支付界面
 * @author Wang
 *
 */
public class PNPayActivity extends EActivity<PayCenterEvent> {

	/**
	 * -1计费失败，0计费成功,1获取验证码中，2获取计费结果中
	 */
	protected int isPay;
	private ImageView logo;
	private LinearLayout content;
	private TextView goodname,amount,cusphone;
	private EditText pnumber,code;
	private Button getcode,paybtn;
	private int count = 60;
	private int count_temp = count;
	private Timer timer = new Timer();
	private AlertDialog dialog;
	private String reString = "{0}.com.my.fee.pnumber";
	private String sendString = "{0}.com.my.fee.pnumber.send";
	private boolean isRun = false;
	
	public BroadcastReceiver pnReceiverpayReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			//mlog.i("pnReceiverpayReceiver ==" + (int)intent.getIntExtra("code", 1));
			
			switch ((int)intent.getIntExtra("code", 1)) {
			case 0:
				//PhoneNumber计费获取验证码成功
				if (isPay == 1) {
					dialog.dismiss();
					startCD();
					isPay = -1;
				}else if (isPay == 2) {
					dialog.dismiss();
					isPay = 0;
					
					Intent suss = new Intent(MessageFormat.format(sendString,getContext().getPackageName()));
					suss.putExtra("what", 3);
					suss.putExtra("msg", "success");
					getContext().sendBroadcast(suss);
					
					getContext().finish();
				}
				break;
			case -1:
				//PhoneNumber计费失败
				if (isPay == 1) {
					dialog.dismiss();
					//startCD();
					isPay = -1;
					Toast.makeText(getContext(), "获取验证码,请检查手机号，或者稍后再试", 1200).show();
				}else if (isPay == 2) {
					dialog.dismiss();
					isPay = -1;
					
					Toast.makeText(getContext(), "验证验证码失败", 1200).show();
					
					Intent suss = new Intent(MessageFormat.format(sendString,getContext().getPackageName()));
					suss.putExtra("what", 3);
					suss.putExtra("msg", "fail");
					getContext().sendBroadcast(suss);

					//getContext().finish();
				}
				break;
			case -2:
				//PhoneNumber计费无返回，继续请求
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent re = new Intent(MessageFormat.format(sendString,getContext().getPackageName()));
				re.putExtra("what", 4);
				re.putExtra("msg", "");
				getContext().sendBroadcast(re);
				break;
			case -3:
				//PhoneNumber自动填写验证码
				if (code.getText().toString().length() == 0) {
					code.setText(intent.getStringExtra("vcode") + "");
				}
				break;

			default:
				break;
			}
		}
	};
	
	private TimerTask task = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(1);
		}
	};
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:
				if (count_temp > 0) {
					getcode.setText("再次(" + count_temp +")");
					count_temp--;
				}else {
					getcode.setEnabled(true);
					getcode.setText("获取验证码");
					task = null;
					timer = null;
				}
				break;
			default:
				break;
			}
		}
	};	
		
	OnKeyListener kl = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	public PNPayActivity(PayCenterEvent messageContent) {
		super(messageContent);
	}

	@Override
	public void onCreate() {
		ViewGroup pay=null;
		 pay = findViewByFileName("pn_layout");
		 if(pay==null){
			Toast.makeText(getContext(), "payCenter布局获取失败", 0).show(); 
			return;
		 }
		setContentView(pay);
		
		logo = (ImageView) findViewByTag("logo_image");
		logo.setImageDrawable(getDrawble("xypay_logo"));
		
		content = (LinearLayout) findViewByTag("content_layout");
		content.setBackgroundDrawable(getXmlDrawble("xypay_info_bg"));
		
		goodname = (TextView) findViewByTag("goodname_tv");
		goodname.setText(getMessage().getPayPoint());
		
		amount = (TextView) findViewByTag("amount_tv");
		amount.setText(Float.valueOf(getMessage().getPayNumber()) * 1.0 / 100 + "");
		
		cusphone = (TextView) findViewByTag("cusphone_tv");
		String payContactStr = getContext().getSharedPreferences("payInfo",
				Context.MODE_PRIVATE).getString("payContact", null);
		if (payContactStr == null) {
			payContactStr = "4001-0595-66";
		}
		cusphone.setText(payContactStr);
		
		pnumber = (EditText) findViewByTag("pnumber_et");
		pnumber.setBackgroundDrawable(getXmlDrawble("xypay_ipt_bg"));
		
		code = (EditText) findViewByTag("code_et");
		code.setBackgroundDrawable(getXmlDrawble("xypay_ipt_bg"));
		
		getcode = (Button) findViewByTag("getcode_btn");	
		Drawable pressed = getXmlDrawble("xypay_btn_pressed");
		Drawable normal = getXmlDrawble("xypay_btn_normal");
		StateListDrawable sd = new StateListDrawable();
		sd.addState(new int[]{-android.R.attr.state_enabled}, pressed);
		sd.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, normal);  
        sd.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, pressed);  
        sd.addState(new int[]{android.R.attr.state_focused}, normal);  
        sd.addState(new int[]{android.R.attr.state_pressed}, pressed);  
        sd.addState(new int[]{android.R.attr.state_enabled}, normal);  
        sd.addState(new int[]{}, normal); 
		getcode.setBackgroundDrawable(sd);
		getcode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//提交手机号
				if (postPN()) {
					isPay = 1;
					dialog.show();
				}
			}
		});
		
		dialog = new AlertDialog.Builder(getContext()).setView(new ProgressBar(getContext())).create();
		
		paybtn = (Button) findViewByTag("pay_btn");
		paybtn.setBackgroundDrawable(getXmlDrawble("xypay_red_bg"));
		
		paybtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//提交验证码，等待结果
				if (postCode()) {
					isPay = 2;
					dialog.show();
					
					//开始查询结果
				}
			}
		});
				
		getContext().registerReceiver(pnReceiverpayReceiver, new IntentFilter(MessageFormat.format(reString,
			getContext().getPackageName())));
		
	}

	@Override
	public void onDestroy() {
		if (isPay != 0) {
			Intent intent = new Intent(MessageFormat.format(
					CommonFinals.ACTION_PAY_FAIL_FORM, getContext()
							.getPackageName()));
			intent.setPackage(getContext().getPackageName());
			getContext().sendBroadcast(intent);
			
			Intent intent2 = new Intent(MessageFormat.format(sendString,getContext().getPackageName()));
			intent2.putExtra("what", 3);
			intent2.putExtra("msg", "fail");
			getContext().sendBroadcast(intent2);
			
		}
		if (pnReceiverpayReceiver != null) {
			getContext().unregisterReceiver(pnReceiverpayReceiver);
		}
		
		if (isRun = true) {
			isRun = false;
		}
	}
	
	private boolean postCode(){
		if (code.getText().toString().length() > 0) {
			Intent intent = new Intent(MessageFormat.format(sendString,getContext().getPackageName()));
			intent.putExtra("what", 2);
			intent.putExtra("msg", code.getText().toString());
			getContext().sendBroadcast(intent);
			
			return true;
		}
		return false;
	}
	
	private boolean postPN(){
		if (pnumber.getText().toString().length() == 11) {
						
			Intent intent = new Intent(MessageFormat.format(sendString,getContext().getPackageName()));
			intent.putExtra("what", 1);
			intent.putExtra("msg", pnumber.getText().toString());
			getContext().sendBroadcast(intent);
			
			return true;
		}
		return false;
	}
	
	private void startCD(){
		count_temp = count;
		if (timer == null)
			timer = new Timer();
		task = new TimerTask() {
			public void run() {
				handler.sendEmptyMessage(1);
			}
		};
		timer.schedule(task, 1000L, 1000L);
		getcode.setEnabled(false);
	}
	

	
}
