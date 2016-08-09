package com.xiaoxiao.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.xiaoxiao.dao.LotteryDao;
import com.xiaoxiao.entity.LotteryBean;

public class LotteryDaoImpl  implements LotteryDao{

	@Override
	public List<LotteryBean> obtainLottery(int num) {
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		List<LotteryBean> list = null;
		try {
			list = sqlSession.selectList("obtainLottery",num);
		} finally {
			sqlSession.close();
		}
		return list;
	}

	@Override
	public void updateLottery(LotteryBean bean) {
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		try {
			sqlSession.update("updateLottery", bean);
			sqlSession.commit();
		} finally {
			sqlSession.close();
		}
		
	}

	@Override
	public List<LotteryBean> myLotterys(String receiveUserId) {
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		List<LotteryBean> list = null;
		try {
			list = sqlSession.selectList("myLotterys",receiveUserId);
		} finally {
			sqlSession.close();
		}
		return list;
	}

}
