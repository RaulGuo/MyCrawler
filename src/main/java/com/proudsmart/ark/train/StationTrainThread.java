package com.proudsmart.ark.train;

import java.sql.ResultSet;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.proudsmart.ark.util.DB3307Util;
import com.proudsmart.ark.util.HttpUtil;

public class StationTrainThread implements Runnable {
	
	private long minId;
	private long maxId;
	
	public StationTrainThread(long minId, long maxId) {
		super();
		this.minId = minId;
		this.maxId = maxId;
	}
	
	@Override
	public void run() {
		ResultSet rs = DB3307Util.executeQuery("select id, station, url from test.province_station where id between "+minId+" and "+maxId);
		try{
			while(rs.next()){
				Long stationId = rs.getLong("id");
				String stationName = rs.getString("station");
				String url = ProvinceInfoCrawler.base+rs.getString("url");
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
			}		}catch(Exception e){
			e.printStackTrace();
			System.out.println("--------------------error occus for station train of id between "+minId+" and "+maxId);
		}
	}

	

}