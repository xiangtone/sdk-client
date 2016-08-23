package com.youka.sdk.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.youka.sdk.ICallBack;
import com.youka.sdk.adapter.AbcAdapter;
import com.youka.sdk.adapter.ViewHolder;
import com.youka.sdk.entry.LotteryActivities;
import com.youka.sdk.lottery.LotteyCtrl;
import com.youka.sdk.lottery.LotteyCtrl.ILotteyCtrl;
import com.youka.sdk.utils.AssetsUtils;

/**
 * 彩票活动的dialog
 * 
 * @author zgt
 * 
 */
public class LotteryActivitiesDialog extends BaseDialog {

	private View rootView;
	private ListView listView;
	private ArrayList<LotteryActivities> list;
	
	private AssetsUtils assetsUtils;
	

	private ICallBack callback;
	
	public LotteryActivitiesDialog(Context context,ArrayList<LotteryActivities> list,ICallBack callback) {
		super(context);
		assetsUtils = new AssetsUtils((Activity) context);
		this.callback=callback;
		this.list = list;
	}

	@Override
	protected void onCreate() {
//		rootView = LayoutInflater.from(context).inflate(
//				R.layout.dialog_lottery_activities, null);
		
		rootView = assetsUtils.findViewByFileName("dialog_lottery_activities");
		
		super.setContentView(rootView);
		initView();
		initData();
	}

	private void initView() {
		//list = new ArrayList<LotteryActivities>();
		listView = (ListView) rootView.findViewWithTag("activities_listview");
		
		
		 rootView.findViewWithTag("activities_ok_btn").setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				callback.click();
				dismiss();
			}
		});

	}

	private void initData() {
		
		
//		final MyBaseAdapter	adapter = new MyBaseAdapter<LotteryActivities>(context, list) {
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				final View item = LayoutInflater.from(context).inflate(R.layout.dialog_lottery_activities_item, null);
//				
//				TextView textView = (TextView) item.findViewWithTag("lottery_txt_info");
//				//textView.setText(list.get(position).getActiveType()+":"+list.get(position).getActiveCondition());
//				
//				 LotteryActivities t = list.get(position);
//				
//				
//				if("first".equals(t.getActiveType())){
//					StringBuilder builder = new StringBuilder();
//					int money= t.getActiveCondition();
//					float moneyF = 0f;
//					if(money!=0){
//						moneyF = money/100.0f;			
//					}
//					builder.append("首次充值"+moneyF+"元 送"+t.getReward()+"彩票");
//					
//					textView.setText(builder.toString());
//					
//				}
//				
//				if("singleOrderOver".equals(t.getActiveType())){
//					StringBuilder builder = new StringBuilder();
//					int money= t.getActiveCondition();
//					float moneyF = 0f;
//					if(money!=0){
//						moneyF = money/100.0f;			
//					}
//					builder.append("单次充值"+moneyF+"元 送"+t.getReward()+"彩票");
//					
//					textView.setText(builder.toString());
//				}
//				
//				
//				return item;
//			}
//		};
		
		
		
		final AbcAdapter adapter = new AbcAdapter<LotteryActivities>(context, "dialog_lottery_activities_item", list) {
			
			@Override
			public void convert(ViewHolder holder, LotteryActivities t) {
				
				if("first".equals(t.getActiveType())){
					StringBuilder builder = new StringBuilder();
					int money= t.getActiveCondition();
					float moneyF = 0f;
					if(money!=0){
						moneyF = money/100.0f;			
					}
					builder.append("首次充值"+moneyF+"元 送"+t.getReward()+"注彩票");
					
					holder.setText("lottery_txt_info", builder.toString());
					
				}
				
				if("singleOrderOver".equals(t.getActiveType())){
					StringBuilder builder = new StringBuilder();
					int money= t.getActiveCondition();
					float moneyF = 0f;
					if(money!=0){
						moneyF = money/100.0f;			
					}
					builder.append("单次充值"+moneyF+"元 送"+t.getReward()+"注彩票");
					
					holder.setText("lottery_txt_info", builder.toString());
				}
				//holder.setText("lottery_txt_info", t.getActiveType()+":"+t.getActiveCondition());
			}
		};
		listView.setAdapter(adapter);
		
		
//		ctrl.getActivities(new ILotteyCtrl() {
//			
//			@Override
//			public void activities(List<LotteryActivities> activities) {
//				callback.getDataSuccess();
//				if(activities!=null){
//					list.clear();
//					list.addAll(activities);
//				}
//				adapter.notifyDataSetChanged();
//			}
//		});
		
	
		
	}
	
	
	
	
	
	
	
	

}
