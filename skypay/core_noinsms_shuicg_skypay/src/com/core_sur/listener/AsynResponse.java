package com.core_sur.listener;

import java.io.Serializable;

public abstract class AsynResponse implements Serializable {

	public abstract void receiveDataSuccess(String result);

	public abstract void receiveDataError(Integer result) ;

}
