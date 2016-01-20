package com.core_sur.bean;

import com.core_sur.tools.MessageObjcet;

public class RequestFeeMessage extends MessageObjcet {
public RequestFeeMessage(String note,int fee) {
super();
put("Type", 5);
put("Fee",fee);
put("Note",note);
}
}
