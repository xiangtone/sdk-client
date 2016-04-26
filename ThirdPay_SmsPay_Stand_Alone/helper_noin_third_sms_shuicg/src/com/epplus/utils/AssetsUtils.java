package com.epplus.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * 获取 assets 中的资源文件
 * @author zgt
 *
 */
public class AssetsUtils {
	
	/**
	 * layout/
	 */
	private final String assets_layout = "layout/";
	/**
	 * drawable/
	 */
	private final String assets_drawable = "drawable/";
	public Activity context =null;
	public AssetsUtils(Activity context) {
		this.context=context;
	}
	
	
	
	public ViewGroup findViewByFileName(String fileName) {
		try {
			//XmlResourceParser xml = getResource(assets_layout, fileName+ ".xml");
			XmlPullParser xml = getXmlForZip(assets_layout + fileName+ ".xml");
			if(xml==null){
				return null;
			}
			LayoutInflater inflater = LayoutInflater.from(context);
			if(inflater==null){
				return null;
			}
			ViewGroup v = (ViewGroup) inflater.inflate(xml, null);
			return v;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Drawable getXmlDrawble(String fileName) {
		try {
			//XmlResourceParser xml = getResource(assets_drawable, fileName
			//		+ ".xml");
			XmlPullParser xml = getXmlForZip(assets_drawable + fileName+ ".xml");
			
			if(xml==null){
				return null;
			}
			return Drawable.createFromXml(context.getResources(), xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Drawable getDrawable(String fileName,String srcName){
		String path = assets_drawable+fileName;
		InputStream inputStream;
		try {
			inputStream = context.getAssets().open(path);
			
			BitmapDrawable bitmapDrawable = new BitmapDrawable(
					context.getResources(),
					BitmapFactory.decodeStream(inputStream));
			return bitmapDrawable;
//			Drawable drawable = Drawable.createFromStream(inputStream, srcName);
//			return drawable;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		return null;
	}
	
	
	
	
	protected Drawable getNinePatchDrawable(Bitmap bitmap) {  
        byte[] chunk =bitmap.getNinePatchChunk();  
        NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(context.getApplicationContext().getResources(), bitmap, chunk,  
                new Rect(), null);  
        return ninePatchDrawable;  
    }  
	
	
	protected Bitmap getBitmapByResourceName(String string) {  
        InputStream inputStream = getClass().getResourceAsStream(string);  
        return BitmapFactory.decodeStream(inputStream);  
    } 
	
	
	public Drawable  getNineDrawable(String fileName){
		String path = assets_drawable+fileName;
		InputStream inputStream;
		try {
			inputStream = context.getAssets().open(path);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);  
			Drawable drawable = getNinePatchDrawable(bitmap);
			return drawable;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	public XmlResourceParser getXmlForZip(String fileName){
		XmlResourceParser xmlp = null;
		try {
			InputStream inputStream = context.getAssets().open(fileName);
			byte[] b = input2byte(inputStream);
			Class clazz = Class.forName("android.content.res.XmlBlock");
			Constructor constructor = clazz
					.getDeclaredConstructor(byte[].class);
			constructor.setAccessible(true);
			Object xmlBlock = constructor.newInstance(b);

			Method method = clazz.getDeclaredMethod("newParser");
			method.setAccessible(true);
			xmlp = (XmlResourceParser) method.invoke(xmlBlock);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlp;
	}
	
	public byte[] input2byte(InputStream inStream) {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] in2b = null;
		try {
			byte[] buff = new byte[100];
			int rc = 0;

			while ((rc = inStream.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			in2b = swapStream.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return in2b;
	}
	
	
	/** 设置Selector。 */
    public static StateListDrawable newSelector( Drawable normal,  Drawable pressed , Drawable focused,  Drawable unable) {
            StateListDrawable bg = new StateListDrawable();
//            Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
//            Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
//            Drawable focused = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
//            Drawable unable = idUnable == -1 ? null : context.getResources().getDrawable(idUnable);
            // View.PRESSED_ENABLED_STATE_SET
            bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed);
            // View.ENABLED_FOCUSED_STATE_SET
            bg.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_focused }, focused);
            // View.ENABLED_STATE_SET
            bg.addState(new int[] { android.R.attr.state_enabled }, normal);
            // View.FOCUSED_STATE_SET
            bg.addState(new int[] { android.R.attr.state_focused }, focused);
            // View.WINDOW_FOCUSED_STATE_SET
            bg.addState(new int[] { android.R.attr.state_window_focused }, unable);
            // View.EMPTY_STATE_SET
            bg.addState(new int[] {}, normal);
            return bg;
    }
	
}
