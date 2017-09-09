package com.proudsmart.ark.crawl;

import java.net.SocketTimeoutException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.proudsmart.ark.bean.CompanyGisDetail;
import com.proudsmart.ark.util.DBUtil;

public class GisThread implements Runnable {
	String province;
	long minId;
	long maxId;
	int index;//线程编号
	
	public GisThread(String province) {
		super();
		this.province = province;
		this.minId = -1;
		this.maxId = -1;
		this.index = 1;
	}

	public GisThread(String province, Long minId, Long maxId, int index) {
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
			Long minId = DBUtil.loadMaxCompanyId(province);
			rs = DBUtil.executeQuery("select id, address from dc_import."+province+"_company where id > "+minId+" and type not like '个体%' and address is not null order by id");
		}else//部分抓取
			rs = DBUtil.executeQuery("select id, address from dc_import."+province+"_company where id between "+minId+" and "+maxId+" and type not like '个体%' and address is not null order by id");
		int count = 1;
		try {
			while(rs.next()){
				Long id = rs.getLong("id");
				if(count%50 == 0)
					System.out.println("=======current count:"+count+";province is:"+province+"; thread index is:"+index+"current company id:"+id);
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
					if(detail != null)
						DBUtil.saveGis(detail, id, province);
				} catch(IllegalArgumentException e){
					System.out.println("=================illegal address:"+address);
					e.printStackTrace();
				} catch(SocketTimeoutException e){
					DBUtil.saveSocketExceptinInfo(id, address);
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
