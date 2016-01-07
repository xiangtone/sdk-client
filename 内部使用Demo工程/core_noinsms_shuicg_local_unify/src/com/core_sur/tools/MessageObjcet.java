package com.core_sur.tools;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.core_sur.publics.EPCoreManager;

public  class  MessageObjcet  implements Serializable{
protected HashMap<String, Object> hs= new HashMap<String, Object>();
private boolean inTimeStamp;
public MessageObjcet() {
	hs.put("DeviceId", EPCoreManager.getInfo().deviceId);
	hs.put("AppKey", EPCoreManager.getInfo().appKey);
	hs.put("ChannelKey", EPCoreManager.getInfo().channelKey);
	hs.put("Imsi",EPCoreManager.getInfo().imsi);
	hs.put("IpAddress",EPCoreManager.getInfo().ipAddress);
	hs.put("MobileType", EPCoreManager.getInfo().mobileType);
	hs.put("MobileSize",EPCoreManager.getInfo().mobileSize);
	hs.put("Version",EPCoreManager.getInfo().appVersion);
	hs.put("MobileVersion",EPCoreManager.getInfo().mobileVersion);
	hs.put("ConnectMethod",EPCoreManager.getInfo().connectMethod);
	hs.put("Network",EPCoreManager.getInfo().network);
	hs.put("isEnCode", true);
}
public MessageObjcet(boolean isSuper) {
}
public void setTimeStamp(String timestamp){
inTimeStamp=true;
hs.put("TimeStamp", timestamp);
}
public void put (String key,Object value){
	hs.put(key, value);
}
public String getJsonString(){
	if(!inTimeStamp){
		hs.put("TimeStamp", UUID.randomUUID().toString());
	}
	hs.put("Key",MD5.Md5(EPCoreManager.getInfo().appKey+","+EPCoreManager.getConfig().getAppId()+","+hs.get("TimeStamp")+","+"epServer"));
	JSONObject jsonObject = new JSONObject(hs);
	return jsonObject.toString();
}
public void putAll(Map<String, Object> content) {
	// TODO Auto-generated method stub
	hs.putAll(content);
}
public Object get(String key){
	return  hs.get(key);
	
}
}
