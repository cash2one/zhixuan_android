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

	// �������
	private RequestQueue requestQueue;
	// ������
	private OnLoadJsonObjectListener listener;

	// ����json������Ͻӿ�
	public interface OnLoadJsonObjectListener {
		public void onResponse(JSONObject object);

		public void onErrorResponse(VolleyError error);
	}

	/*
	 * ���캯��
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
			// �õ�HttpClient����
			HttpClient getClient = new DefaultHttpClient();
			// �õ�HttpGet����
			HttpGet request = new HttpGet(url);
			// �ͻ���ʹ��GET��ʽִ����̣���÷������˵Ļ�Ӧresponse
			HttpResponse response = getClient.execute(request);
			// �ж������Ƿ�ɹ�
			if (response.getStatusLine().getStatusCode() == 200) {

				// ���������
				InputStream inputStream = response.getEntity().getContent();
				int len = 0;
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				byte[] data = new byte[1024];

				while ((len = inputStream.read(data)) != -1) {
					outputStream.write(data, 0, len);
				}
				result = new String(outputStream.toByteArray(), "utf-8");
				// �ر�������
				inputStream.close();

			} else {
				Log.d("---", "�����������ʧ��");
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
			// ����������ͨ���ļ������жϣ��Ƿ񱾵��д�ͼƬ
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
			// ���MD5ժҪ�㷨�� MessageDigest ����
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// ʹ��ָ�����ֽڸ���ժҪ
			mdInst.update(btInput);
			// �������
			byte[] md = mdInst.digest();
			// ������ת����ʮ�����Ƶ��ַ�����ʽ
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
