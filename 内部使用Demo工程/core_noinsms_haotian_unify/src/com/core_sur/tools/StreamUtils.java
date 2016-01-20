package com.core_sur.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class StreamUtils {

	public static String StreamtoString(InputStream in){
		if(in==null){
			return null;
		}
		int len;
		
		byte[] b = new byte[512];
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	try {
		while((len=in.read(b))!=-1){
			out.write(b, 0, len);
		}
		return out.toString();

	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally{
		try {
			out.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			out=null;
			in=null;
			e.printStackTrace();
		}
	}
	return null;
		
	}

	public static Bitmap StreamtoBitmap(InputStream in){
		if(in==null){
			return null;
		}
		Bitmap decodeStream = BitmapFactory.decodeStream(in);
		return decodeStream;
		
	}

	public static byte[] StreamtoBytes(InputStream in) {
		if(in==null){
			return null;
		}
		// TODO Auto-generated method stub
	int len;
		
		byte[] b = new byte[512];
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	try {
		while((len=in.read(b))!=-1){
			out.write(b, 0, len);
		}
		byte[] bytes = out.toByteArray();
		return bytes;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally{
		try {
			out.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			out=null;
			in=null;
			e.printStackTrace();
		}
	}
	return null;
	}
    public static void ByteToFile(byte[] b ,File filePath){
    if(filePath==null){
    	return;
    }
	FileOutputStream out = null;
	try {
		 out = new FileOutputStream(filePath);
		 out.write(b);
	}catch(Exception e){
		CheckLog.log(StreamUtils.class.getName(),new Exception().getStackTrace()[new Exception().getStackTrace().length-1].toString(),"EPCheck ByteToFile Error");
	}
	}
	public static void StreamToFile(InputStream in, File urlFile) {
		if(in==null){
			return ;
		}
		FileOutputStream out = null;
		try {
			 out = new FileOutputStream(urlFile);
			byte[] b= new byte[512];
			int len = 0;
			while ((len=in.read(b))!=-1) {
				out.write(b, 0, len);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					out=null;
				}
			}
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					in=null;
				}
			}
		}
		
		// TODO Auto-generated method stub
		
	}

	public static void StreamToJpg(InputStream in, File filePath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inSampleSize=2;
		Bitmap bitmap = BitmapFactory.decodeStream(in);
		
	}
	private   static   byte[] compressImage(Bitmap image)

	{
	     ByteArrayOutputStream baos =  new   ByteArrayOutputStream();

	     image.compress(Bitmap.CompressFormat.JPEG,  100 , baos); // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

	     int   options =  100 ;

	     while   (baos.toByteArray().length /  1024   >  100 )

	     {  // 循环判断如果压缩后图片是否大于100kb,大于继续压缩

	         baos.reset(); // 重置baos即清空baos

	         image.compress(Bitmap.CompressFormat.JPEG, options, baos); // 这里压缩options%，把压缩后的数据存放到baos中

	         options -=  10 ; // 每次都减少10

	     }
	     return   baos.toByteArray();

	}
}
