package com.proudsmart.ark.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.proudsmart.ark.util.HttpUtil;

public class ProxyUtil {
	
	public static List<String> hosts;

	public static void initConfig(){
		if(hosts == null || hosts.isEmpty()){
			hosts = new ArrayList<String>();
			BufferedReader br = null;
			try{
				File file = new File("conf\\proxys.txt");
				System.out.println(file.getAbsolutePath());
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line = null;
				while((line = br.readLine()) != null){
					hosts.add(line);
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private final static String proxyUrl="http://dps.kuaidaili.com/api/getdps/?orderid=937080193414252&num=1&dedup=1&format=json&sep=1";
	private static HttpClient httpClient = HttpUtil.getCommonClient();
	private static Gson gson = new Gson();
	private static int count = 0;
	
	public static HttpHost getProxyHost(){
		return getProxyHost(1);
	}
	
	public static HttpHost getProxyHost(int proxyCount){
		HttpResponse response = getPage(proxyUrl);
		if(response != null){
			ResultObj obj =null;
			try{
				obj= solveProxyResponse(response);
				if(obj == null || obj.msg.contains("没有找到符合条件的代理")){
					if(proxyCount <= 100){
						try {
							Thread.sleep(50*1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						System.out.println("=============failed to get proxy, current time:"+proxyCount);
						proxyCount++;
						return getProxyHost(proxyCount);
					}else{
						System.out.println("================no available proxy anymore, application will exist");
						System.exit(0);
					}
				}
				
				String[] hostStr = obj.data.proxy_list[0].split(":");
				HttpHost host = new HttpHost(hostStr[0], Integer.parseInt(hostStr[1]));
				count++;
				System.out.println("===========proxy refresh count:"+count+"; current host info is:"+obj.data.proxy_list[0]);
				return host;
			}catch(Exception e){
//				e.printStackTrace();
//				System.out.println("=================get proxy host exception");
				//100秒后重新获取代理
				try {
					Thread.sleep(100*1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("==========get proxy host exception occurs, current time:"+proxyCount);
				e.printStackTrace();
				proxyCount++;
				return getProxyHost(proxyCount);
			}
		}else{
			System.out.println("=============no response of the proxy url found");
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		HttpHost host = getProxyHost();
		System.out.println(host.getHostName());
		System.out.println(host.getPort());
	}
	
	
	private static ResultObj solveProxyResponse(HttpResponse response){
		HttpUtil.solveGzipResponse(response);
		String content = null;
		try {
			content = HttpUtil.getResponseContentByReader(response);
		} catch (Exception e) {
			System.out.println("==========parse result error!");
		} 
		content = decodeUnicode(content);
		try{
			ResultObj obj = gson.fromJson(content,ResultObj.class);
			return obj;
		}catch(JsonSyntaxException e){
			return null;
		}
	}
	
	public static String decodeUnicode(String theString) {
		char aChar;
	    int len = theString.length();
	    StringBuffer outBuffer = new StringBuffer(len);
	    for (int x = 0; x < len;) {
	    	aChar = theString.charAt(x++);
	        if (aChar == '\\') {
	        	aChar = theString.charAt(x++);
	            if (aChar == 'u') {
	                    // Read the xxxx
	            	int value = 0;
	                for (int i = 0; i < 4; i++) {
	                	aChar = theString.charAt(x++);
	                    switch (aChar) {
	                    case '0':
	                    case '1':
	                    case '2':
	                    case '3':
	                    case '4':
	                    case '5':
	                    case '6':
	                    case '7':
	                    case '8':
	                    case '9':
	                    	value = (value << 4) + aChar - '0';
	                        break;
	                    case 'a':
	                    case 'b':
	                    case 'c':
	                    case 'd':
	                    case 'e':
	                    case 'f':
	                        value = (value << 4) + 10 + aChar - 'a';
	                        break;
	                    case 'A':
	                    case 'B':
	                    case 'C':
	                    case 'D':
	                    case 'E':
	                    case 'F':
	                        value = (value << 4) + 10 + aChar - 'A';
	                        break;
	                    default:
	                    	throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
	                    }
	                }
	                outBuffer.append((char) value);
	            } else {
	            	if (aChar == 't')
	            	aChar = '\t';
	            	else if (aChar == 'r')
	                	aChar = '\r';
	            	else if (aChar == 'n')
	                	aChar = '\n';
	            	else if (aChar == 'f')
	                	aChar = '\f';
	             	outBuffer.append(aChar);
	            }
	       } else
	       	outBuffer.append(aChar);
	    }
	    return outBuffer.toString();
	    }
	
	private static	HttpResponse getPage(String url){
		HttpGet httpGet = null;
		try{
			httpGet = new HttpGet(url);
			HttpResponse response =  httpClient.execute(httpGet);
			return response;
		}catch(Exception e){
			return null;
		}finally{
			if(httpGet != null){
				httpGet.completed();
        		httpGet = null;
			}
		}
	}
} 
