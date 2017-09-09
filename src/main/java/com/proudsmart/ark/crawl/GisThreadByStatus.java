package com.proudsmart.ark.crawl;

import java.net.SocketTimeoutException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.proudsmart.ark.bean.CompanyGisDetail;
import com.proudsmart.ark.util.DBUtil;

public class GisThreadByStatus implements Runnable {
	String province;
	long minId;
	long maxId;
	int index;//线程编号
	
	public GisThreadByStatus(String province) {
		super();
		this.province = province;
		this.minId = -1;
		this.maxId = -1;
		this.index = 1;
	}

	public GisThreadByStatus(String province, Long minId, Long maxId, int index) {
		super();
		this.province = province;
		this.minId = minId;
		this.maxId = maxId;
		this.index = index;
	}
	
	@Override
	public void run() {
		ResultSet rs = null;
		if(minId == -1){//全表抓取
			rs = DBUtil.executeQuery("select company_id, address from test."+province+"_address where status = 0");
		}else//部分抓取
			rs = DBUtil.executeQuery("select company_id, address from test."+province+"_address where id between "+minId+" and "+maxId+" and status = 0 and address is not null order by id");
		int count = 1;
		try {
			while(rs.next()){
				Long companyId = rs.getLong("company_id");
				if(count%100 == 0)
					System.out.println("=======current count:"+count+";province is:"+province+"; thread index is:"+index+"; current company id:"+companyId);
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
				try{
					CompanyGisDetail detail = GisLocationCrawler.crawlObj(address);
					if(detail != null){
						DBUtil.saveGis(detail, companyId, province);
					}
					else
						DBUtil.updateStatus(province, companyId, 2);
				} catch(IllegalArgumentException e){
					System.out.println("=================illegal address:"+address);
					DBUtil.updateStatus(province, companyId, 2);
				} catch(SocketTimeoutException e){
//					DBUtil.saveSocketExceptinInfo(companyId, address);
					DBUtil.updateStatus(province, companyId, 2);
				} catch(Exception e){
					DBUtil.updateStatus(province, companyId, 4);
				}
				
				count++;
			}
			
			System.out.println("=================current province is:"+province+"; thread index is:"+index+" has finished crawling");
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
