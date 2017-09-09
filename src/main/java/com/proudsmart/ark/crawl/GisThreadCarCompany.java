package com.proudsmart.ark.crawl;

import java.net.SocketTimeoutException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.proudsmart.ark.bean.CompanyGisDetail;
import com.proudsmart.ark.util.DBUtil;

public class GisThreadCarCompany implements Runnable {
	long minId;
	long maxId;
	int index;//线程编号
	String table;
	
	public GisThreadCarCompany() {
		super();
		this.minId = -1;
		this.maxId = -1;
		this.index = 1;
		this.table = "crawler.car_company_address";
	}

	public GisThreadCarCompany(Long minId, Long maxId, int index, String table) {
		super();
		this.minId = minId;
		this.maxId = maxId;
		this.index = index;
		this.table = table;
	}
	
	@Override
	public void run() {
		ResultSet rs = null;
		if(minId == -1){//全表抓取
			rs = DBUtil.executeQuery("select id, address from "+table+" where status = 0");
		}else//部分抓取
			rs = DBUtil.executeQuery("select id, address from "+table+" where status != 1 and index_id between "+minId+" and "+maxId+" and address is not null order by id");
		int count = 1;
		try {
			while(rs.next()){
				Long companyId = rs.getLong("id");
				if(count%50 == 0)
					System.out.println("=======current count:"+count+"; thread index is:"+index+"; current company id:"+companyId);
				String address = replaceBlank(rs.getString("address"));
				if(address.contains("#")){
					address = address.substring(0, address.indexOf("#"));
				}
				if(address.contains("\\")){
					address = address.replace("\\", "");
				}
				if(address.contains("`")){
					address = address.replace("`", "");
				}
				if(address.contains("^")){
					address = address.replace("^", "");
				}
				if(address.contains("|")){
					address = address.replace("|", "");
				}
				if(address.contains("<")){
					address = address.replace("<", "").replace(">", "");
				}
				if(address.contains("“") || address.contains("”") || address.contains("\"")){
					address = address.replace("\"", "").replace("”", "").replace("“", "");
				}
				try{
					CompanyGisDetail detail = GisLocationCrawler.crawlObj(address);
					if(detail != null){
						DBUtil.saveCarCompanyGis(detail, companyId, table);
					}
					else
						DBUtil.updateCarGisStatus(companyId, 2, table);
				} catch(IllegalArgumentException e){
					System.out.println("=================illegal address:"+address);
					DBUtil.updateCarGisStatus(companyId, 2, table);
				} catch(SocketTimeoutException e){
//					DBUtil.saveSocketExceptinInfo(companyId, address);
					DBUtil.updateCarGisStatus(companyId, 2, table);
				} catch(Exception e){
					DBUtil.updateCarGisStatus(companyId, 4, table);
				}
				
				count++;
			}
			
			System.out.println("=================current thread index is:"+index+" has finished crawling");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str.trim());
            dest = m.replaceAll("");
        }
        return dest;
    }
	
}
