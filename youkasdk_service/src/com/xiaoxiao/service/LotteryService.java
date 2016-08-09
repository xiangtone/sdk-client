package com.xiaoxiao.service;

import java.util.ArrayList;
import java.util.List;


import com.xiaoxiao.dao.LotteryActivitiesDao;
import com.xiaoxiao.dao.LotteryDao;
import com.xiaoxiao.dao.LotteryUserDao;
import com.xiaoxiao.dao.impl.LotteryActivitiesDaoImpl;
import com.xiaoxiao.dao.impl.LotteryDaoImpl;
import com.xiaoxiao.dao.impl.LotteryUserDaoImpl;
import com.xiaoxiao.entity.LotteryActivities;
import com.xiaoxiao.entity.LotteryBean;
import com.xiaoxiao.entity.LotteryUser;

public class LotteryService {
	
	private LotteryDao lotteryDao = new LotteryDaoImpl();
	
	private LotteryUserDao userDao = new LotteryUserDaoImpl();
	
	private LotteryActivitiesDao lotteryActivitiesDao = new LotteryActivitiesDaoImpl();
	
  
	/**
	 * send Lottery
	 * @param uid 
	 * @param lottery_activitie 
	 * @param num Lottery num
	 * @param money 
	 * @return
	 */
	public List<LotteryBean> obtainLottery(String appKey,int money,String uid) {
		
		List<LotteryBean> list = new ArrayList<>();
		
		List<LotteryActivities> lotteryActivities =  lotteryActivitiesDao.getLotteryActivities(appKey);
		for (LotteryActivities activitie : lotteryActivities) {
			
			if(money>=activitie.getActiveCondition()){
				String type = activitie.getActiveType();
				if("first".equals(type)){
					LotteryUser user = userDao.findByUidActivitie(uid, type);
					if(user!=null){
						continue;
					}else {
						userDao.addUser(new LotteryUser(uid, type));
						list.addAll(sendLottery(uid, activitie.getReward()));
					}
				}else {
					List<LotteryBean> lotteryBeans =sendLottery(uid, activitie.getReward());
					list.addAll(lotteryBeans);
					return list;
				}
			}
		}
		
		return list;
	}
	
	
	
	
	
public List<LotteryBean> obtainLottery2(String appKey,int money,String uid) {
		
		List<LotteryBean> list = new ArrayList<>();
		
		List<LotteryActivities> lotteryActivities =  lotteryActivitiesDao.getLotteryActivities(appKey);
		for (LotteryActivities activitie : lotteryActivities) {
			
			if(money>=activitie.getActiveCondition()){
				String type = activitie.getActiveType();
				if("first".equals(type)){
					LotteryUser user = userDao.findByUidActivitie(uid, type);
					if(user!=null){
						continue;
					}else {
						userDao.addUser(new LotteryUser(uid, type));
						list.addAll(sendLottery(uid, activitie.getReward()));
					}
				}else {
					List<LotteryBean> lotteryBeans =sendLottery(uid, activitie.getReward());
					list.addAll(lotteryBeans);
					return list;
				}
			}
		}
		
		return list;
	}
	
	
	
	
	/**
	 * my Lotterys
	 * @param uid
	 * @return
	 */
	public List<LotteryBean> getMyLotterys(String uid){
		return lotteryDao.myLotterys(uid);
	}
	

	
	/**
	 * 
	 * @param uid
	 * @param num
	 * @return
	 */
	private List<LotteryBean> sendLottery(String uid,int num){
		List<LotteryBean>  list = lotteryDao.obtainLottery(num);
		for (LotteryBean lotteryBean : list) {
			lotteryBean.setReceiveUserId(uid);
			lotteryBean.setReceiveTime(System.currentTimeMillis()+"");
			lotteryDao.updateLottery(lotteryBean);
		}
		return list;
	}
	
	
	
	

}
