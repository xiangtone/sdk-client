package com.youka.sdk.dialog;

import java.util.ArrayList;
import java.util.List;

import com.youka.sdk.adapter.AbcAdapter;
import com.youka.sdk.adapter.MyBaseAdapter;
import com.youka.sdk.adapter.ViewHolder;
import com.youka.sdk.entry.LotteryBean;
import com.youka.sdk.lottery.LotteyCtrl;
import com.youka.sdk.lottery.LotteyCtrl.ILotteyCtrl;
import com.youka.sdk.utils.AssetsUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 我的彩票
 * @author zgt
 *
 */
public class MyLotteyDialog extends BaseDialog {

	private LotteyCtrl lotteyCtrl;

	private String uid;
	
	private View rootView;
	private ArrayList<LotteryBean> lotteryBeans;
	
	private ListView listView;
	
	//private MyBaseAdapter<LotteryBean> adapter;
	private AbcAdapter<LotteryBean> adapter;
	
	private AssetsUtils assetsUtils;
	

	public MyLotteyDialog(String uid, Context context,ArrayList<LotteryBean> lotteryBeans) {
		super(context);
		this.uid = uid;
		lotteyCtrl = new LotteyCtrl(context);
		
		this.lotteryBeans = lotteryBeans;//new ArrayList<LotteryBean>();
		assetsUtils = new AssetsUtils((Activity) context);
	}

	@Override
	protected void onCreate() {
		//rootView = LayoutInflater.from(context).inflate(R.layout.dialog_lottery, null);
		rootView = assetsUtils.findViewByFileName("dialog_lottery");
		super.setContentView(rootView);
		iniView();
		iniData();
	}

	private void iniView() {
		
		
		listView = (ListView) rootView.findViewWithTag("lottery_dialog_list");
		

		rootView.findViewWithTag("lottery_dialog_btn").setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	private void iniData() {
		
//      adapter = new MyBaseAdapter<LotteryBean>(context, lotteryBeans) {
//			@SuppressLint("ViewHolder") @Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				//View item = LayoutInflater.from(context).inflate(R.layout.lotter_item, null);
//				View item =assetsUtils.findViewByFileName("lotter_item");
//				
//				final LotteryBean t =lotteryBeans.get(position);
//				
//				
//				TextView txt_01 = (TextView) item.findViewWithTag("txt_01");
//				txt_01.setText(t.getExchangeCode());
//				TextView txt_02 = (TextView) item.findViewWithTag("txt_02");
//				txt_02.setText(t.getPasswordCode());
//				
//				TextView xl_text = (TextView) item.findViewWithTag("xl_text");
//				xl_text.setText("序列号"+t.getTag()+":");
//				
//				TextView yzm_txt = (TextView) item.findViewWithTag("yzm_txt");
//				yzm_txt.setText("验证码"+t.getTag()+":");
//				
//				
//				TextView xlh = (TextView) item.findViewWithTag("txt_011");
//				TextView yzm =(TextView) item.findViewWithTag("txt_022");
//				
//				xlh.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						copyCode(t.getExchangeCode(), "序列号拷贝成功");
//					}
//				});
//				
//				
//				yzm.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						copyCode(t.getPasswordCode(), "验证码拷贝成功");
//					}
//				});
//				
//				
//				
//				
//				return item;
//			}
//		};
		
		
		adapter = new AbcAdapter<LotteryBean>(context,"lotter_item",lotteryBeans) {
			
			@Override
			public void convert(ViewHolder item, final LotteryBean t) {
				TextView txt_01 = (TextView) item.findViewWithTag("txt_01");
				txt_01.setText(t.getExchangeCode());
				TextView txt_02 = (TextView) item.findViewWithTag("txt_02");
				txt_02.setText(t.getPasswordCode());
				
				TextView xl_text = (TextView) item.findViewWithTag("xl_text");
				xl_text.setText("序列号"+t.getTag()+":");
				
				TextView yzm_txt = (TextView) item.findViewWithTag("yzm_txt");
				yzm_txt.setText("验证码"+t.getTag()+":");
				
				
				TextView xlh = (TextView) item.findViewWithTag("txt_011");
				TextView yzm =(TextView) item.findViewWithTag("txt_022");
				
				xlh.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						copyCode(t.getExchangeCode(), "序列号拷贝成功");
					}
				});
				
				
				yzm.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						copyCode(t.getPasswordCode(), "验证码拷贝成功");
					}
				});
				
			}
		};
		
		
		
		
		listView.setAdapter(adapter);
		
//		lotteyCtrl.getMyLotterys(uid, new ILotteyCtrl() {
//			public void lotterys(List<LotteryBean> list) {
//				if(list!=null){
//					for (int i = 0; i <list.size(); i++) {
//						LotteryBean bean = list.get(i);
//						bean.setTag((i+1));
//						lotteryBeans.add(bean);
//					}
//					//lotteryBeans.addAll(list);
//					adapter.notifyDataSetChanged();
//				}
//			}
//		});
		
		
		
		
	}
	
	
	
	
	@SuppressLint("NewApi") 
	private void copyCode(String data,String info) {
		ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("simple text",data);
		clipboard.setPrimaryClip(clip);
		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
	}

}
