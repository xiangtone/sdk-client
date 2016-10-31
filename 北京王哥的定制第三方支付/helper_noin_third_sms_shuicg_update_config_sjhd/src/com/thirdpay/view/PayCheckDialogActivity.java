package com.thirdpay.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import android.R.id;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thirdpay.publics.ThirdPayHelper;
import com.thirdpay.statistics.HttpStatistics;
import com.thirdpay.statistics.JSON;
import com.thirdpay.statistics.URLFlag;
import com.thirdpay.utils.ApkUtils;
import com.thirdpay.utils.AssetsUtils;
import com.thirdpay.utils.LogUtils;
import com.thirdpay.utils.SDKUtils;

@SuppressLint("DefaultLocale") 
public class PayCheckDialogActivity extends Activity implements OnItemClickListener {

	
	
	public static final String CALLBACK = "com.thirdpay.view.PayCheckDialogActivity.CALLBACK";
	
	
	private AssetsUtils assetsUtils;

	private int money;
	private String note;
	private String userOrderId;
	
	//游戏类型
	private String gameType;
	//网游支付参数
	private PayParams mPayParams;

	private View rootView;

	private ListView pay_check_listview;

	private ArrayList<PayTypeBean> datas;

	private Drawable imgs[] = new Drawable[7];
	private String texts[] = { "支付宝", "银联支付", "微信支付", "百度支付" ,"话费支付","微信支付","微信支付"};
	private PayFlag flags[] = { PayFlag.ZFB, PayFlag.YL, PayFlag.WX, PayFlag.BD,PayFlag.SMS ,PayFlag.WXWAP,PayFlag.WXSwiftPay};

	enum PayFlag {
		ZFB, YL, WX, BD,SMS,WXWAP,WXSwiftPay;
	}

	private final String PayListView = "pay_dialog_check_list";
	private final String PayListViewItem = "pay_dialog_check_list_item";

	
	private ThirdPayHelper ep;
	private Activity context;
	
	private HashMap<String, PayTypeBean> maps ;
	
	private HashMap<String, String> mShowFlags;
	
	
	private PayParams params;
	
	private void init(){
		datas = new ArrayList<PayTypeBean>();
		maps = new HashMap<String,PayTypeBean>();
		imgs[0] = assetsUtils.getDrawable("icon_zfb.png", "zfb");
		imgs[1] = assetsUtils.getDrawable("yl.png", "yl");
		imgs[2] = assetsUtils.getDrawable("icon_wx.png", "wx");
		imgs[3] = assetsUtils.getDrawable("baidu_icon.png", "bd");
		imgs[4] = assetsUtils.getDrawable("icon_sms.png", "bd");
		imgs[5] = assetsUtils.getDrawable("icon_wx.png", "wx");
		imgs[6] = assetsUtils.getDrawable("icon_wx.png", "wx");

		
		maps.put(ShowFlag.alipay, new PayTypeBean(imgs[0], texts[0], flags[0]));
		maps.put(ShowFlag.unionpay, new PayTypeBean(imgs[1], texts[1], flags[1]));
		maps.put(ShowFlag.wechatpay, new PayTypeBean(imgs[2], texts[2], flags[2]));
		maps.put(ShowFlag.baidupay, new PayTypeBean(imgs[3], texts[3], flags[3]));
		maps.put(ShowFlag.smspay, new PayTypeBean(imgs[4], texts[4], flags[4]));
		maps.put(ShowFlag.wxWapPay, new PayTypeBean(imgs[5], texts[5], flags[5]));
		maps.put(ShowFlag.wxSwiftPay, new PayTypeBean(imgs[6], texts[6], flags[6]));
		

		Iterator<String> it = mShowFlags.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			//到时间判断key 是否要显示
			String vlaue = mShowFlags.get(key);
			if("1".equals(vlaue)){
				if(maps.containsKey(key)){
					PayTypeBean bean = maps.get(key);
					if(ShowFlag.baidupay.equals(key)){
						if(!SDKUtils.checkBaiduConfig()){
							continue;
						}
					}
					
					//检测支付宝是否配置
					if(ShowFlag.alipay.equals(key)){
						if(!SDKUtils.checkAliPay()){
							continue;
						}
					}
					
					if(ShowFlag.unionpay.equals(key)){
						if(!SDKUtils.checkUnionpay()){
							continue;
						}
					}
					
					if(ShowFlag.wechatpay.equals(key)){
						if(!SDKUtils.checkWXpay()){
							continue;
						}
					}
					if(ShowFlag.baidupay.equals(key)){
						if(!SDKUtils.checkBaidupay()){
							continue;
						}
					}
					
					if(ShowFlag.wxSwiftPay.equals(key)){
						if(!SDKUtils.checkWXSwiftpay()){
							continue;
						}
					}
					
					
					datas.add(bean);
				}
			}
		}
		
