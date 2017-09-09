package com.proudsmart.ark.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;  
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.proudsmart.ark.proxy.DynamicProxyRoutePlanner;
import com.proudsmart.ark.proxy.ProxyInfo;
import com.proudsmart.ark.proxy.ProxyUtil;
import com.proudsmart.ark.util.HeaderFactory;
import com.proudsmart.ark.util.HttpUtil;


/**
 */
public class HttpUtil {
	private static RequestConfig requestConfig = null;
//	private static HttpClient httpClient = null;
	
	private static HttpClient proxyClient = null;
	private static DynamicProxyRoutePlanner routePlanner = null;
	private static HttpHost proxy = null;
	
	public static ReentrantLock lock = new ReentrantLock(true);
	
	//使用前先获取一个ip
//	static {
//		lock = 
//		refreshProxyClient();
//		requestConfig = getRequestConfig(proxy);
//		
//		System.out.println("===========proxy initialize successfully");
//	}
	
//	public static void initHttpClient(){
//		httpClient = getCommonClient();
//	}
	
	public static HttpClient getHttpClient(String type){
		switch(type){
		case "https":
			return getSSLInsecureClient();
		case "http":
			return getCommonClient();
		default:
			return getCommonClient();
		}
	}
	public static HttpClient getSSLInsecureClient() {  
        
		try {  
			SSLContext sslContext = SSLContext.getInstance("TLS");        
			sslContext.init(null, new TrustManager[] { truseAllManager }, null);         
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(  
                    sslContext, new X509HostnameVerifier() {  
                        
                    	@Override  
                        public boolean verify(String arg0, SSLSession arg1) {  
                            return true;  
                        }  
  
                        @Override  
                        public void verify(String host, SSLSocket ssl)  
                                throws IOException {  
                        }  
  
                        @Override  
                        public void verify(String host, X509Certificate cert)  
                                throws SSLException {  
                        }  
  
                        @Override  
                        public void verify(String host, String[] cns,  
                                String[] subjectAlts) throws SSLException {  
                        }  
  
                    });  
            
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();  
            
        } catch (GeneralSecurityException e) {  
            return null;
        }  
    }
	
	private static TrustManager truseAllManager = new X509TrustManager(){  
		  
        public void checkClientTrusted(  
                java.security.cert.X509Certificate[] arg0, String arg1)  
                throws CertificateException {  
            // TODO Auto-generated method stub  
              
        }  
  
        public void checkServerTrusted(  
                java.security.cert.X509Certificate[] arg0, String arg1)  
                throws CertificateException {  
            // TODO Auto-generated method stub  
              
        }  
  
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
          
    }; 
    
    /**
     * @return
     */
    public static HttpClient getCommonClient(){ 	
    	return HttpClients.custom()
			    .disableAutomaticRetries()
			    .disableConnectionState()
			    .disableContentCompression()
			    .disableRedirectHandling()
			    .useSystemProperties()
			//    .setDefaultCookieStore(cookieStore)
			    .build();
    }
    /**
     * @return
     */
    public static RequestConfig getRequestConfig(){
    	return getRequestConfig(Default.DEFAULT_SOCKET_TIMEOUT, Default.DEFAULT_CONNECT_TIMEOUT);
    }
    /**
     * @param socket_timeout
     * @param connect_timeout
     * @return
     */
    public static RequestConfig getRequestConfig(int socket_timeout, int connect_timeout){
    	return RequestConfig.custom()
				.setSocketTimeout(socket_timeout)
				.setConnectTimeout(connect_timeout)
				.build();
    }
    /**
     * @param proxy_info
     * @return
     */
    public static RequestConfig getRequestConfig(HttpHost host){
    	return getRequestConfig(host,Default.DEFAULT_SOCKET_TIMEOUT, Default.DEFAULT_CONNECT_TIMEOUT);
    }

