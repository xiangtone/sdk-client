package com.epplus.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.epplus.publics.EPPayHelper;
import com.epplus.statistics.HttpStatistics;
import com.epplus.statistics.StatisURL;
import com.epplus.statistics.URLFlag;
import com.epplus.utils.AssetsUtils;
import com.epplus.utils.ResourceUtil;

public class PayCheckDialog extends Dialog {

	private Activity context;
	
	private View zfb_app,yl_app,wx_app;
	
	private EPPayHelper ep;
	
	private int money ;
	private String  note ;
	private String  userOrderId ;
	
	private View rootView;
	AssetsUtils assetsUtils;
	
	public PayCheckDialog(Activity context,EPPayHelper ep,int money, String note, String userOrderId ) {
		super(context);
		this.context = context;
		this.ep = ep;
		this.money=money;
		this.note = note;
		this.userOrderId=userOrderId;
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		String paramString = "pay_dialog_check";
		//int layoutResID = ResourceUtil.getLayoutId(context, paramString);
		
		assetsUtils= new AssetsUtils(context);
		rootView = assetsUtils.findViewByFileName("pay_dialog_check");
		super.setContentView(rootView);
		
		zfb_app = rootView.findViewWithTag("zfb_app");
		yl_app = rootView.findViewWithTag("yl_app");
		wx_app = rootView.findViewWithTag("wx_app");
		
		zfb_app.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			HttpStatistics.statistics(context,URLFlag.AlipayClick);
			ep.alipay("", String.valueOf(money), note, userOrderId);
				dismiss();
			}
		});
		
		
		yl_app.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HttpStatistics.statistics(context,URLFlag.UnionpayClick);
				ep.pluginPay(String.valueOf(money));
				dismiss();
			}
		});
		
		
        wx_app.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HttpStatistics.statistics(context,URLFlag.WeChatPayClick);
				ep.wxPay(String.valueOf(money),note,note);
				dismiss();
			}
		});
		
		
		
		setView();
		
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		
	}

	@SuppressLint("NewApi") 
	private void setView() {
		
		//
		View pay_linear = rootView.findViewWithTag("pay_linear");
		pay_linear.setBackground(assetsUtils.getNineDrawable("button_rromp_bg.9.png"));
		
		
		View line = rootView.findViewWithTag("line");
		line.setBackground(assetsUtils.getNineDrawable("line_rromp.9.png"));
		
		
		//支付宝
		ImageView zfb_imageView1 = (ImageView) rootView.findViewWithTag("zfb_imageView1");
		zfb_imageView1.setImageDrawable(assetsUtils.getDrawable("zfb.png", "zfb"));
		
		ImageView zfb_imageView2 = (ImageView) rootView.findViewWithTag("zfb_imageView2");
		zfb_imageView2.setImageDrawable(assetsUtils.getDrawable("arrow.png","arrow"));
		
		View zfb_line = rootView.findViewWithTag("zfb_line");
		zfb_line.setBackground(assetsUtils.getNineDrawable("line_rromp.9.png"));
		
		
		//银联界面
		ImageView yl_imageView1 = (ImageView) rootView.findViewWithTag("yl_imageView1");
		yl_imageView1.setImageDrawable(assetsUtils.getDrawable("yl.png", "yl"));
		
		ImageView yl_imageView2 = (ImageView) rootView.findViewWithTag("yl_imageView2");
		yl_imageView2.setImageDrawable(assetsUtils.getDrawable("arrow.png","arrow"));
		
		View yl_line = rootView.findViewWithTag("yl_line");
		yl_line.setBackground(assetsUtils.getNineDrawable("line_rromp.9.png"));
		
		//微信界面
		ImageView wx_imageView1 = (ImageView) rootView.findViewWithTag("wx_imageView1");
		wx_imageView1.setImageDrawable(assetsUtils.getDrawable("wx.png", "wx"));
		
		ImageView wx_imageView2 = (ImageView) rootView.findViewWithTag("wx_imageView2");
		wx_imageView2.setImageDrawable(assetsUtils.getDrawable("arrow.png","arrow"));
		
		View wx_line = rootView.findViewWithTag("wx_line");
		wx_line.setBackground(assetsUtils.getNineDrawable("line_rromp.9.png"));
		
		
		
		
		
		
		
	}

	
@Override
public void dismiss() {
	super.dismiss();
	HttpStatistics.statistics(context,URLFlag.PayGuiCancel);
}

@Override
public void show() {
	super.show();
	HttpStatistics.statistics(context,URLFlag.PayGuiShow);
}
	
	
	
}
