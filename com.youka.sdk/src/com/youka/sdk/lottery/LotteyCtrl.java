package com.youka.sdk.lottery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.youka.sdk.entry.LotteryActivities;
import com.youka.sdk.entry.LotteryBean;
import com.youka.sdk.utils.ConfigUtils;
import com.youka.sdk.utils.HttpUtils;
import com.youka.sdk.utils.IHttpResult;
import com.youka.sdk.utils.YKUrlUtil;

/**
 * 彩票控制器
 * @author zgt
 *
 */
public class LotteyCtrl {
	
	
	private Context context;
	
    public LotteyCtrl(Context context){
    	this.context=context;
    }
    
    /**
     * 获取彩票活动
     * @param iCtrl
     */
    public void getActivities(final ILotteyCtrl iCtrl){
    	
    	HashMap<String, String> map =  new HashMap<String, String>();
    	String appkey = ConfigUtils.getEp_APPKEY(context);
    	map.put("AppKey", appkey);
    	map.put("type", "activities");
    	HttpUtils.asyPost(YKUrlUtil.LotteryServlet,map, new IHttpResult() {
			@Override
			public void result(Object arg0) {
				if(arg0!=null){
					iCtrl.activities(parseActivities(arg0.toString()));
				}else {
					iCtrl.activities(null);
				}
			}
		});
    }
    
    private List<LotteryActivities> parseActivities(String json){
    	List<LotteryActivities> list = new ArrayList<LotteryActivities>();
    	try {
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				int id=object.getInt("id");
				String activeType = object.getString("activeType");
				int activeCondition = object.getInt("activeCondition");
				int reward = object.getInt("reward");
				int probability = object.getInt("probability");
				list.add(new LotteryActivities(id, activeType, activeCondition, reward, probability));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
    }
    
    
    
    //送彩票 =>兑换码   参数 uid 用户id 钱
    /**
     * 送彩票 =>兑换码   参数 uid 
     * @param uid  用户id 
     * @param money  钱
     * @param iCtrl 回调
     */
    public void getLottery(String uid,String money,final ILotteyCtrl iCtrl){
    	HashMap<String, String> map =  new HashMap<String, String>();
    	String appkey = ConfigUtils.getEp_APPKEY(context);
    	map.put("type", "sent");
    	map.put("uid", uid);
    	map.put("money", money);
    	map.put("AppKey", appkey);
    	HttpUtils.asyPost(YKUrlUtil.LotteryServlet,map, new IHttpResult() {
			@Override
			public void result(Object arg0) {
				if(arg0!=null){
					iCtrl.lotterys(parseLottery(arg0.toString()));
				}else {
					iCtrl.lotterys(null);
				}
			}
		});
    }
    
    /**
     * 我的彩票
     * @param uid
     * @param iCtrl
     */
    public void getMyLotterys(String uid,final ILotteyCtrl iCtrl){
    	HashMap<String, String> map =  new HashMap<String, String>();
    	map.put("uid", uid);
    	map.put("type", "mylotterys");
    	HttpUtils.asyPost(YKUrlUtil.LotteryServlet,map, new IHttpResult() {
			@Override
			public void result(Object arg0) {
				if(arg0!=null){
					iCtrl.lotterys(parseLottery(arg0.toString()));
				}else {
					iCtrl.lotterys(null);
				}
			}
		});
    }
    
    
    
    
    /**
     * 解析彩票
     * @param string
     * @return
     */
    private List<LotteryBean> parseLottery(String json) {
    	List<LotteryBean> list = new ArrayList<LotteryBean>();
    	try {
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				//[{"exchangeCode":"WJYX261207406125","passwordCode":"313855","expireTime":"2017-05-15","receiveUserId":"jj","receiveTime":"1466404039024"}]
				JSONObject object = array.getJSONObject(i);
				String exchangeCode = object.getString("exchangeCode");
				String passwordCode = object.getString("passwordCode");
				String expireTime = object.getString("expireTime");
				
				String receiveUserId = object.getString("receiveUserId");
				String receiveTime = object.getString("receiveTime");
				
				list.add(new LotteryBean(exchangeCode, passwordCode, expireTime));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
    
   
    
    
    
    
    
    
    
    public static class ILotteyCtrl{
    	/**
    	 * 获取活动
    	 * @param list
    	 */
    	public void activities(List<LotteryActivities> list){}
    	
    	/**
    	 * 获取彩票
    	 * @param list
    	 */
    	public void lotterys(List<LotteryBean> list){}
    }
    
    
   
    

}
