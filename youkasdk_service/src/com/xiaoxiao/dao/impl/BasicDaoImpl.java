package com.xiaoxiao.dao.impl;

import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public abstract class BasicDaoImpl {

	private static SqlSessionFactory sqlSessionFactory = null;

	static {
		String resource = "conf.xml";
		InputStream is = BasicDaoImpl.class.getClassLoader().getResourceAsStream(resource);
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
		
		System.err.println("resource");
		
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
	
	
	
	
	

}
