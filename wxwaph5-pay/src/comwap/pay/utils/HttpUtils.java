package comwap.pay.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


/**
 * HttpUtils 
 * @author zgt
 *
 */
public class HttpUtils {

	
	/** 
	 * post
	 * @param uri 
	 * @param map
	 * @return
	 */
	public static   String post(String uri,Map<String, String> map)  {
		//HttpClient client = new DefaultHttpClient();
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(uri);
		CloseableHttpResponse response = null ;
		try {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> iter = map.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, String> entry = iter.next();
				String key = entry.getKey(); 
				String value = entry.getValue();
				parameters.add(new BasicNameValuePair(key, value));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");
			httpPost.setEntity(entity);
			//HttpResponse response = client.execute(httpPost); 
			response= client.execute(httpPost);
			// read response
			if(response.getStatusLine().getStatusCode()==200){
				String result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			//client.getConnectionManager().shutdown();
			if(response!=null){try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
			
		}	
		return null;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * @param userName
	 * @param userPass
	 */
	public static   String get(String uri){
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = null ;
		try {
			HttpGet httpGet = new HttpGet(uri);
			
			response = client.execute(httpGet); // 
			int statusCode = response.getStatusLine()
					.getStatusCode();// 
			if (statusCode == 200) { //
				String result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(response!=null){try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		}
		
		return null;
	}
	
	
	
}
