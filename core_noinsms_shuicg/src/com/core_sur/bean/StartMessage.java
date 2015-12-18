package com.core_sur.bean;

import com.core_sur.finals.ParamFinals;
import com.core_sur.tools.MessageObjcet;

public class StartMessage extends MessageObjcet {
	public StartMessage(String sdkVersion) {
		// TODO Auto-generated constructor stub
	super();
	hs.put("Type", ParamFinals.STATISTICAL_START);
	hs.put("SdkVersion", sdkVersion);
	}
	@Override
		public void put(String key, Object value) {
			// TODO Auto-generated method stub
			super.put(key, value);
		}
}
