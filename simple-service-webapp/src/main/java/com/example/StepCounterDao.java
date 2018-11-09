package com.example;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;


public class StepCounterDao {
	protected ConnectionManager conn;
	private static StepCounterDao instance = null;
	private final int NO_DATA = -1;
	
	protected StepCounterDao() {
		conn = ConnectionManager.getInstance();
	}
	
	public static StepCounterDao getInstance() {
		if (instance == null) {
			instance = new StepCounterDao();
		}
		
		return instance;
	}
	
	public String insert(CountData data) throws Exception {
		
		String insertion = "INSERT INTO Records(UserID, Day, TimeInterval, StepCount)" + "values (?,?,?,?);";
		Connection connection = conn.getConnection();
		try (//Connection connection = conn.getConnection();
			PreparedStatement insertStmt = connection.prepareStatement(insertion)) {
			insertStmt.setInt(1, data.getUserId());
			insertStmt.setInt(2, data.getDay());
			insertStmt.setInt(3, data.getInterval());
			insertStmt.setInt(4, data.getCount());
			insertStmt.executeUpdate();
			
			//conn.closeConnection(connection);
			
			return "Inserted userID: " + data.getUserId() + " day: " + data.getDay() + 
					" interval: " + data.getInterval() + " step: " + data.getCount();
		} catch (Exception e) {
			throw e;
		} finally {
			conn.closeConnection(connection);
		}
	}
	
	
	public int getCurrent(int userID) throws Exception {
		
		String getCurrDayData = "SELECT Day FROM Records WHERE UserID = ? Day = 1;";
		int day;
		
		try (Connection connection = conn.getConnection();
			PreparedStatement selectStmt = connection.prepareStatement(getCurrDayData)) {
			
			selectStmt.setInt(1, userID);
			ResultSet result = selectStmt.executeQuery();
			
			if (result.next()) {
				day = result.getInt(1);
				
				conn.closeConnection(connection);
				
				return getDay(userID, day);
			}
			
			conn.closeConnection(connection);
		} catch (Exception e) {
			throw e;
		}
		return NO_DATA;
	}
	
	
	public int getDay(int userID, int day) throws Exception {
		
		String getDayData = "SELECT SUM(StepCount) FROM Records WHERE UserID = ? AND Day = 1;";
		
		try(Connection connection = conn.getConnection();
				PreparedStatement selectStmt = connection.prepareStatement(getDayData)) {
			
			selectStmt.setInt(1, userID);
			selectStmt.setInt(2, day);
			ResultSet result = selectStmt.executeQuery();
			
			if (result.next()) {
				
				conn.closeConnection(connection);
				
				return result.getInt(1);
			}
			
			conn.closeConnection(connection);
		} catch (Exception e) {
			throw e;
		}
		
		return NO_DATA;
	}
	
	
	public List<Integer> getRange(int userID, int day, int range) throws Exception {
		
		List<Integer> counts = new ArrayList<>();
		for (int i = day; i < day + range; ++i) {
			counts.add(getDay(userID, i));
		}
		
		return counts;
	}
	
}
