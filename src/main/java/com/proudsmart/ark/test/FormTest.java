package com.proudsmart.ark.test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class FormTest {
	private static String url = "http://www.chinacar.com.cn/Home/GonggaoSearch/GonggaoSearch/search_json?_dc=1500450544057";
	private static String formUrl = "http://www.chinacar.com.cn/ggcx_new/list.html";
	public static void form() throws UnsupportedEncodingException{
		
		HttpClientBuilder builder = HttpClientBuilder.create();
		HttpClient client = builder.build();
		
		try{
			HttpPost post = new HttpPost("url");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("", ""));
			
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			
			
		}finally{
			client.getConnectionManager().shutdown();
		}
	}
}
