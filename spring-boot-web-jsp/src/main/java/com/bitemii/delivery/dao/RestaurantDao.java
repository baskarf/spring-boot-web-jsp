package com.bitemii.delivery.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bitemii.delivery.model.OrderDetail;
import com.bitemii.delivery.model.RestaurantDetails;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mysql.cj.jdbc.MysqlDataSource;
@Component
public class RestaurantDao {
	static Map<Integer, String> restaurantMap = new HashMap<>();
	static Map<Integer, String> deliveryStaffMap = new HashMap<>();
	// 52.48.85.114
	static int nLocalPort = 3370; // local port number use to bind SSH tunnel
	static int nRemotePort = 3306;
	
	@Autowired
	MysqlConnect dao;
	
	static String getPemFile(String fileName) throws IOException {

		StringBuilder result = new StringBuilder("");
		System.out.println("MysqlConnect.class.getResource " + RestaurantDao.class.getResource("/bitemii.pem").getFile());

		File file = new File(RestaurantDao.class.getResource("/bitemii.pem").getFile());
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line;
		while ((line = br.readLine()) != null) {
			result.append(line).append("\n");
		}
		br.close();

		return result.toString();
	}
	static Session sshTunnel(String strSshUser, String strSshPassword, String strSshHost, int nSshPort, String strRemoteHost,
			int nLocalPort, int nRemotePort) {
		final JSch jsch = new JSch();
		try {
			jsch.addIdentity("bitemii.pem", getPemFile("bitemii.pem").getBytes(), null, null);

			Session session = jsch.getSession(strSshUser, strSshHost, 22);
			session.setPassword(strSshPassword);

			final Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();
			session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);
			return session;

		} catch (JSchException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	static Connection getDBConnection() throws SQLException {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("bitemii_yoyumm_user");
		dataSource.setPassword("E0wbuikknShMJ0xZ");
		dataSource.setServerName("localhost");
		dataSource.setDatabaseName("bitemii_yoyumm_new");
		dataSource.setPort(nLocalPort);

		Connection conn = dataSource.getConnection();

		return conn;
	}
	
	public static Map<Integer, String> deliveryStaff() {

		Session session;

		try {
			//session = sshTunnel("ubuntu", null, "3.16.170.89", 22, "127.0.0.1", nLocalPort, nRemotePort);

			Connection conn = getDBConnection();
			Statement stmt = conn.createStatement();

			System.out.println("conn successfully  " + conn.getCatalog());
			System.out.println(MysqlConnect.GET_DELIVERY_STAFF);
			ResultSet rs = stmt.executeQuery(MysqlConnect.GET_DELIVERY_STAFF);

			while (rs.next()) {
				deliveryStaffMap.put(rs.getInt("user_id"), rs.getString("user_email"));
			}
			System.out.println("delivery staff --->  " + deliveryStaffMap);

			rs.close();
			stmt.close();
			conn.close();
			//session.disconnect();
			System.out.println("SQL session is disconnected");
		} catch (Exception e) {
			System.out.println("SQL Connect Exception");
			e.printStackTrace();
			// Your exception handling mechanism goes here.

		}
		return restaurantMap;

	}
	
	
	public List<OrderDetail> getAllNewOrder() {
		List<OrderDetail> orderDetailsList = new ArrayList();
		Session session;

		try {

			//Map<Integer, String> restNmaes = this.loadRestNames();
			session = dao.doSshTunnel("ubuntu", null, "3.16.170.89", 22, "127.0.0.1", nLocalPort, nRemotePort);

			Connection conn = dao.getConnection();
			Statement stmt = conn.createStatement();

			System.out.println("conn successfully  " + conn.getCatalog());
			System.out.println(MysqlConnect.GET_NEW_ORDER);
			ResultSet rs = stmt.executeQuery(MysqlConnect.GET_NEW_ORDER);

			while (rs.next()) {

				orderDetailsList.add(new OrderDetail(rs.getString("order_id"), rs.getString("order_user_id"),
						restaurantMap.get(rs.getInt("order_restaurant_id")), rs.getString("order_user_phone"),
						rs.getDate("order_date"), rs.getString("order_payment_method"),
						rs.getString("order_process_status"), rs.getDouble("order_total_amount"),
						deliveryStaffMap.get(rs.getInt("order_deliverystaff_id"))));
			}

			rs.close();
			stmt.close();
			conn.close();
			session.disconnect();
			System.out.println("SQL session is disconnected");
		} catch (Exception e) {
			System.out.println("SQL Connect Exception");
			e.printStackTrace();
			// Your exception handling mechanism goes here.

		}
		return orderDetailsList;
	}
	
	
	public static Map<Integer, String> getRestaurantNames() {

		//Map<Integer, String> restaurantMap = new HashMap<>();
		Session session;

		try {
			//session = sshTunnel("ubuntu", null, "3.16.170.89", 22, "127.0.0.1", nLocalPort, nRemotePort);

			Connection conn = getDBConnection();
			Statement stmt = conn.createStatement();

			System.out.println("conn successfully  " + conn.getCatalog());
			System.out.println(MysqlConnect.GET_REST_NAME);
			ResultSet rs = stmt.executeQuery(MysqlConnect.GET_REST_NAME);

			while (rs.next()) {
				restaurantMap.put(rs.getInt("restaurantlang_restaurant_id"), rs.getString("restaurant_name"));
			}
			System.out.println("restaurantMap --->  " + restaurantMap);

			rs.close();
			stmt.close();
			conn.close();
			//session.disconnect();
			System.out.println("SQL session is disconnected");
		} catch (Exception e) {
			System.out.println("SQL Connect Exception");
			e.printStackTrace();
			// Your exception handling mechanism goes here.

		}
		return restaurantMap;

	}

	/*public static void main(String args[]){
		getRestaurantNames();
		deliveryStaff();
		System.out.println(restaurantMap+" values " + deliveryStaffMap);
    }*/
}
