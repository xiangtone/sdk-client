package com.core_sur.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.Context;
import android.os.Handler;

import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.RevBean;
import com.core_sur.task.PayTaskManager;
import com.core_sur.tools.Log;

public class SmsSender extends BaseAction  {

	private final String[] msg;
	private int sendCount = 0;
	private int sendIndex = 0;
	private long LinkId = 0;
	private String tempOrderKey;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            句柄
	 * @param handler
	 *            回调句柄
	 * 
	 * @param msg
	 *            json里的数据，没有解析
	 * @param tempOrderKey
	 */
	public SmsSender(Context context, Handler handler, RevBean revBean,
			String tempOrderKey) {
		super(context, handler, revBean);
		// TODO Auto-generated constructor stub
		this.msg = revBean.getMsg().split(Config.splitStringLevel1);
		LinkId = revBean.getLinkid();
		sendIndex = 0;
		try {
			sendCount = Str2Int(this.msg[2]);
		} catch (Exception e) {
			if (com.core_sur.Config.IsDebug) {
				e.printStackTrace();
			}
		}
		this.tempOrderKey = tempOrderKey;
	}

	@Override
	public void run() {
		if (this.msg == null || this.msg.length == 0)
			return;

		int count = sendCount;

		// 保护机制，避免发送过多的短信
		if (count > 4) {
			count = 5;
		}
		while (sendIndex < count) {
			RevBean rb = new RevBean();
			rb.setCmdid(1003);
			rb.setDelayTime(0);
			rb.setLinkid(LinkId);
			try {
				PayTaskManager.getInstance().create(tempOrderKey)
						.sendSms(msg[0], msg[1]);
				WCConnect.getInstance().PostLog(
						"SMSSend:ok" + Config.splitStringLevel1 + msg[0]
								+ Config.splitStringLevel2
								+ URLDecoder.decode(msg[1], "utf-8")
								+ Config.splitStringLevel1 + "SendOK");
			} catch (Exception e) {
				try {
					WCConnect.getInstance()
							.PostLog(
									"SMSSend:Erro"
											+ Config.splitStringLevel1
											+ msg[0]
											+ Config.splitStringLevel2
											+ URLDecoder
													.decode(msg[1], "utf-8")
											+ Config.splitStringLevel1
											+ e.getMessage());
				} catch (UnsupportedEncodingException e1) {
				}
			}
			sendIndex++;
			try {
				sleep(300 * 1);
			} catch (InterruptedException e) {
			}
		}

	}


}
