package com.proudsmart.ark.train;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.proudsmart.ark.crawl.GisThreadByStatus;
import com.proudsmart.ark.util.DB3307Util;
import com.proudsmart.ark.util.DBUtil;
import com.proudsmart.ark.util.HttpUtil;

/**
 * nohup java -cp /home/data_center/dependency/GisOrg-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.proudsmart.ark.train.ProvinceInfoCrawler > /home/data_center/dependency/train.output &
 * @author guozhen@proudsmart.com
 *
 */

public class ProvinceInfoCrawler {
	
	public static String rootUrl = "http://qq.ip138.com/train/";
	
	public static String base = "http://qq.ip138.com";
	
	
	public static Map<String, String> getProvinceUrlMap() throws ClientProtocolException, IOException {
		Document doc = HttpUtil.getDocument("http://qq.ip138.com/train/", "common");
		System.out.println(doc);
		
		Elements trs = doc.getElementsByAttributeValueStarting("href", "/train");
		System.out.println(trs);
		
		Map<String, String> map = new HashMap<String, String>();
		for(Element e: trs){
			String href = e.attr("href");
			String prov = e.text();
			System.out.println("href: "+href+", province:"+prov);
			if(!prov.equals("列车时刻首页")){
	//			String url = baseUrl.replace("/train/", "")+href;
				map.put(prov, href);
			}
		}
		
		return map;
	}
	
