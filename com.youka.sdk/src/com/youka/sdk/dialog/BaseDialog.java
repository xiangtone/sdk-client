package com.youka.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

public abstract class BaseDialog extends Dialog{


	protected Context context;
	
	public BaseDialog(Context context) {
		super(context);
		this.context =context;
		setCancelable(false);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		onCreate();
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
	}
	
	
	protected abstract void onCreate();
	

}
