package com.core_sur.bean;

import com.core_sur.event.impl.PayCenterEvent;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.MessageObjcet;

public class PayStatusMessage extends MessageObjcet {
	
	/**
	 *  2X：申请某个网银计费（给10个值，不够在拓展，比如 21代表 支付宝  22代表微信）</br>
  	 *  31：网银支付取消</br>
  	 *  32：网银支付成功 </br>
  	 *  </br>
  	 *  21:申请易宝支付
  	 *  
	 * @param payStatus 
	 * @param timestamp 支付宝需要将此作为订单号，便于数据同步
	 */
	public PayStatusMessage(int payStatus,String timestamp) {
		// TODO Auto-generated constructor stub
		super();
		hs.put("Type", payStatus);
		if (timestamp != null && !timestamp.equals("")) {
			setTimeStamp(timestamp);
		}
		if (EPCoreManager.getInstance().fee != null && !EPCoreManager.getInstance().fee.equals("")) {
			hs.put("Fee", EPCoreManager.getInstance().fee);
		}
		if (EPCoreManager.getInstance().note != null && !EPCoreManager.getInstance().note.equals("")) {
			hs.put("Note", EPCoreManager.getInstance().note);
		}
	}

	@Override
	public void put(String key, Object value) {
		// TODO Auto-generated method stub
		super.put(key, value);
	}
}
