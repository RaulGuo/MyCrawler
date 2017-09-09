package com.proudsmart.kpi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.proudsmart.ark.util.DB3307Util;

public class KpiOverviewDataArrange {
	public static void main(String[] args) throws SQLException {
		Gson gson = new Gson();
		ResultSet rs = DB3307Util.executeQuery("select id from car_kpi_overview where data is null");
		Set<Long> overviewIds = new HashSet<Long>();
		while(rs.next()){
			overviewIds.add(rs.getLong("id"));
		}
		
		for(Long id: overviewIds){
			ResultSet result = DB3307Util.executeQuery("select time, strdata from kpi_detail where overview_id = "+id +" order by time desc");
			List<Data> dataSet = new ArrayList<Data>();
			while(result.next()){
				String time = result.getString("time");
				String strdata = result.getString("strdata");
				Data data = new Data(time, strdata);
				dataSet.add(data);
			}
			
			String toJson = gson.toJson(dataSet);
			DB3307Util.saveTmpData(id, toJson);
			
		}
	}
}


class Data{
	String t;
	String v;
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public String getV() {
		return v;
	}
	public void setV(String v) {
		this.v = v;
	}
	public Data(String t, String v) {
		super();
		this.t = t;
		this.v = v;
	}
	
	
}
