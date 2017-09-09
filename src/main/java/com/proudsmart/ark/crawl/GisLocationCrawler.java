package com.proudsmart.ark.crawl;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.proudsmart.ark.bean.CompanyGisDetail;
import com.proudsmart.ark.util.DBUtil;
import com.proudsmart.ark.util.HttpUtil;

/**
 * nohup java -Xms4096m -Xmx8192m -jar /home/data_center/dependency/GisOrg-0.0.1-SNAPSHOT-jar-with-dependencies.jar > output &
 * @author guozhen@proudsmart.com
 *
 */

public class GisLocationCrawler {
	private final static String reqUrlPrefix = "http://apis.map.qq.com/jsapi?qt=geoc&key=FBOBZ-VODWU-C7SVF-B2BDI-UK3JE-YBFUS&output=jsonp&pf=jsapi&ref=jsapi&cb=qq.maps._svcb3.geocoder1&addr="; 
	public static Gson gson = new Gson();
	private static final String detailStr = "\"detail\":";//用于获取的页面内容的截取
	
	/**
	 * 分省，每个省一个线程来抓取
	 * @param args
	 * @throws SQLException
	 * @throws InterruptedException
	 */
//	public static void main(String[] args) throws SQLException, InterruptedException {
//		ExecutorService executors = Executors.newFixedThreadPool(4);
//		
//		for(String province:args){
//			Runnable r = new GisThread(province);
//			executors.execute(r);
////			System.out.println("============="+province+" crawl gis end");
//		}
//		
//		executors.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
//		executors.shutdown();
//		
//	}
	
	
	/**
	 * 分省，每个省分成四个线程去抓取，使用GisThread
	 * @param args
	 * @throws SQLException
	 * @throws InterruptedException
	 */
//	public static void main(String[] args) throws SQLException, InterruptedException {
//		
//		for(String province:args){
//			long currMinId = DBUtil.loadMaxCompanyId(province);
//			Long[] ids = DBUtil.loadDCCompanyMaxMinId(province);
//			Long min = ids[0] > currMinId? ids[0]: currMinId;
//			long max = ids[1];
//			
//			int size = 20;
//			long batch = (max - min)/size;
//			ExecutorService executors = Executors.newFixedThreadPool(size);
//			Long currMin = min;
//			Long currMax = min+batch;
//			for(int i = 0; i < size; i++){
//				Runnable r = new GisThread(province, currMin, currMax, i);
//				executors.execute(r);
//				
//				currMin = currMax;
//				currMax = currMin + batch;
//			}
//			executors.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
//			executors.shutdown();
//			
//			System.out.println("++++++++++++++++++"+province+" finished");
//		}
//		
//		
//	}
	
	
	/**
	 * 分省份抓取地址
	 * @param args
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws SQLException, InterruptedException {
		HttpUtil.refreshProxyClient();
		
		for(int circulTime = 0; circulTime < 4; circulTime++){
			for(String province:args){
				Long[] ids = DBUtil.loadTestMaxMinId(province);
				Long min = ids[0];
				long max = ids[1];
				
				int size = DBUtil.getGisThreadsCount();
				System.out.println("==========thread number:"+size);
				long batch = (max - min)/size;
				ExecutorService executors = Executors.newFixedThreadPool(size);
				Long currMin = min;
				Long currMax = min+batch;
				System.out.println("==========start to create threads");
				for(int i = 0; i < size; i++){
					Runnable r = new GisThreadByStatus(province, currMin, currMax, i);
					executors.execute(r);
					
					currMin = currMax;
					currMax = currMin + batch;
					
					Thread.sleep(100);
				}
				System.out.println("==========finish creating threads");
				executors.shutdown();
				executors.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
				
				System.out.println("++++++++++++++++++"+province+" finished");
			}
		}
		
	}
	
	/**
	 * 抓取汽车行业的公司地址的gis
	 * @param args
	 * @throws SQLException
	 * @throws InterruptedException
	 */
//	public static void main(String[] args) throws SQLException, InterruptedException {
//		HttpUtil.refreshProxyClient();
//		
//		
////		int tableSize = 4;
//		
//		int tableIndex = Integer.parseInt(args[0]);
//		
//		String tableName = "crawler.car_company_address_"+tableIndex;
//		for(int times = 1; times <= 10; times++){
//			Long[] ids = DBUtil.loadCarCompanyMaxMinId(tableName);
//			Long min = ids[0];
//			long max = ids[1];
//			int size = DBUtil.getGisThreadsCount();
//			System.out.println("==========thread number:"+size);
//			long batch = (max - min)/size;
//			ExecutorService executors = Executors.newFixedThreadPool(size);
//			Long currMin = min;
//			Long currMax = min+batch;
//			System.out.println("==========start to create threads");
//			for(int i = 0; i < size; i++){
//				Runnable r = new GisThreadCarCompany(currMin, currMax, i, tableName);
//				executors.execute(r);
//				
//				currMin = currMax;
//				currMax = currMin + batch;
//				
//				Thread.sleep(10);
//			}
//			System.out.println("==========finish creating threads");
//			executors.shutdown();
//			executors.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
//			System.out.println("===========car company for table "+tableIndex+", "+times+" times is over=====");
//		}
//		System.out.println("============all is over===============");
//	}
	
