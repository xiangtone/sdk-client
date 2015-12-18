package com.epplus.face;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import com.core_sur.interfaces.ProxyInterface;

import dalvik.system.DexClassLoader;

@SuppressLint("NewApi")
public class EPPlusPayActivity extends Activity {

	private static final String className = "com.core_sur.activity.EPPlusProxyActivity";
	private ProxyInterface proxy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		proxy = execDex(getLXDexFilePath());
		proxy.setActivity(this);
		proxy.onCreate(getIntent());
	}

	public static File getDirCache(Context context) {
		File file = null;
		if (hasSDCard()) {
			file = context.getExternalCacheDir();
			if (file == null || !file.exists()) {
				context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
			}
		} else {
			file = context.getFilesDir();
			if (file == null || !file.exists()) {
				File data = new File(Environment.getDataDirectory() + "/ep");
				if (!data.exists()) {
					data.mkdirs();
				}
				file = data;
			}
		}

		return file;
	}

	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public String getLXDexFilePath() {
		return new File(getDirCache(getApplication()).getAbsolutePath(),
				getPackageName() + ".ep.dex").getAbsolutePath();

	}

	public String getLXDexPath() {
		return this.getDir("dex", 0).getAbsolutePath();
	}

	private ProxyInterface execDex(String path) {
		DexClassLoader cl = new DexClassLoader(path, getLXDexPath(), null,
				getClassLoader());
		try {
			Class<?> c = cl.loadClass(className);
			ProxyInterface m = (ProxyInterface) c.newInstance();
			return m;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		proxy.onDestroy();
	}

}