		if(datas.size()>0){
			Collections.sort(datas, new Comparator<PayTypeBean>() {
			@Override
			public int compare(PayTypeBean lhs, PayTypeBean rhs) {
				return lhs.getFlag().compareTo(rhs.getFlag());
			}
		    });
		}
	}
	

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		m_init();
				
		requestWindowFeature(Window.FEATURE_NO_TITLE);
  		rootView = assetsUtils.findViewByFileName(PayListView);
  		

  		
  		getWindow().setBackgroundDrawable(new ColorDrawable(0));
  		initView();
  		pay_check_listview = (ListView) rootView.findViewWithTag("pay_check_listview");

  		ColorDrawable pressed = new ColorDrawable(Color.parseColor("#E5E5E5"));
  		StateListDrawable drawable = AssetsUtils.newSelector(null, pressed,null, null);
  		pay_check_listview.setSelector(drawable);

  		PayAdapter adapter = new PayAdapter();
  		pay_check_listview.setAdapter(adapter);
  		pay_check_listview.setOnItemClickListener(this);
		
		
		super.setContentView(rootView);
		
		//setFinishOnTouchOutside(false); 
		IntentFilter filter = new IntentFilter(CALLBACK);
		registerReceiver(receiver, filter);
		rootView.setVisibility(View.GONE);
		pay();
		
	}

	@SuppressWarnings("unchecked")
	private void m_init() {
		
		//params = (PayParams) getIntent().getSerializableExtra("params");
		String jsonParams =  getIntent().getStringExtra("params");
		params = JSON.parseObject(jsonParams, PayParams.class);
		
		
		this.assetsUtils = new AssetsUtils(this);
		this.money =params.getPrice();
		this.note = params.getProductName();
		this.userOrderId = params.getCpOrderId();
		ep = ThirdPayHelper.getInstance(this);
        this.context =this;
//        this.mShowFlags = showFlags;
//        this.gameType = gameType;
        
        //this.mShowFlags = (HashMap<String, String>) getIntent().getSerializableExtra("mShowFlags");
        String jsonShowFlags =  getIntent().getStringExtra("mShowFlags");
        this.mShowFlags = (HashMap<String, String>) JSON.toJsonMap(jsonShowFlags);
        
        
        this.gameType = getIntent().getStringExtra("gameType");
        
        
        if(this.mShowFlags.containsKey(ShowFlag.webOrderid)){
   		 params.setWebOrderid(this.mShowFlags.get(ShowFlag.webOrderid));
   	    }
        this.mPayParams = params;
        //setCanceledOnTouchOutside(false);
        init();
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PayTypeBean bean = datas.get(position);
		switch (bean.getFlag()) {
		case ZFB:
			//支付宝支付
			AlipayPay();
			break;
		case YL:
			//银联支付
			unionPay();
			break;
		case WX:
			//微信支付
			weChatPay();
			break;
		case BD:
			//百度支付
			baiduPay();
			break;
		case WXWAP:
			//微信wap支付
			wxWapPay();
			break;
		case WXSwiftPay:
			//SwiftPay 微信wap支付
			wxSwiftPay();
			break;

		default:
			break;
		}

	}
	
	
	/**
	 * SwiftPay
	 */
	private void wxSwiftPay() {
		HttpStatistics.statistics(context,userOrderId,URLFlag.WxSwiftPayClick,gameType,mPayParams);
		ep.wxSwifPay(mPayParams);
		dismiss();
	}
	
	

	/**
	 * 微信wap支付
	 */
	private void wxWapPay() {
		HttpStatistics.statistics(context,userOrderId,URLFlag.WxWapClick,gameType,mPayParams);
		ep.wxWapPay(mPayParams);
		//dismiss();
		
	}


	/**
	 * 微信支付
	 */
	private void weChatPay() {
		HttpStatistics.statistics(context,userOrderId,URLFlag.WeChatPayClick,gameType,mPayParams);
		ep.wxPay(mPayParams);
		//dismiss();
	}

	/**
	 * 百度支付
	 */
	private void baiduPay() {
		HttpStatistics.statistics(context,userOrderId,URLFlag.BaidupayClick,gameType,mPayParams);
		ep.baiduPay(mPayParams);
		//dismiss();
	}

	/**
	 * 银联支付
	 */
	private void unionPay() {
		HttpStatistics.statistics(context,userOrderId,URLFlag.UnionpayClick,gameType,mPayParams);
		ep.pluginPay(mPayParams);
		//dismiss();

	}

	/**
	 * 支付宝支付
	 */
	private void AlipayPay() {
		HttpStatistics.statistics(context,userOrderId,URLFlag.AlipayClick,gameType,mPayParams);
		ep.alipay(mPayParams);
		//dismiss();
	}

	@SuppressLint("NewApi")
	private void initView() {
		View pay_linear = rootView.findViewWithTag("pay_linearlay");
		pay_linear.setBackground(assetsUtils
				.getNineDrawable("button_rromp_bg.9.png"));

		View lineView = rootView.findViewWithTag("pay_top_line_view");
		lineView.setBackground(assetsUtils.getNineDrawable("line_rromp.9.png"));
		
		TextView goodsTxt =  (TextView) rootView.findViewWithTag("pay_good_name_txt");
		TextView priceTxt =  (TextView) rootView.findViewWithTag("pay_price_txt");
		ImageView goodsImg = (ImageView) rootView.findViewWithTag("pay_icon_good_img");
		ImageView priceImg = (ImageView) rootView.findViewWithTag("pay_icon_price_img");
		goodsImg.setImageDrawable(assetsUtils.getDrawable("icon_goods.png", ""));
		priceImg.setImageDrawable(assetsUtils.getDrawable("icon_price.png", ""));
		goodsTxt.setText(note);
		priceTxt.setText(changePrice()+"元");
		
		
		View showGoodsLay = rootView.findViewWithTag("pay_show_goods_lay");
		showGoodsLay.setVisibility(View.VISIBLE);
		TextView payTitleTxt = (TextView) rootView.findViewWithTag("pay_title");
		payTitleTxt.setText("支付");
		
		if(mShowFlags.containsKey(ShowFlag.productInfo)){
			String value = mShowFlags.get(ShowFlag.productInfo);
			if("1".equals(value)){
				showGoodsLay.setVisibility(View.VISIBLE);
			}else if ("0".equals(value)) {
				showGoodsLay.setVisibility(View.GONE);
			}
		}
		
	}
	
	
	@Override
	public void onBackPressed() {
		dismiss();
		context.finish();
	}
	
	private String changePrice(){
		float m = money/100.0f;
		String str = String.format("%.2f", m);
		return str;
	}
	
 
	class PayAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int postion) {
			return datas.get(postion);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			Stub stub;
			if (view == null) {
				view = assetsUtils.findViewByFileName(PayListViewItem);
				stub = new Stub();
				stub.lay = (RelativeLayout) view.findViewWithTag("item_lay");
				stub.img1 = (ImageView) view.findViewWithTag("item_img1");
				stub.img2 = (ImageView) view.findViewWithTag("item_img2");
				stub.txt = (TextView) view.findViewWithTag("item_txt");
				stub.line = view.findViewWithTag("item_line");
				view.setTag(stub);
			} else {
				stub = (Stub) view.getTag();
			}

			PayTypeBean bean = datas.get(position);
			stub.img1.setImageDrawable(bean.getImg());
			stub.txt.setText(bean.getText());

			stub.img2.setImageDrawable(assetsUtils.getDrawable("arrow.png",
					"arrow"));
			stub.line.setBackground(assetsUtils
					.getNineDrawable("line_rromp.9.png"));
			return view;
		}

	}

	class Stub {
		public RelativeLayout lay;
		public ImageView img1;
		public ImageView img2;
		public TextView txt;
		public View line;
	}

	class PayTypeBean {

		private Drawable img;
		private String text;
		private PayFlag flag;

		
		
		
		public PayTypeBean(Drawable img, String text, PayFlag flag) {
			super();
			this.img = img;
			this.text = text;
			this.flag = flag;
		}
		
		public PayTypeBean() {
		}

		public PayFlag getFlag() {
			return flag;
		}

		public void setFlag(PayFlag flag) {
			this.flag = flag;
		}

		public Drawable getImg() {
			return img;
		}

		public void setImg(Drawable img) {
			this.img = img;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}
	
	
	//@Override
	public void dismiss() {
		//super.dismiss();
		if(datas.size()>0){
		 HttpStatistics.statistics(context,userOrderId,URLFlag.PayGuiCancel,gameType,mPayParams);
		}
	}
	
	//@Override
//	public void show() {
//		//super.show();
//		if(datas.size()<=0){
//			dismiss();
//		}else {
//			HttpStatistics.statistics(context,userOrderId,URLFlag.PayGuiShow,gameType,mPayParams);
//		}
//	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ThirdPayHelper.getInstance(this).onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			dismiss();
			finish();
			unregisterReceiver(this);
		}
		
	};
	
	public static void sendBroadcast(Activity activity){
		Intent intent = new Intent(CALLBACK);
		activity.sendBroadcast(intent);
		
	}

	/**
	 * 调起支付
	 */
	private void pay(){
		if(payPriority()){
			//微信wap支付
			String value = mShowFlags.get(ShowFlag.wxWapPay);
			if(!TextUtils.isEmpty(value)){
				if("1".equals(value)){
					//微信wap支付
					wxWapPay();
					return;
				}
			}
			
			value =  mShowFlags.get(ShowFlag.wxSwiftPay);
			if(!TextUtils.isEmpty(value)){
				if("1".equals(value)){
					//SwiftPay微信wap支付
					wxSwiftPay();
					return;
				}
			}
			//如果都不满足 支付宝支付
			AlipayPay();
			
		}else {
			//支付宝
			AlipayPay();
		}
		
	}
	
	/**
	 * 支付优先级
	 * @return true 表示微信  false 表示 支付宝
	 */
	private boolean payPriority(){
		if(ApkUtils.isWeixinAvilible(this)){
			return true;
		}
		return false;
	}
	
	
}