package com.core_sur.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.core_sur.tools.HttpClientUtils;
import com.core_sur.tools.LruCache;
import com.core_sur.tools.MD5;
import com.core_sur.tools.StreamUtils;

public class ImagerManager {
	static LruCache<String, Bitmap> bitmaps;
	static Context c;
	private static ImagerManager imagerManager;
	public static ImagerManager getInstance(Context context) {
		c = context;
		if (imagerManager == null) {
			imagerManager = new ImagerManager();
		}
		return imagerManager;
	}
	private ImagerManager() {
		int memClass = ((ActivityManager) c
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		memClass = memClass > 32 ? 32 : memClass;
		final int cacheSize = 1024 * 1024 * memClass / 7;
		bitmaps = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}
	/**
	 * 
	 * 同步下载图片 请勿在UI主线程下载
	 * 
	 * @param url
	 * @return 图片
	 */
	public Bitmap loadImage(String url) {
		if(url==null){
			return null;
		}
		Bitmap bitmap = get(url);
		if (bitmap != null) {
			return bitmap;
		}
		bitmap = nativeLoadImage(url);
		if (bitmap != null) {
			bitmaps.put(url, bitmap);
		}
		bitmap = get(url);
		if (bitmap != null) {
			return bitmap;
		}
		writeNativeFile(url);
		bitmap = nativeLoadImage(url);
		if (bitmap != null) {
			bitmaps.put(url, bitmap);
		}
		bitmap = get(url);
		if (bitmap != null) {
			return bitmap;
		}
		return null;
	}
	public Bitmap get(String url) {
		return bitmaps.get(url);
	}
	public boolean put(String url, Bitmap bitmap) {
		if (bitmap == null) {
			return false;
		}
		bitmaps.put(url, bitmap);
		return true;
	}
	public static void writeNativeFile(String url) {
		HttpClientUtils httpClientUtils = new HttpClientUtils();
		InputStream in = httpClientUtils.getInputStream(url,true);
		File cacheDir = c.getCacheDir();
		File cacheFile = new File(cacheDir, MD5.Md5(url)); // 缓存到本地
		StreamUtils.StreamToFile(in, cacheFile);
	}
	private static Bitmap readBitMap(Context context, File file) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inSampleSize=2;
		opt.inInputShareable = true;
		try {
			return BitmapFactory.decodeStream(new FileInputStream(file), null,
					opt);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * 本地拿图片资源
	 * 
	 * @param url
	 *            Url特征码 [未加密]
	 * @return
	 */
	public Bitmap nativeLoadImage(String url) {
		File cacheDir = c.getCacheDir();
		File urlFile = new File(cacheDir, MD5.Md5(url));
		boolean exists = urlFile.exists();
		if (exists) {
			Bitmap bitmap = readBitMap(c, urlFile);
			return bitmap;
		}
		return null;

	}
}
