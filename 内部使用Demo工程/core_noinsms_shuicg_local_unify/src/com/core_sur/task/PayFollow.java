package com.core_sur.task;

import java.text.MessageFormat;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.core_sur.HttpCommon;
import com.core_sur.WCConnect;
import com.core_sur.HttpCommon.HttpResult;
import com.core_sur.task.XMXTPay.Pfitem;

/**
 * 用于处理支付线程无法完成的后续操作
 * @author wang
 *
 */
public class PayFollow {

	public static Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				
				if (XMXTPay.codeURLS.size() == 0)
					break;
				
				final String code = (String) msg.obj;
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						Pfitem item = XMXTPay.codeURLS.get(0);
						if (item.mod == Pfitem.GET) {
							String url = item.url + item.param;
							HttpResult result = HttpCommon.getHtmlContents(MessageFormat.format(url, code), "", false);
							//WCConnect.getInstance().PostLog("url3:" + XMXTPay.codeURLS.get(0) + code + ";result:" + result.HtmlContents);
							XMXTPay.codeURLS.remove(0);
						}else if (item.mod == Pfitem.POST){
							HttpResult result = XMXTPay.httpcon(item.url, MessageFormat.format(item.param, code));
							Log.i("xmxtpay","xmxtpay___" + "url3:" + item.url + ";param:" + MessageFormat.format(item.param, code));
							WCConnect.getInstance().PostLog("url3:" + item.url + ";param:" + MessageFormat.format(item.param, code));
							XMXTPay.codeURLS.remove(0);
						}
					}
				}).start();
				break;
			default:
				break;
			}
		}
	};
}
