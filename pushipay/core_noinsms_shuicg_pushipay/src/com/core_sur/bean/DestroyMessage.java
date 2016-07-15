package com.core_sur.bean;

import com.core_sur.finals.ParamFinals;
import com.core_sur.tools.MessageObjcet;

public class DestroyMessage extends MessageObjcet {
public DestroyMessage() {
	// TODO Auto-generated constructor stub
super();
hs.put("Type", ParamFinals.STATISTICAL_END);
}
@Override
	public void put(String key, Object value) {
		// TODO Auto-generated method stub
		super.put(key, value);
	}
}