	public static CompanyGisDetail crawlObj(String address) throws SocketTimeoutException{
		String url = reqUrlPrefix+address;
		String text = null;
		try {
			Document doc = HttpUtil.getDocument(url, "OnlineMap");
			text = doc.text();
			if(text != null && !text.trim().isEmpty()){
				if(text.contains("被封禁")){
					HttpUtil.refreshProxyClient();
					return crawlObj(address);
				} 
				
				if(text.contains("Too Many Request")){
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else if(text.contains("errmsg")){
					return null;
				}else{
					String subText = null;
					if(text.contains(detailStr)){
						if(text.endsWith("}})"))
							subText = text.substring(text.indexOf(detailStr)+detailStr.length(), text.lastIndexOf("})"));
						else
							subText = text.substring(text.indexOf(detailStr)+detailStr.length(), text.lastIndexOf(")"));
						CompanyGisDetail detail = gson.fromJson(subText, CompanyGisDetail.class);
						detail.setDetailStringContent(subText);
						return detail;
					}else{
						System.out.println("===========return response text doesn't contain detail info, content:"+text);
					}
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch(HttpHostConnectException e){
			HttpUtil.refreshProxyClient();
			return crawlObj(address);
		} catch(SocketException e){
//			System.out.println("===============socket exception for:"+address+", proxy will be refreshed");
//			e.printStackTrace();
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			HttpUtil.refreshProxyClient();
			return crawlObj(address);
		} catch(SocketTimeoutException e){
			System.out.println("===============socket timeout for:"+address);
			throw e;
		} catch(ConnectTimeoutException e){
//			HttpUtil.refreshProxyClient();
			return crawlObj(address);
		} catch(NoHttpResponseException e){//服务器没有响应，休眠，然后重新抓取
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return crawlObj(address);
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e){
			System.out.println("================text exception:"+text);
		} catch(NoClassDefFoundError e){
			System.out.println("================current exception: NoClassDefFoundError for address:"+address);
		}  catch(JsonSyntaxException e){
			System.out.println("===============gson exception:"+text);
			throw e;
		}
		
		return null;
	}
	
	
//	public static void loadByProvince(String province) throws SQLException, InterruptedException{
//		GisThread r = new GisThread(province);
//		r.executeCrawl();
//	}
	
	
	
	protected Document getDocument(String url,String headerType) throws ClientProtocolException, IOException{
		String content;
		HttpResponse response = HttpUtil.getPage(url,headerType);
		HttpUtil.solveGzipResponse(response);
		content = HttpUtil.getResponseContentByReader(response,"GBK");
		return Jsoup.parse(content);
	}

}


