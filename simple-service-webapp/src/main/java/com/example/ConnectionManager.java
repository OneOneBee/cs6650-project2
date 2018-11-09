package com.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;


public class ConnectionManager {
	private static final String username = "audreyniu";
//	private static final String username = "root";
	private static final String password = "neucs6650";
//	private static final String password = "Putongmima@1";
	private static final String host = "cs6650p2db2.cqiiwgdmulxt.us-west-2.rds.amazonaws.com";
//	private static final String host = "127.0.0.1";
	private static final String port = "3306";
	private static final String schema = "StepCounter";
	private static BasicDataSource ds;
	private static ConnectionManager connManager = new ConnectionManager();
	
//	private ComboPooledDataSource dataSource;
//	
	public static ConnectionManager getInstance() {
		ds = new BasicDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://" + host + ":" + port + "/" + schema);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setInitialSize(128);
		ds.setMinIdle(2);
		
		return connManager != null ? connManager : new ConnectionManager();
	}
//	
//	private ConnectionManager() {
//		try {
//			dataSource = new ComboPooledDataSource();
//			dataSource.setDriverClass("com.mysql.jdbc.Driver");
//			dataSource.setJdbcUrl("jdbc.mysql://" + host + ":" + port + "/" + schema);
//			dataSource.setUser(username);
//			dataSource.setPassword(password);
//			dataSource.setMaxPoolSize(128);
//			dataSource.setMaxIdleTime(2);
//		} catch (PropertyVetoException e) {
//			e.printStackTrace();
//		}
//	}
//	
	
	
	public Connection getConnection() { //throws Exception {
//
//		try {
//			Properties connProperties = new Properties();
//			connProperties.put("user", ConnectionManager.username);
//			connProperties.put("password", ConnectionManager.password);
//			connProperties.put("useSSL", "false");
//			
//			String connectionURL = "jdbc:mysql://" + ConnectionManager.host + ":" + ConnectionManager.port + "/" + ConnectionManager.schema;
//			
//			Connection connection = null;
//			
//			Class.forName("com.mysql.jdbc.Driver").newInstance();
//			connection = DriverManager.getConnection(connectionURL, connProperties);
//			
//			return connection;
//			
//		} catch (SQLException e) {
//			Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, e.getMessage(), e);
//			throw e;
//		} catch (Exception e) {
//			Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, e.getMessage(), e);
//			throw e;
//		}
		Connection conn = null;
		try {
			conn = ds.getConnection();
			return conn;
		} catch (SQLException e) {
			Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
		
		return conn;
		
	}
	
	
	public void closeConnection(Connection connection) throws Exception {
		try {
			connection.close();
		} catch (SQLException e) {
			Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw e;
		} catch (Exception e) {
			Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}
	
}
