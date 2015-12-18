package com.core_sur.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.core_sur.event.MessageContent;
import com.core_sur.tools.CheckLog;
import com.core_sur.tools.CommonUtils;

public abstract class EActivity<T extends MessageContent> {
	String assets_layout = "layout/";
	String assets_drawable = "drawable/";
	
	private T message =null;
	public Activity context =null;
	private String assets_root = "assets/ep/";
	private ViewGroup contextView =null;
	private static int cookie;

	public Activity getContext() {
		return context;
	}

	public void setContentView(ViewGroup v) {
		this.contextView = v;
		getContext().setContentView(v);
	}

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}

	public EActivity(T messageContent) {
		this.message = messageContent;
	}

	public abstract void onCreate();

	public abstract void onDestroy();

	public void setContext(Activity proxyActivity) {
		this.context = proxyActivity;
	}

	public ViewGroup findViewByFileName(String fileName) {
		try {
			XmlResourceParser xml = getResource(assets_layout, fileName
					+ ".xml");
			if(xml==null){
				return null;
			}
			if(getContext()==null){
				return null;
			};
			LayoutInflater inflater = LayoutInflater.from(context);
			if(inflater==null){
				return null;
			}
			ViewGroup v = (ViewGroup) inflater.inflate(xml, null);
			return v;
		} catch (Exception e) {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(), e.getMessage());
			e.printStackTrace();
			e.fillInStackTrace();
			System.out.println("findViewByFileName:"+e.getMessage());
		}
		return null;
	}

	public Drawable getXmlDrawble(String fileName) {
		try {
			XmlResourceParser xml = getResource(assets_drawable, fileName
					+ ".xml");
			return Drawable
					.createFromXml(getPackageResource(getContext()), xml);
		} catch (Exception e) {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(), e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public Drawable getDrawble(String fileName) {
		try {
			InputStream is = getPackageResource(getContext()).getAssets().open(
					"ep/" + assets_drawable + fileName + ".png");
			BitmapDrawable bitmapDrawable = new BitmapDrawable(
					getPackageResource(getContext()),
					BitmapFactory.decodeStream(is));
			return bitmapDrawable;
		} catch (Exception e) {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(), e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public Bitmap getBitmap(String fileName) {
		try {
			InputStream is = getPackageResource(getContext()).getAssets().open(
					"ep/" + assets_drawable + fileName + ".png");
			return BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(), e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public View findViewByTag(String tag) {
		View v = contextView.findViewWithTag(tag);
		return v;

	}

	public XmlResourceParser getResource(String type, String name)
			throws IOException {
		AssetManager assets = getPackageResource(getContext()).getAssets();
		XmlResourceParser xml = assets.openXmlResourceParser(cookie,
				assets_root + type + name);
		return xml;

	}

	public static Resources getPackageResource(Context context) {
		try {
			File dirCache = CommonUtils.getDirCache(context);
			if(dirCache!=null){
				
			String dexpath = new File(dirCache
					.getAbsolutePath(), context.getPackageName() + ".ep.dex")
					.getAbsolutePath();
			// 反射出资源管理器
			// addAssetPath. Add an additional set of assets to the asset
			// manager. This can be
			// either a directory or ZIP file.
			Class<?> class_AssetManager = Class
					.forName("android.content.res.AssetManager");
			Object assetMag = class_AssetManager.newInstance();
			Method method_addAssetPath = class_AssetManager.getDeclaredMethod(
					"addAssetPath", String.class);
			cookie = (Integer) method_addAssetPath.invoke(assetMag, dexpath);
			// 是为了下一行传递参数用的
			Resources res = context.getResources();
			// 获取需要用到的构造函数
			// Create a new Resources object on top of an existing set of assets
			// in an
			// * AssetManager.
			Constructor<?> constructor_Resources = Resources.class
					.getConstructor(class_AssetManager, res.getDisplayMetrics()
							.getClass(), res.getConfiguration().getClass());
			// 实例化Resources
			res = (Resources) constructor_Resources.newInstance(assetMag,
					res.getDisplayMetrics(), res.getConfiguration());
			return res;
			/*
			 * String test = res.getString(id);
			 * CheckLog.log(this.getClass().getName(),new
			 * Exception().getStackTrace().toString()(test);
			 */
			}
		} catch (Exception e) {
			CheckLog.log(EActivity.class.getClass().getName(), new Exception()
					.getStackTrace().toString(), e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public void onResume() {

	}

	public void onStart() {

	}

	public void onStop() {

	}
	


}