    /**
     * @param proxy_info
     * @param socket_timeout
     * @param connect_timeout
     * @return
     */
    public static RequestConfig getRequestConfig(HttpHost host, int socket_timeout, int connect_timeout){
    	HttpHost proxy = new HttpHost(host.getHostName(),host.getPort(),"http");
    	return RequestConfig.custom()
					.setProxy(proxy)
					.setSocketTimeout(socket_timeout)
					.setConnectTimeout(connect_timeout)
					.build();	
    }
    
    
    public static String getResponseContentByReader(HttpResponse response) throws ParseException, IOException{
    	return getResponseContentByReader(response, "UTF-8");
    }
    
    
    /**
	 * @param response
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
    public static String getResponseContentByReader(HttpResponse response, String type) throws ParseException, IOException{
		BufferedReader reade = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),type));
		StringBuffer sb = new StringBuffer();
		String str;
		while((str = reade.readLine()) != null){ 
			sb.append(str);
		}
		return sb.toString();
	}
	/**
	 * @param response
	 */
    public static void solveGzipResponse(HttpResponse response){
		Header ceheader = response.getEntity().getContentEncoding();   
		if (ceheader != null) {  
			for (HeaderElement element : ceheader.getElements()) {  
				if (element.getName().equalsIgnoreCase("gzip")) 
					response.setEntity(new GzipDecompressingEntity(response.getEntity())); 
			}
		}
	}
	/**
	 * @param response
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
    public static String getResponseContent(HttpResponse response) throws ParseException, IOException{
		return EntityUtils.toString(response.getEntity());
	}
    /**
     * @param url
     * @param headerType
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static Document getDocumentFormGetRequest(String url,String headerType) throws ClientProtocolException, IOException{
    	String content = getContentFormGetRequest(url,headerType);
    	return Jsoup.parse(content);
    }
    /**
     * @param url
     * @param headerType
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String getContentFormGetRequest(String url,String headerType) throws ClientProtocolException, IOException{
    	HttpResponse response = getResponseFormGetRequest(url,headerType);
    	solveGzipResponse(response);
		return getResponseContentByReader(response);
    }
    /**
     * @param url
     * @param headerType
     * @return
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    public static HttpResponse getResponseFormGetRequest(String url,String headerType) throws ClientProtocolException, IOException{
    	HttpGet httpGet = null;
		try{
			httpGet = new HttpGet(url);
			httpGet.setConfig(getRequestConfig());
			httpGet.setHeaders(HeaderFactory.get(headerType));
			HttpClient httpClient = getCommonClient();
			return httpClient.execute(httpGet);
		}finally{
			httpGet.completed();
        	httpGet = null;
		}
    }
    
    
    public static Document getDocument(String url,String headerType) throws ClientProtocolException, IOException{
		String content;
		HttpResponse response = getPageWithoutProxy(url,headerType);
		HttpUtil.solveGzipResponse(response);
		content = HttpUtil.getResponseContentByReader(response,"GBK");
		return Jsoup.parse(content);
	}
    
    public static HttpResponse getPage(String url, String headerType) throws ClientProtocolException, IOException{
		HttpGet httpGet = null;
		try{
			httpGet = new HttpGet(url);
			httpGet.setConfig(requestConfig);
			httpGet.setHeaders(HeaderFactory.get(headerType));
			
			HttpResponse response =  proxyClient.execute(httpGet);
			return response;
		} catch(NoHttpResponseException e){
			throw e;
		} catch(ConnectTimeoutException e){
//			refreshProxyClient();
//			return getPage(url, headerType);
			throw e;
		} catch(SocketTimeoutException e){
			throw e;
		} catch(HttpHostConnectException e){
			throw e;
		} catch(SocketException e){
			throw e;
		} catch(NoClassDefFoundError e){
			throw e;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally{
			httpGet.completed();
        	httpGet = null;
		}
	}
    
    
    public static HttpResponse getPageWithoutProxy(String url, String headerType) {
		HttpGet httpGet = null;
		try{
			httpGet = new HttpGet(url);
			httpGet.setConfig(requestConfig);
			httpGet.setHeaders(HeaderFactory.get(headerType));
			
			HttpResponse response = getCommonClient().execute(httpGet);
			return response;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
    }
    
    private static Long lastTime = null;
    
    /**
     * 通过锁来控制代理的刷新。两次刷新之间的间隔要大于10秒，防止并发的出现
     */
    public static void refreshProxyClient(){
//    	lock.lock();
    	if(lock.tryLock()){
    		try{
    			if(lastTime == null || (System.currentTimeMillis()-lastTime) > 150*1000){
			    	proxy = ProxyUtil.getProxyHost();
			    	routePlanner = new DynamicProxyRoutePlanner(proxy);
			    	proxyClient = HttpClients.custom().setRoutePlanner(routePlanner).build();
			    	requestConfig = getRequestConfig(proxy);
			    	lastTime = System.currentTimeMillis();
    			}else{
    				try {
    					Thread.sleep(5*1000);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    		}finally{
    			lock.unlock();
    		}
    	}else{
    		try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
//    	if(routePlanner == null){
//        	routePlanner = new DynamicProxyRoutePlanner(proxy);
//        	proxyClient = HttpClients.custom().setRoutePlanner(routePlanner).build();
//    	}else{
//    		routePlanner.setProxy(proxy);
//    	}
    }
}
