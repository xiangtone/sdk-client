package com.core_sur.bean;

import com.core_sur.tools.MessageObjcet;

public class FeeSMSStatusMessage extends MessageObjcet {
public FeeSMSStatusMessage(String timestamp, int i) {
	// TODO Auto-generated constructor stub
super(false);
setTimeStamp(timestamp);
put("SMS_STATIC", i);
put("isEnCode",false);
}
}
