package com.proudsmart.ark.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DB3307Util {
	private static String url = "jdbc:mysql://192.168.1.207:3307/kpi";
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
		if(conn == null)
			conn = getConnection();
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
	
	public static void executeUpdate(String sql){
		if(conn == null)
			conn = getConnection();
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveTmpData(Long id, String toJson) {
		if(conn == null)
			conn = getConnection();
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("insert into tmp_data(id, data) values (?, ?)");
			stmt.setLong(1, id);
			stmt.setString(2, toJson);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveProvinceStation(String province, String station, String url){
		if(conn == null)
			conn = getConnection();
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("insert into test.province_station(province, station, url) values (?, ?, ?)");
			stmt.setString(1, province);
			stmt.setString(2, station);
			stmt.setString(3, url);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveCheciInfo(Long stationId, String stationName, String checiStr, String suffix,
			String liecheLeixing, String shifazhan, String shifaShijian, String jingguozhan, String jingguoDaodaShijian,
			String jingguoFacheShijian, String zhongdianzhan, String daodashijian) {
		String sql = "INSERT INTO test.train"+
			" (station_id, station_name, checi, checi_url, lieche_leixing, shifazhan, shifa_shijian, jingguozhan, jingguo_daoda_shijian, jingguo_fache_shijian, zhongdianzhan, zhongdian_daoda_shijian)"+
			" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		if(conn == null)
			conn = getConnection();
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			int index = 1;
			stmt.setLong(index++, stationId);
			stmt.setString(index++, stationName);
			stmt.setString(index++, checiStr);
			stmt.setString(index++, suffix);
			stmt.setString(index++, liecheLeixing);
			stmt.setString(index++, shifazhan);
			stmt.setString(index++, shifaShijian);
			stmt.setString(index++, jingguozhan);
			stmt.setString(index++, jingguoDaodaShijian);
			stmt.setString(index++, jingguoFacheShijian);
			stmt.setString(index++, zhongdianzhan);
			stmt.setString(index++, daodashijian);
			
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static int size = 0;

	public static void saveCheciStationInfo(Long trainId, String checi, String stationHref, String stationName, String daodaShijian,
			String facheshijian, String licheng) {
		String sql = "INSERT INTO test.train_daozhan"
				+" (train_id, checi, station_href, station_name, daoda_shijian, fache_shijian, licheng)"
				+" VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		if(conn == null)
			conn = getConnection();
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			int index = 1;
			stmt.setLong(index++, trainId);
			stmt.setString(index++, checi);
			stmt.setString(index++, stationHref);
			stmt.setString(index++, stationName);
			stmt.setString(index++, daodaShijian);
			stmt.setString(index++, facheshijian);
			stmt.setString(index++, licheng);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveTrainAtmtInfo(Long trainId, String checi, String quanchengHaoshi, String quanchengJuli,
			String quanchengJiage) {
		String sql = "INSERT INTO test.train_atmt "+
				"(id, checi, quancheng_haochi, quancheng_juli, jiage_info) "+
				"VALUES (?, ?, ?, ?, ?)";
		if(conn == null)
			conn = getConnection();
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			int index = 1;
			stmt.setLong(index++, trainId);
			stmt.setString(index++, checi);
			stmt.setString(index++, quanchengHaoshi);
			stmt.setString(index++, quanchengJuli);
			stmt.setString(index++, quanchengJiage);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static Long[] loadMaxMinId(String table) {
		try {
			if(conn == null || conn.isClosed()){
				conn = getConnection();
			}
			
			String sql = "select max(id) as max, min(id) as min from test."+table;
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
	
}
