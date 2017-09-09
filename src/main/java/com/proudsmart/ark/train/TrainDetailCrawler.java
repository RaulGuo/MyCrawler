package com.proudsmart.ark.train;

import java.sql.ResultSet;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.proudsmart.ark.util.DB3307Util;
import com.proudsmart.ark.util.HttpUtil;

public class TrainDetailCrawler implements Runnable {
	private long minId;
	private long maxId;
	
	public TrainDetailCrawler(long minId, long maxId) {
		super();
		this.minId = minId;
		this.maxId = maxId;
	}

	@Override
	public void run() {
		try{
			ResultSet rs = DB3307Util.executeQuery("select id, checi, checi_url from test.province_station where id between "+minId+" and "+maxId);
			while(rs.next()){
				Long trainId = rs.getLong("id");
				String checi = rs.getString("checi");
				String checiUrl = ProvinceInfoCrawler.base+rs.getString("checi_url");
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
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("================error occus for train detail of id between "+minId+" and "+maxId);
		}
	}
	
	
	
}
