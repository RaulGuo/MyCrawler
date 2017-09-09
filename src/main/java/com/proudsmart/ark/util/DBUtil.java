package com.proudsmart.ark.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.proudsmart.ark.bean.CompanyGisDetail;

public class DBUtil {
	private static String url = "jdbc:mysql://192.168.1.207:3306/test";
	private static String user = "root";
	private static String password = "PS@Letmein123";
	
	private static final Properties prop = getConnProp();
	
	private static Connection conn = null;

	private static Properties getConnProp() {
		
		Properties properties = new Properties();
		properties.put("user", user);
		properties.put("password", password);
		properties.put("driver", "com.mysql.jdbc.Driver");
		properties.put("useServerPrepStmts", "false");
		properties.put("rewriteBatchedStatements", "true");
		properties.put("interactiveClient", "true");
		properties.put("netTimeoutForStreamingResults", "1800");
		properties.put("autoReconnect", "true");
		properties.put("useUnicode", "true");
		properties.put("characterEncoding", "utf-8");

		return properties;
	}
	
	public static Connection getConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(url, prop);
			return conn;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ResultSet executeQuery(String sql){
		Connection conn = getConnection();
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			return stmt.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void saveCarCompanyGis(CompanyGisDetail detail, long companyId, String table){
		try {
			if(conn == null || conn.isClosed()){
				conn = getConnection();
			}
			
			if(detail != null){
				String sql = "insert ignore into crawler.car_company_gis_info(city, district, latitude, longitude, text, company_id) values(?,?,?,?,?,?)";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, detail.getCity());
				stmt.setString(2, detail.getDistrict());
				stmt.setDouble(3, detail.getPointx());
				stmt.setDouble(4, detail.getPointy());
				stmt.setString(5, detail.getDetailStringContent());
				stmt.setLong(6, companyId);
				stmt.execute();
			}
			
			updateCarGisStatus(companyId, 1, table);
			
		} catch (SQLException e) {
			e.printStackTrace();
			updateCarGisStatus(companyId, 2, table);
		}
	}
	
	public static void saveGis(CompanyGisDetail detail, long companyId, String province) {
		try {
			if(conn == null || conn.isClosed()){
				conn = getConnection();
			}
			
			if(detail != null){
				String sql = "insert ignore into statistics."+province+"_company_gis_info(city, district, latitude, longitude, text, company_id) values(?,?,?,?,?,?)";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, detail.getCity());
				stmt.setString(2, detail.getDistrict());
				stmt.setDouble(3, detail.getPointx());
				stmt.setDouble(4, detail.getPointy());
				stmt.setString(5, detail.getDetailStringContent());
				stmt.setLong(6, companyId);
				stmt.execute();
			}
			
			updateStatus(province, companyId, 1);
			
		} catch (SQLException e) {
			e.printStackTrace();
			updateStatus(province, companyId, 2);
		}
	}

	public static void saveSocketExceptinInfo(Long id, String address) {
		try {
			if(conn == null || conn.isClosed()){
				conn = getConnection();
			}
			
			String sql = "insert into statistics.gis_socket_timeout(id, address) values(?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setLong(1, id);
			stmt.setString(2, address);
			stmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Long loadMaxCompanyId(String province) {
		try {
			if(conn == null || conn.isClosed()){
				conn = getConnection();
			}
			
			String sql = "select max(company_id) as company_id from statistics."+province+"_company_gis_info";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery();
			rs.next();
			Long currId = rs.getLong("company_id");
			return currId;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return -1L;
	}
	
	
	
	public static Long[] loadDCCompanyMaxMinId(String province) {
		try {
			if(conn == null || conn.isClosed()){
				conn = getConnection();
			}
			
			String sql = "select max(id) as max, min(id) as min from dc_import."+province+"_company";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery();
			rs.next();
			Long maxId = rs.getLong("max");
			Long minId = rs.getLong("min");
			return new Long[]{minId, maxId};
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static void updateCarGisStatus(Long companyId, int status, String table) {
		try {
			if(conn == null || conn.isClosed()){
				conn = getConnection();
			}
			
			String sql = "update "+table+" set status = "+status+" where id = "+companyId;
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void updateStatus(String province, Long companyId, int status) {
		try {
			if(conn == null || conn.isClosed()){
				conn = getConnection();
			}
			
			String sql = "update test."+province+"_address set status = "+status+" where company_id = "+companyId;
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static Long[] loadTestMaxMinId(String province) {
		try {
			if(conn == null || conn.isClosed()){
				conn = getConnection();
			}
			
			String sql = "select max(id) as max, min(id) as min from test."+province+"_address where status = 0";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery();
			rs.next();
			Long maxId = rs.getLong("max");
			Long minId = rs.getLong("min");
			return new Long[]{minId, maxId};
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Long[] loadCarCompanyMaxMinId(String tableName) {
		try {
			if(conn == null || conn.isClosed()){
				conn = getConnection();
			}
			
			String sql = "select max(index_id) as max, min(index_id) as min from "+tableName+" where status != 1";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery();
			rs.next();
			Long maxId = rs.getLong("max");
			Long minId = rs.getLong("min");
			return new Long[]{minId, maxId};
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Long[] loadCarCompanyMaxMinId() {
		return loadCarCompanyMaxMinId("crawler.car_company_address");
	}

	public static int getGisThreadsCount() {
		try{
			if(conn == null || conn.isClosed()){
				conn = getConnection();
			}
			
			String sql = "select value from crawler.app_config where name = 'gis_threads';";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery();
			rs.next();
			Integer value = Integer.parseInt(rs.getString("value"));
			return value;
		}catch(Exception e){
			e.printStackTrace();
			return 50;
		}
	}
	
	
}
