package com.core_sur.tools;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;

public class CActivityManager {
	public static boolean getRunningProgressIsThis(Context c) {
		return getRunningProgressContain(c, c.getPackageName());
	}

	public static boolean getRunningProgressContain(Context c,
			String packageName) {
		ActivityManager ac = (ActivityManager) c
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> running = ac.getRunningTasks(15);
		for (RunningTaskInfo run : running) {
			if (packageName == null) {
				return false;
			}
			String runPackageName = run.topActivity.getPackageName();
			if (packageName.equals(runPackageName)) {
				return true;
			}
		}
		return false;

	}
}