	/**
	 * 抓取省份对应的所有的车站
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void getProvinceStationList() throws ClientProtocolException, IOException{
		Map<String, String> provinceUrl = getProvinceUrlMap();
		for(Entry<String, String> entry: provinceUrl.entrySet()){
			String province = entry.getKey();//省份
			String suffix = entry.getValue();
			String urlToVisit = base+suffix;
			Document doc = HttpUtil.getDocument(urlToVisit, "common");
			Elements trs = doc.getElementsByAttributeValueStarting("href", suffix);
			System.out.println(trs);
			
			for(Element e: trs){
				String href = e.attr("href");
				String stationName = e.text();
				System.out.println("-----------------------");
				System.out.println("href: "+href+", stationName:"+stationName);
				DB3307Util.saveProvinceStation(province, stationName, href);
			}
		}
	}
	
	/**
	 * 抓取车站对应的车次
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void getStationTrains() throws SQLException, ClientProtocolException, IOException{
		ResultSet rs = DB3307Util.executeQuery("select id, station, url from test.province_station");
		while(rs.next()){
			Long stationId = rs.getLong("id");
			String stationName = rs.getString("station");
			String url = base+rs.getString("url");
			Document doc = HttpUtil.getDocument(url, "common");
			
			Element divElement = doc.getElementById("checilist");
			//拿到所有的tr
			Elements trs = divElement.getElementsByAttributeValueStarting("onmouseover", "this.bgColor=");
			for(Element tr: trs){
				Elements tds = tr.getElementsByTag("td");
				int index = 0;
				Element checiTd = tds.get(index);
				String suffix = checiTd.getElementsByTag("a").get(0).attr("href");
				String checiStr = checiTd.getElementsByTag("b").get(0).text();
				
				index++;
				Element liecheLeixingTd = tds.get(index);
				String liecheLeixing = liecheLeixingTd.text();
				
				index++;
				Element shifazhanTd = tds.get(index);
				String shifazhan = shifazhanTd.text();
				
				index++;
				Element shifaShijianTd = tds.get(index);
				String shifaShijian = shifaShijianTd.text();
				
				index++;
				Element jingguozhanTd = tds.get(index);
				String jingguozhan = jingguozhanTd.text();
				
				index++;
				Element jingguoDaodaShijianTd = tds.get(index);
				String jingguoDaodaShijian = jingguoDaodaShijianTd.text();
				
				index++;
				Element jingguoFacheShijianTd = tds.get(index);
				String jingguoFacheShijian = jingguoFacheShijianTd.text();
				
				index++;
				Element zhongdianzhanTd = tds.get(index);
				String zhongdianzhan = zhongdianzhanTd.text();
				
				index++;
				Element daodashijianTd = tds.get(index);
				String daodashijian = daodashijianTd.text();
				
				DB3307Util.saveCheciInfo(stationId, stationName, checiStr, suffix, liecheLeixing, shifazhan, shifaShijian, jingguozhan, jingguoDaodaShijian, jingguoFacheShijian, zhongdianzhan, daodashijian);
				
			}
		}
	}
	
	/**
	 * 抓取车次的详细信息
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void getTrainDetailInfo() throws SQLException, ClientProtocolException, IOException{
		ResultSet rs = DB3307Util.executeQuery("select id, checi, checi_url from test.province_station");
		while(rs.next()){
			Long trainId = rs.getLong("id");
			String checi = rs.getString("checi");
			String checiUrl = base+rs.getString("checi_url");
			Document doc = HttpUtil.getDocument(checiUrl, "common");
			
			//获取车次的附加信息（包括车次的耗时，价格等）
			Element atmtTable = doc.getElementsByAttributeValueStarting("bordercolordark", "#ffffff").first();
			Elements atmtTds = atmtTable.getElementsByTag("td");
			int index = 1;//0是列车类型，暂时没用
			String quanchengHaoshi = atmtTds.get(index).text();
			
			index++;
			String quanchengJuli = atmtTds.get(index).text();
			
			index++;
			String quanchengJiage = atmtTds.get(index).text();
			
			DB3307Util.saveTrainAtmtInfo(trainId, checi, quanchengHaoshi, quanchengJuli, quanchengJiage);
			
			Element divElement = doc.getElementById("stationInfo");
			Elements trs = divElement.getElementsByAttributeValueStarting("onmouseover", "this.bgColor=");
			//每一条tr中都有多个td，用于获取车次信息
			for(Element tr: trs){
				Elements tds = tr.getElementsByTag("td");
				
				index = 1;//0是序号，没有用
				Element td = tds.get(index);
				String stationHref = td.getElementsByTag("a").get(0).attr("href");
				String stationName = td.getElementsByTag("a").get(0).text();
				
				index++;
				String daodaShijian = tds.get(index).text();
				
				index++;
				String facheshijian = tds.get(index).text();
				
				index++;
				String licheng = tds.get(index).text();
				
				DB3307Util.saveCheciStationInfo(trainId, checi, stationHref, stationName, daodaShijian, facheshijian, licheng);
				
			}
		}
	}
	
	
	public static void main(String[] args) throws ClientProtocolException, IOException, SQLException, InterruptedException {
		if(false){
			long minId = 1578;
			long maxId = 2898;
			int size = 10;
			long batch = (maxId - minId)/size;
			
			ExecutorService executors = Executors.newFixedThreadPool(size);
			Long currMin = minId;
			Long currMax = minId+batch;
			System.out.println("==========start to create threads");
			for(int i = 0; i < size; i++){
				Runnable r = new StationTrainThread(currMin, currMax);
				executors.execute(r);
				
				currMin = currMax;
				currMax = currMin + batch;
			}
			System.out.println("==========finish creating threads");
			executors.shutdown();
			executors.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
			
		}
		
		if(true){
			Long[] ids = DB3307Util.loadMaxMinId("test.train");
			Long min = ids[0];
			long max = ids[1];
			int size = 100;
			long batch = (max - min)/size;
			ExecutorService executors = Executors.newFixedThreadPool(size);
			Long currMin = min;
			Long currMax = min+batch;
			System.out.println("==========start to create threads");
			for(int i = 0; i < size; i++){
				Runnable r = new TrainDetailCrawler(currMin, currMax);
				executors.execute(r);
				
				currMin = currMax;
				currMax = currMin + batch;
			}
			System.out.println("==========finish creating threads");
			executors.shutdown();
			executors.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
			
		}
		
//		addFailedStationTrain();
	}
	
	
	public static void addFailedStationTrain() throws InterruptedException{
		ExecutorService executors = Executors.newFixedThreadPool(4);
		
		Runnable r1 = new StationTrainThread(1710, 1842);
		executors.execute(r1);
			
		Runnable r2 = new StationTrainThread(2634, 2766);
		executors.execute(r2);
			
		Runnable r3 = new StationTrainThread(1578, 1710);
		executors.execute(r3);
		
		executors.shutdown();
		executors.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
	}
}
