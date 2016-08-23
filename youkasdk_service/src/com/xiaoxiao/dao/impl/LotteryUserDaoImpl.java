package com.xiaoxiao.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.xiaoxiao.dao.LotteryUserDao;
import com.xiaoxiao.entity.LotteryBean;
import com.xiaoxiao.entity.LotteryUser;

public class LotteryUserDaoImpl  implements LotteryUserDao {

	@Override
	public LotteryUser findByUid(String uid) {
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		LotteryUser user = null;
		try {
			user = sqlSession.selectOne("findByUid", uid);
		} finally {
			sqlSession.close();
		}
		return user;
	}

	@Override
	public LotteryUser findByUidActivitie(String uid, String lottery_activitie) {
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		LotteryUser user = null;
		try {
			
			 Map<String, String> parameterMap = new HashMap<String,  String>();
			 parameterMap.put("uid", uid);
			 parameterMap.put("lottery_activitie", lottery_activitie);
			
			user = sqlSession.selectOne("findByUidActivitie", parameterMap);
		} finally {
			sqlSession.close();
		}
		return user;
	}

	@Override
	public void addUser(LotteryUser user) {
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		try {
		  int n =  sqlSession.insert("addUser", user);
		  sqlSession.commit();
		} finally {
			sqlSession.close();
		}
		
	}

}
