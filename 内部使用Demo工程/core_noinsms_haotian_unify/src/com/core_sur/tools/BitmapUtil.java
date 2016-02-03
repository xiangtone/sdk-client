package com.core_sur.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;

public class BitmapUtil {
	/**
	 * 根据Uri获取图片
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
		
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	public static Bitmap decodeFile(Context context, File f) {
		// 获取屏幕密度
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return decodeFile(f, metrics.widthPixels, metrics.heightPixels);

	}

	public static Bitmap decodeFile(Context context, String absolutePath) {
		// 获取屏幕密度
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return decodeFile(new File(absolutePath), metrics.widthPixels,
				metrics.heightPixels);

	}

	public static Bitmap decode640File(Context context, String absolutePath) {
		return decodeFile(new File(absolutePath), 640, 640);

	}

	public static Bitmap decode320File(Context context, String absolutePath) {
		return decodeFile(new File(absolutePath), 320, 320);

	}

	/**
	 * 按比例大小压缩方法（根据路径获取图片并压缩）
	 * 
	 * @param f
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap decodeFile(File f, int width, int height) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = calculateInSampleSize(o, width, height);
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 质量压缩方法
	 * 
	 * @param image
	 * @param size
	 * @return
	 */
	public static Bitmap compressBmpFromBmp(Bitmap image, int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 100;
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos); // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset(); // 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos); // 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10; // 每次都减少10

		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray()); // 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null); // 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 从BitmapFactory.Options中获取图片的原始高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// 计算原始尺寸与显示尺寸的比例
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// 使用两个比例中较大的作为采样比，可以保证图片在两个方向上都满足需求
			inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;

			// 为了防止一些纵横比异常（很宽或者很长）的图片仍然会占用较大内存，增加
			// 对显示像素的限制
			final float totalPixels = width * height;

			// 如果以前面计算到的采样比采样得到的bitmap像素大于请求的像素的两倍，继续
			// 提高采样比
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	/**
	 * bitmap有损质量压缩
	 * 
	 * @param image
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public static Bitmap compressImage(Bitmap image, int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 10, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		int sca = sizeOf(image) / baos.toByteArray().length;
		while (baos.toByteArray().length * sca > size * 1024) { // 循环判断如果压缩后图片是否大于50kb,大于继续压缩
			options = options / 2;// 每次都减少原来的一半
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

		}
		Bitmap bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
				baos.toByteArray().length);

		// ByteArrayInputStream isBm = new
		// ByteArrayInputStream(baos.toByteArray());//
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
		// 把ByteArrayInputStream数据生成图片
		image.recycle();
		return bitmap;
	}

	/**
	 * 获取bitmap占用的字节数
	 * 
	 * @param data
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public static int sizeOf(Bitmap data) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
			return data.getRowBytes() * data.getHeight();
		} else {
			return data.getByteCount();
		}
	}

	/**
	 * bitmap转成二进制
	 * 
	 * @param bitmap
	 * @return
	 */
	public static byte[] getBitmapByte(Bitmap bitmap) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * 二进制转成bitmap
	 * 
	 * @param temp
	 * @return
	 */
	public static Bitmap getBitmapFromByte(byte[] temp) {
		if (temp != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			return bitmap;
		} else {
			return null;
		}
	}

	/**
	 * 将Drawable转化为Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 从bitmap中得到输入流
	 * 
	 * @param bitmap
	 * @return
	 */
	public static InputStream getInputStreamFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		return isBm;
	}

}
