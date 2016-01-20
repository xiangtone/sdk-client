package com.core_sur.tools;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.core_sur.finals.ErrorFinals;
import com.core_sur.listener.AsynResponse;

public class CarryImpi {
	private Context m_context;

	public CarryImpi(Context context) {
		this.m_context = context;
	}

	public void onDestroy() {
	}

	public void sendData(String url, String jsonString) {
		getData(url, null, jsonString);
	}

	public void sendData(String url, String jsonString,
			AsynResponse asynResponse) {
		getData(url, asynResponse, jsonString);
	}

	public void sendMessage(String url, final MessageObjcet msgObjcet) {
		sendMessage(url, msgObjcet, null);
	}

	public void sendMessage(String url, final MessageObjcet msgObjcet,
			final AsynResponse asynResponse) {
		CheckLog.log(
				this.getClass().getName(),
				new Exception().getStackTrace()[new Exception().getStackTrace().length - 1]
						.toString(), msgObjcet.getJsonString());
		sendData(url, msgObjcet.getJsonString(), new AsynResponse() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7882271052919863108L;

			@Override
			public void receiveDataSuccess(String result) {
				Object object = msgObjcet.get("Type");
				if (object != null) {
					CheckLog.log(this.getClass().getName(),
							new Exception().getStackTrace()[new Exception()
									.getStackTrace().length - 1].toString(),
							object + ":" + "messageSimpleName"
									+ msgObjcet.getClass().getSimpleName()
									+ "入库完成");
				}
				if (asynResponse != null) {
					asynResponse.receiveDataSuccess((String) result);
				}
			}

			@Override
			public void receiveDataError(Integer result) {
				if (asynResponse != null) {
					asynResponse.receiveDataError((Integer) result);
				}
			}
		});
	}

	protected void getData(final String url, final AsynResponse asynResponse,
			final String value) {
		new M_Async<Void, Void, Object>(m_context) {
			@Override
			protected Object doInBackground(Void... params) {
				HttpClientUtils client = new HttpClientUtils();
				String msg = value;
				if (isGoodJson(value)) {
					// CheckLog.log("明文:"+msg);
					msg = Base64UTF.encode(msg);
					// CheckLog.log("密文:"+msg);
				}
				String messageString = client.getString(url, msg);
				if (messageString == null || "".equals(messageString)) {
					return ErrorFinals.NOT_ADCONTENT;
				}
				return messageString;
			}

			@Override
			protected void onPostExecute(Object result) {
				if (result instanceof String) {
					if (asynResponse != null) {
						asynResponse.receiveDataSuccess((String) result);
					}
				}
				if (result instanceof Integer) {
					if (asynResponse != null) {
						asynResponse.receiveDataError((Integer) result);
					}
				}
				super.onPostExecute(result);
			}
		}.execute();
	}

	public static boolean isBadJson(String json) {
		return !isGoodJson(json);
	}

	public static boolean isGoodJson(String json) {
		if (json == null) {
			return false;
		}
		if (json.length() >= 2) {
			try {
				return json.startsWith("{") && json.endsWith("}")
						&& new JSONObject(json).getBoolean("isEnCode");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
			}
			return false;
		}
		return false;
	}
}
