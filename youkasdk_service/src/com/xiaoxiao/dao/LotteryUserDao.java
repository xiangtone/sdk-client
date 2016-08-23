package com.xiaoxiao.dao;

import org.apache.ibatis.annotations.Param;

import com.xiaoxiao.entity.LotteryUser;

public interface LotteryUserDao {

	/**
	 * @param uid
	 * @return
	 */
	public LotteryUser findByUid(@Param("uid")String uid);
	/**
	 * @param uid
	 * @param lottery_activitie
	 * @return
	 */
	public LotteryUser findByUidActivitie(@Param("uid")String uid,@Param("lottery_activitie")String lottery_activitie);
	
	
	/**
	 * @param user
	 */
	public void addUser(LotteryUser user);
	
	
	
	
}
