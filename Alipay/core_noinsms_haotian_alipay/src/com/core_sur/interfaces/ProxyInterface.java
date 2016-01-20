package com.core_sur.interfaces;

import android.app.Activity;
import android.content.Intent;

public interface ProxyInterface {
	public void onCreate(Intent intent);
public void onDestroy();
public void setActivity(Activity activity);
}
