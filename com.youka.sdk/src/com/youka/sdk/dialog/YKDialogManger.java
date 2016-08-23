package com.youka.sdk.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Toast;

import com.youka.sdk.ICallBack;
import com.youka.sdk.entry.LotteryActivities;
import com.youka.sdk.entry.LotteryBean;
import com.youka.sdk.lottery.LotteyCtrl;
import com.youka.sdk.lottery.LotteyCtrl.ILotteyCtrl;

/**
 * dialog 管理者
 * @author zgt
 *
 */
public class YKDialogManger {
	
	private ArrayList<Dialog> dialogs = new ArrayList<Dialog>();
	private Activity context;
	
	private static YKDialogManger manger;
	
	public static YKDialogManger instances(Activity context){
		if(manger==null){
			manger = new YKDialogManger();
		}
		manger.context=context;
		return manger;
	}
	
	
	
	
	
	/**
	 * 获取彩票活动
	 * @param iCtrl
	 */
	public void activities(final ICallBack callBack){
		LotteyCtrl ctrl = new LotteyCtrl(context);
		ctrl.getActivities(new ILotteyCtrl() {
			
			@Override
			public void activities(List<LotteryActivities> activities) {
				
				if(activities!=null&&activities.size()>0){
					callBack.getDataSuccess();
					LotteryActivitiesDialog dialog = new LotteryActivitiesDialog(context,(ArrayList<LotteryActivities>)activities,callBack);
					dialog.show();
					dialogs.add(dialog);
				}else {
					if(activities!=null){
						Toast.makeText(context, "暂无活动", Toast.LENGTH_SHORT).show();
					}else {
						Toast.makeText(context, "没有获取到数据", Toast.LENGTH_SHORT).show();
					}
				}
				
			}
		});
		
		
		
		
	}
	
	
	
	
	/**
	 * 送彩票
	 * @param uid
	 * @param money
	 */
	public void sendLottery(final String uid,final String money){
		
		LotteyCtrl ctrl = new LotteyCtrl(context);
		ctrl.getLottery(uid, money, new ILotteyCtrl() {
			public void lotterys(List<LotteryBean> list) {
				if(list!=null&&list.size()>0){
					ArrayList<LotteryBean> lotteryBeans = new ArrayList<LotteryBean>();
					for (int i = 0; i <list.size(); i++) {
						LotteryBean bean = list.get(i);
						bean.setTag((i+1));
						lotteryBeans.add(bean);
					}
					
					SendLotteyDialog dialog = new SendLotteyDialog(uid, money, context,lotteryBeans);
					dialog.show();
					dialogs.add(dialog);
				}else {
					
				}
			}
		});
		
		
		
		
		
	}
	
	
	/**
	 * 我的彩票
	 * @param uid
	 */
	public void myLottery(final String uid){
		LotteyCtrl ctrl = new LotteyCtrl(context);
		ctrl.getMyLotterys(uid, new ILotteyCtrl() {
			public void lotterys(List<LotteryBean> list) {
				if(list!=null){
					ArrayList<LotteryBean> lotteryBeans = new ArrayList<LotteryBean>();
					for (int i = 0; i <list.size(); i++) {
						LotteryBean bean = list.get(i);
						bean.setTag((i+1));
						lotteryBeans.add(bean);
					}

					MyLotteyDialog dialog = new MyLotteyDialog(uid, context,lotteryBeans);
					dialog.show();
					dialogs.add(dialog);
				}
			}
		});
		
		
		
	}
	
	

}
