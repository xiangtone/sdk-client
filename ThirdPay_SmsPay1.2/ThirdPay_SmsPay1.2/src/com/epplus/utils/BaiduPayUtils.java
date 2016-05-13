package com.epplus.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

//import com.baidu.android.pay.PayCallBack;
//import com.baidu.wallet.api.BaiduWallet;
//import com.baidu.wallet.api.Constants;
//import com.baidu.wallet.core.DebugConfig;

/**
 * �ٶ�֧��
 * 
 * @author zgt
 * 
 */
public class BaiduPayUtils {

	ProgressDialog progressDialog;
	private Activity activity;
	// ��̨֪ͨ��ַ
	private String returnUrl;
	// ��Ʒ��ַ
	private String goodsUrl;

	private static final int CREATE_ORDER = 1;

	private Handler mDopayHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (progressDialog != null) {
				if (progressDialog.isShowing()) {
					progressDialog.cancel();
				}
			}
			switch (msg.what) {
			case CREATE_ORDER:
				if (msg.obj != null) {
					if (msg.obj instanceof String) {
						String str = (String) msg.obj;
						if (!TextUtils.isEmpty(str)
								&& str.contains("service_code")) {
							realPay(str);
						} else {
							Toast.makeText(activity, "��������ʧ��",Toast.LENGTH_SHORT).show();
						}
					}
				}
				break;

			default:
				break;
			}
		}
	};

	
	  class PayCallBackPorx implements InvocationHandler{

		 
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			
			String methodName = method.getName();
			if("onPayResult".equals(methodName)){
				int stateCode = (Integer) args[0];
				String payDesc = (String) args[1];
				handlepayResult(stateCode, payDesc);
				
				return null;
			}
			
			if("isHideLoadingDialog".equals(methodName)){
				return true;
			}
			
			return null;
		}
		 
	 }
	
	
	private void realPay(String orderInfo) {
		//Log.e("zgt", "orderInfo:"+orderInfo);
		/**
		 * orderInfo�Ƕ�����Ϣ������������server�����ɲ����ǩ����ȷ����ȫ�� ����ǩ����������ս����ĵ��е� ǩ������ �½�
		 */
//		BaiduWallet.getInstance().doPay(activity, orderInfo, new PayCallBack() {
//			public void onPayResult(int stateCode, String payDesc) {
//				handlepayResult(stateCode, payDesc);
//			}
//
//			public boolean isHideLoadingDialog() {
//				return true;
//			}
//		});
		
		try {
			Class clazz = Class.forName("com.baidu.wallet.api.BaiduWallet");
			Method instance = clazz.getMethod("getInstance");
			Object BaiduWallet = instance.invoke(null);
            
			Class payClass = Class.forName("com.baidu.android.pay.PayCallBack");
			Method doPay = clazz.getMethod("doPay", Context.class, String.class,payClass);
			
			Class[] interfaces = {payClass};
			Object object=Proxy.newProxyInstance(PayCallBackPorx.class.getClassLoader(),  
	                interfaces, new PayCallBackPorx());
			
			doPay.invoke(BaiduWallet, activity,orderInfo,object);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

	/**
	 * ֧���������
	 * 
	 * @param stateCode
	 * @param payDesc
	 */
	private void handlepayResult(int stateCode, String payDesc) {

		switch (stateCode) {
		//case Constants.PAY_STATUS_SUCCESS:// ��Ҫ���������֤֧�����
		case 0:
			//Toast.makeText(activity, "֧���ɹ�\n " + payDesc, Toast.LENGTH_SHORT).show();
			baiduHandler.baiduPaySuccess(payDesc,0+"");
			break;
		//case Constants.PAY_STATUS_PAYING:// ��Ҫ���������֤֧�����
		case 1:
			//Toast.makeText(activity, "֧��������\n " + payDesc, Toast.LENGTH_SHORT).show();
			baiduHandler.baiduPayFailed(payDesc, 1+"");
			break;
		//case Constants.PAY_STATUS_CANCEL:
		case 2:
			//Toast.makeText(activity, "֧��ȡ��\n " + payDesc, Toast.LENGTH_SHORT).show();
			baiduHandler.baiduPayCancel(payDesc, 2+"");
			break;
		default:
			break;
		}

		// TODO �����������Լ��ķ����������ѯ֧����������������ѯ֧�����������ĵ��е� ��������֪֧ͨ��������½�

	}
	
	private BaiduHandler baiduHandler;

	public BaiduPayUtils(Activity activity,String OrderIdSelf,String OrderIdCp,BaiduHandler baiduHandler) {
		this.activity = activity;
        this.baiduHandler = baiduHandler;
        
       // String baseUrl =ConfigUtils.Notify_Url_Baidu; //"http://thirdpay-webhook.n8wan.com:29141/thirdpayCountServlet";
		this.returnUrl = URLUtils.notifyUrlBaidu(activity,OrderIdSelf,OrderIdCp);//baseUrl+ConfigUtils.getNotifyBaiduPramData(activity);//+"?"+ConfigUtils.xx_notifyData+"="+ConfigUtils.getNotifyJsonData(activity,ConfigUtils.BAIDU);
		//returnUrl = "http://db-testing-eb07.db01.baidu.com:8666/success.html";
		goodsUrl = "";
		
		
		try {
			Class clazz = Class.forName("com.baidu.wallet.api.BaiduWallet");
			Method instance = clazz.getMethod("getInstance");
			Object BaiduWallet = instance.invoke(null);

			Method getLoginToken = clazz.getMethod("getLoginToken");
			String token = (String) getLoginToken.invoke(BaiduWallet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//String token = BaiduWallet.getInstance().getLoginToken();

	}

	public void pay(String goods_name, String goodsDesc, String price) {
		try {
			final List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("goods_name", goods_name));
			params.add(new BasicNameValuePair("total_amount", price));//
			params.add(new BasicNameValuePair("goods_desc", goodsDesc)); //
			params.add(new BasicNameValuePair("goods_url", goodsUrl)); //
			params.add(new BasicNameValuePair("return_url", returnUrl));//
			params.add(new BasicNameValuePair("unit_amount", "")); //
			params.add(new BasicNameValuePair("unit_count", "")); //
			params.add(new BasicNameValuePair("transport_amount", ""));//
			params.add(new BasicNameValuePair("page_url", "")); //
			params.add(new BasicNameValuePair("buyer_sp_username", "")); //
			params.add(new BasicNameValuePair("pay_type", "2")); //
			params.add(new BasicNameValuePair("extra", ""));

			Class DebugConfigClass = Class.forName("com.baidu.wallet.core.DebugConfig");
			Method Instance = DebugConfigClass.getMethod("getInstance", Context.class);
			Object DebugConfigobj = Instance.invoke(null, activity);
			
			Method getEnvironment = DebugConfigClass.getMethod("getEnvironment");
			String env = (String) getEnvironment.invoke(DebugConfigobj);
			//String env = DebugConfig.getInstance(activity).getEnvironment();
			if (!TextUtils.isEmpty(env)) {
				//if (env.equals(DebugConfig.ENVIRONMENT_QA)) {
				if (env.equals("QA")) {
					params.add(new BasicNameValuePair("environment", "qa"));
				//} else if (env.equals(DebugConfig.ENVIRONMENT_RD)) {
				} else if (env.equals("RD")) {
					params.add(new BasicNameValuePair("environment", "rd"));
				} else {
					params.add(new BasicNameValuePair("environment", "online"));
				}
			} else {
				params.add(new BasicNameValuePair("environment", "online"));
			}

			// if (!TextUtils.isEmpty(profitSolution)) {
			// params.add(new BasicNameValuePair("profit_solution",
			// profitSolution));
			// }

			// final String url = "http://bdwallet.duapp.com/createorder/pay_wap.php";
			//final String url = "http://192.168.0.111:8080/bdpayrefund/BaiduPayService";
			//final String url = "http://192.168.0.111:8080/baidupay/BaiduPayService";
			//final String url = "http://192.168.0.101:8080/BaiduPay_Server/BaiduPayService";
			final String url = "http://baidupay-server.n8wan.com:29141/BaiduPayService";

			if (progressDialog == null) {
				progressDialog = new ProgressDialog(activity);
			}

			progressDialog.setMessage("���ڴ�������...");
			progressDialog.setCancelable(false);
			progressDialog.show();

			new Thread(new Runnable() {

				@Override
				public void run() {
					String orderinfo = doPost(url, params);
					// Log.i(TAG, "orderinfo=" + orderinfo);
					mDopayHandler.sendMessage(mDopayHandler.obtainMessage(
							CREATE_ORDER, orderinfo));
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �ͻ��˷��Ͳ�����server����ǩ����������������ʱ��Ҫ�������ǩ����ɺ󷵸��ͻ���orderInfo��Ȼ��ͻ��˵���doPay
	 * 
	 * @param url
	 * @param postParameters
	 * @return
	 */
	public String doPost(String url, List<NameValuePair> postParameters) {
		String resultStr = "";
		//BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(url);
			// ʵ����UrlEncodedFormEntity����
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					postParameters, "gbk");

			// ʹ��HttpPost����������UrlEncodedFormEntity��Entity
			request.setEntity(formEntity);
			HttpResponse response = client.execute(request);
			
			HttpEntity entity = response.getEntity();
			String respContent = EntityUtils.toString( entity, "GBK").trim();
			//System.out.println(respContent);	
			 //respContent =  URLDecoder.decode(respContent,"GBK");
			 //Log.e("zgt", "respContent:"+respContent);
			 
			
			 
			 return respContent;
//			in = new BufferedReader(new InputStreamReader(response.getEntity()
//					.getContent()));
//
//			StringBuffer string = new StringBuffer("");
//			String lineStr = "";
//			while ((lineStr = in.readLine()) != null) {
//				// Log.i(TAG,"lineStr="+lineStr);
//				string.append(lineStr + "\n");
//				resultStr = lineStr;
//			}
//			
//			
//			 
//			in.close();
			// Log.i(TAG, "result=" + string.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			if (in != null) {
//				try {
//					in.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
		}
		return resultStr;
	}
	
	
	public static abstract class BaiduHandler{
		public abstract void baiduPaySuccess(String resultInfo,String resultStatus);
		public abstract void baiduPayFailed(String resultInfo,String resultStatus);
		public abstract void baiduPayCancel(String resultInfo,String resultStatus);
	}
	

}
