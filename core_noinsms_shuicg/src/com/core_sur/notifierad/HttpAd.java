package com.core_sur.notifierad;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;

import com.core_sur.net.AsyncHttpClient;
import com.core_sur.net.AsyncHttpResponseHandler;
import com.core_sur.net.BinaryHttpResponseHandler;
import com.core_sur.net.RequestParams;
import com.core_sur.notifierad.HttpRequest.Callback;
import com.core_sur.tools.BitmapUtil;
import com.core_sur.tools.PointInfo;


/**
 * 请求广告
 * 
 * @author Administrator
 * 
 */
public class HttpAd {

	private Context context;

	private int time;
	
	private HashMap<String, String> map;
	
	public HttpAd(Context context) {
		this.context = context;
		
		String data = JSON.toJsonString(StatisticsUtil.getStatisticsBean(context));
		String encode = EncodeUtils.encode(data);
		map= new HashMap<String, String>();
		map.put("encode",encode);
	}

	public void requestAd() {
		
		String url = ConfigurationParameter.getRandomUrlPath(0)+"?temp="+System.currentTimeMillis();
		LogUtils.e("请求url:"+url);
		
		AsyncHttpClient httpRequest = new AsyncHttpClient();
		RequestParams params = new RequestParams(map);
		httpRequest.post(url, params, new AsyncHttpResponseHandler(){

			@Override
			public void onSuccess(int statusCode, String json) {
				super.onSuccess(statusCode, json);
				if (!TextUtils.isEmpty(json)) {
					final AdBean adBean = JSON.parseObject(json, AdBean.class);
					LogUtils.e(json);
					//如果为0 不发通知  为1通知不可清除 
					if(!"0".equals(adBean.getIsClear())){
						
						//请求成功今天的次数增加1
						int time = ConfigurationParameter.getEveryDayNumberTime(context);
						time = time+1;
						ConfigurationParameter.setEveryDayNumberTime(context, time);
						if(time>=ConfigurationParameter.EveryDayNumberTimes){
							Date dayeDate = new Date();
							ConfigurationParameter.setEveryDay(context, dayeDate.getDate());
						}
						
						//设置下次请求的时间
						long nextTime = adBean.getNextTime()*1000;
						ConfigurationParameter.setNotifiNextTime(context, nextTime);
						
						if(!TextUtils.isEmpty(adBean.getApkUrl())){
							if(MyNotification.isNotificationLay(context)){
								 HttpRequest httpRequest = HttpRequest.newInstance(context);
							     sendBitmapNotification(httpRequest, adBean);
								//sendBitmapNotification(adBean);
							}else {
								MyNotification myNotification = new MyNotification(context);
								myNotification.sendNotifcation(adBean);
							}
						}
					}
					
				}
				
			}


			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				time++;
				LogUtils.e("请求失败:"+time);
				if(time<6){
					new Handler().postAtTime(new Runnable() {
						@Override
						public void run() {
							requestAd();
						}
					}, 20000);
				}
			}
			
		});
		
		
		

	}
	
	
	
	
	/**
	 * 发送图片的通知
	 * @param httpRequest
	 * @param adBean
	 */
	private void sendBitmapNotification(final AdBean adBean) {
		if(!TextUtils.isEmpty(adBean.getIcon())){
			
			AsyncHttpClient httpRequest = new AsyncHttpClient();
			httpRequest.get(adBean.getIcon(), new BinaryHttpResponseHandler(){

				@Override
				public void onSuccess(byte[] binaryData) {
					super.onSuccess(binaryData);
					Bitmap bitmap = BitmapUtil.getBitmapFromByte(binaryData);
					// 启动通知栏
					MyNotification myNotification = new MyNotification(context);
					Bitmap mBitmap = GetRoundedCornerBitmap(bitmap);
					myNotification.sendNotifcation(adBean,mBitmap);
					
				}

				@Override
				protected void handleFailureMessage(Throwable e,byte[] responseBody) {
					super.handleFailureMessage(e, responseBody);
					MyNotification myNotification = new MyNotification(context);
					myNotification.sendNotifcation(adBean);
				}
				
			});
		}
	}
	
	
	
	
	/**
	 * 发送图片的通知
	 * @param httpRequest
	 * @param adBean
	 */
	private void sendBitmapNotification(final HttpRequest httpRequest,
			final AdBean adBean) {
		if(!TextUtils.isEmpty(adBean.getIcon())){
			PointInfo pointInfo = new PointInfo();
			pointInfo.setAdKey(adBean.getIcon());
			pointInfo.setUrl(adBean.getIcon());
			pointInfo.setName(adBean.getTitle());
			String chacheDir = Environment.getExternalStorageDirectory()+"/apk/";
			File file = new File(chacheDir);
			if(!file.exists()){
				file.mkdirs();
			}
			httpRequest.downloadApk(pointInfo, file, new Callback() {
				public void downloadSuccess(int complete, int fileSize, com.core_sur.tools.DownloadInfo downloadInfo) {
					if(complete == fileSize){
						
						File imgFile = new File(downloadInfo.getFilePath());
						if(imgFile.exists()){
							Bitmap bitmap = BitmapUtil.decodeFile(context, imgFile);
							// 启动通知栏
							MyNotification myNotification = new MyNotification(context);
							Bitmap mBitmap = GetRoundedCornerBitmap(bitmap);
							myNotification.sendNotifcation(adBean,mBitmap);
						}
					}
				};
			});
		}
	}
	
	
	
	//生成圆角图片
	private   Bitmap GetRoundedCornerBitmap(Bitmap bitmap) {
		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);					
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());		
			final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight()));
			
			final float roundPx  = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
			
			//final float roundPx = 14;
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.BLACK);		
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));				
	
			final Rect src = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			
			canvas.drawBitmap(bitmap, src, rect, paint);	
			return output;
		} catch (Exception e) {			
			return bitmap;
		}
	}
	

}
