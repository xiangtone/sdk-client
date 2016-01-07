package com.core_sur.bean;

import com.core_sur.tools.MessageObjcet;

public class FeeParamMessage extends MessageObjcet
{
	public FeeParamMessage(int statusNumber, String statusMsg)
	{
		super();
		put("Type", 7);
		put("FeeStatusNumber", statusNumber);
		put("FeeStatusMsg", statusMsg);
	}
}
