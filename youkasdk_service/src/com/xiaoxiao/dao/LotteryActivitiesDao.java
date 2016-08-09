package com.xiaoxiao.dao;

import java.util.List;

import com.xiaoxiao.entity.LotteryActivities;
/**
 * 
 * @author zgt
 *
 */
public interface LotteryActivitiesDao {
	
	/**
	 * 
	 * @return
	 */
	public List<LotteryActivities> getLotteryActivities(String appKey);
	
	/**
	 * find lottery activitie to money
	 * @param money
	 * @return
	 */
	public LotteryActivities  findByMoneyLotteryActivities(String appKey,int activeCondition);
	
	

}
