package com.zhixuan.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class HttpUtils {

	// 请求队列
	private RequestQueue requestQueue;
	// 监听器
	private OnLoadJsonObjectListener listener;

	// 加载json数据完毕接口
	public interface OnLoadJsonObjectListener {
		public void onResponse(JSONObject object);

		public void onErrorResponse(VolleyError error);
	}

	/*
	 * 构造函数
	 */
	public HttpUtils() {
		
	}
	
	public HttpUtils(Context context) {
		requestQueue = Volley.newRequestQueue(context);
	}

	public void LoadJsonObject(String url) {
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject object) {
						Log.d("TAG", object.toString());
						listener.onResponse(object);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAG", error.getMessage(), error);
						listener.onErrorResponse(error);
					}
				});

		requestQueue.add(jsonObjectRequest);
	}

	public String doHttpGet(String url) {
		String result = "";
		try {
			// 得到HttpClient对象
			HttpClient getClient = new DefaultHttpClient();
			// 得到HttpGet对象
			HttpGet request = new HttpGet(url);
			// 客户端使用GET方式执行请教，获得服务器端的回应response
			HttpResponse response = getClient.execute(request);
			// 判断请求是否成功
			if (response.getStatusLine().getStatusCode() == 200) {

				// 获得输入流
				InputStream inputStream = response.getEntity().getContent();
				int len = 0;
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				byte[] data = new byte[1024];

				while ((len = inputStream.read(data)) != -1) {
					outputStream.write(data, 0, len);
				}
				result = new String(outputStream.toByteArray(), "utf-8");
				// 关闭输入流
				inputStream.close();

			} else {
				Log.d("---", "请求服务器端失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public Drawable loadImageFromNetwork(String imageUrl) {
		Drawable drawable = null;
		try {
			// 可以在这里通过文件名来判断，是否本地有此图片
			drawable = Drawable.createFromStream(
					new URL(imageUrl).openStream(), "image.jpg");
		} catch (IOException e) {
			Log.d("--->", e.getMessage());
		}
		if (drawable == null) {
			Log.d("--->", "null drawable");
		} else {
			Log.d("--->", "not null drawable");
		}

		return drawable;
	}

	public String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
