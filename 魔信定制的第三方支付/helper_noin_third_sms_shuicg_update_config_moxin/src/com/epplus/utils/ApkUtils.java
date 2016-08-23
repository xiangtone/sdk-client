package com.epplus.utils;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * apk ������
 * 
 * @author zgt
 * 
 */
public class ApkUtils {

	// �ж�΢���Ƿ�װ��
	public static boolean isWeixinAvilible(Context context) {
		final PackageManager packageManager = context.getPackageManager();// ��ȡpackagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// ��ȡ�����Ѱ�װ����İ���Ϣ
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * �ж�qq�Ƿ����
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isQQClientAvailable(Context context) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mobileqq")) {
					return true;
				}
			}
		}
		return false;
	}

}
