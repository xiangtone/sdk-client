package com.xiaoxiao.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.xiaoxiao.dao.LotteryActivitiesDao;
import com.xiaoxiao.entity.LotteryActivities;

public class LotteryActivitiesDaoImpl  implements LotteryActivitiesDao {

	@Override
	public List<LotteryActivities> getLotteryActivities(String appKey) {
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		List<LotteryActivities> adBeans = null;
		try {
			adBeans = sqlSession.selectList("getLotteryActivities", appKey);
		} finally {
			sqlSession.close();
		}
		return adBeans;
		
	}
	
	

	@Override
	public LotteryActivities findByMoneyLotteryActivities(String appKey, int activeCondition) {
		
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		LotteryActivities bean = null;
		try {
			HashMap<String, Object> parameter = new HashMap<>();
			parameter.put("appKey", appKey);
			parameter.put("activeCondition", activeCondition);
			bean = sqlSession.selectOne("findByMoneyLotteryActivities", parameter);
		} finally {
			sqlSession.close();
		}
		return bean;
	}

}
