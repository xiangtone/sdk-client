package com.core_sur.interfaces;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;

public interface ProxyInterface {
	public void onCreate(Intent intent);
	public void onDestroy();
	public void setActivity(Activity activity);
	public boolean onKeyDown(int keyCode, KeyEvent event);
}
