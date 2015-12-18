package com.core_sur.bean;

import com.core_sur.tools.MessageObjcet;

public class AppendPointMessage extends MessageObjcet {
public AppendPointMessage(String adName,String adKey,String appKey,String channelKey,String AdVersion,String point,String imsi) {
	hs.put("AdName", adName);
	hs.put("Appkey",appKey);
	hs.put("AdKey",adKey);
	hs.put("ChannelKey",channelKey);
	hs.put("AdVersion",AdVersion);
	hs.put("Type",2);
	hs.put("GetPoint",point);
	hs.put("ImisUserId",imsi);
	hs.put("isEnCode", true);
}
}
