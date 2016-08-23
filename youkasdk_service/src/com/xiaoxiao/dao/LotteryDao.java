package com.xiaoxiao.dao;

import java.util.List;

import com.xiaoxiao.entity.LotteryBean;

public interface LotteryDao {
	
	/**
	 * 
	 * @param num
	 * @return
	 */
	public List<LotteryBean> obtainLottery(int num);
	/**
	 * 
	 * @param bean
	 */
	public void updateLottery(LotteryBean bean);
	
	/**
	 * @param receiveUserId
	 * @return
	 */
	public List<LotteryBean> myLotterys(String receiveUserId);
	

}
