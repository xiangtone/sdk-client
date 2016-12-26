package com.core_sur.bean;

import com.core_sur.tools.MessageObjcet;

public class PayFeeMessage extends MessageObjcet {
public PayFeeMessage(String note,int fee,int isFreeSuccess,int errorCode) {
super();
put("Type", 6);
put("Fee",fee);
put("FeeType",isFreeSuccess);
put("Note",note);
put("ErrorCode",errorCode);
}
}
