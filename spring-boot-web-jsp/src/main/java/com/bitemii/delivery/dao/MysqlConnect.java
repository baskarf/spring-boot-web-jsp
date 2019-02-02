package com.bitemii.delivery.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.bitemii.delivery.model.DeliveryStaff;
import com.bitemii.delivery.model.OrderDetail;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mysql.cj.jdbc.MysqlDataSource;

import ch.qos.logback.core.net.SyslogOutputStream;

@Component
public class MysqlConnect {
	//
	static final String GET_REST_NAME = "select * from  bitemii_yoyumm_new.tbl_restaurants_lang;";
	static final String ORDER_EXIST = "SELECT * FROM bitemii_yoyumm_new.tbl_order_deliverystaff where od_order_id= ? ";

	static final String GET_NEW_ORDER = "SELECT * FROM bitemii_yoyumm_new.tbl_orders WHERE  order_type=1 and DATE(order_date) > ADDDATE( CURDATE( ) , INTERVAL -60 MINUTE  )";
	static final String GET_DELIVERY_STAFF = "SELECT * FROM bitemii_yoyumm_new.tbl_users where user_is_merchant=3 and user_active=1";
	static final String ASSIGNED_DELIVERY_STAFF = "SELECT * FROM bitemii_yoyumm_new.tbl_order_deliverystaff  WHERE DATE (od_date) > ADDDATE( CURDATE( ) , INTERVAL -1 DAY ) ; ";
	static final String ASSIGN_ORDER_TO_DELIVERY_STAFF = "INSERT INTO bitemii_yoyumm_new.tbl_order_deliverystaff (od_order_id, od_user_id,od_date,od_assigned_by)	VALUES(?,?,?,?)";
	static final String UPDATE_ORDER_TO_DELIVERY_STAFF = "UPDATE bitemii_yoyumm_new.tbl_orders set order_deliverystaff_id=? where order_id=? ";
	static final String UNASSIGNED_DELIVERY_STAFF = "SELECT distinct t1.user_id,t1.user_email FROM bitemii_yoyumm_new.tbl_users t1 LEFT JOIN bitemii_yoyumm_new.tbl_order_deliverystaff t2 ON t1.user_id = t2.od_user_id WHERE t1.user_is_merchant=3  and t1.user_active=1 and ( DATE(t2.od_date) < ADDDATE( CURDATE( ) ,  INTERVAL -3 HOUR) or t2.od_user_id IS NULL) ;";
	static final String UNASSIGNED_ORDER = "select * from bitemii_yoyumm_new.tbl_orders where order_deliverystaff_id = 0 and order_type=1 and order_process_status=1 and DATE(order_date) > ADDDATE( CURDATE( ) , INTERVAL -15 MINUTE ) ;";
	static final String ALL_DELIVERY_STAFF = "SELECT * FROM bitemii_yoyumm_new.tbl_order_deliverystaff  WHERE DATE (od_date) > ADDDATE( CURDATE( ) ,  INTERVAL -60 MINUTE ) ; ";

	// 52.48.85.114
	static int nLocalPort = 3370; // local port number use to bind SSH tunnel
	static int nRemotePort = 3306;

	String getPem(String fileName) throws IOException {

		StringBuilder result = new StringBuilder("");
		System.out.println("MysqlConnect.class.getResource " + getClass().getResource("/bitemii.pem").getFile());

		File file = new File(getClass().getResource("/bitemii.pem").getFile());
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line;
		while ((line = br.readLine()) != null) {
			result.append(line).append("\n");
		}
		br.close();

		return result.toString();
	}

	Session doSshTunnel(String strSshUser, String strSshPassword, String strSshHost, int nSshPort, String strRemoteHost,
			int nLocalPort, int nRemotePort) {
		final JSch jsch = new JSch();
		try {
			jsch.addIdentity("bitemii.pem", getPem("bitemii.pem").getBytes(), null, null);

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

	/*
	 * public static void main(String args[]) throws JSchException, IOException{
	 * MysqlConnect mysqlConnect=new MysqlConnect(); System.out.println(
	 * mysqlConnect.getNewOrder());
	 * 
	 * }
	 */

	public List<OrderDetail> getNewOrder() {
		List<OrderDetail> orderDetailsList = new ArrayList();
		Session session;

		try {

			Map<Integer, String> restNmaes = this.loadRestNames();
			session = doSshTunnel("ubuntu", null, "3.16.170.89", 22, "127.0.0.1", nLocalPort, nRemotePort);

			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			System.out.println("conn successfully  " + conn.getCatalog());
			System.out.println(UNASSIGNED_ORDER);
			ResultSet rs = stmt.executeQuery(UNASSIGNED_ORDER);

			while (rs.next()) {

				orderDetailsList.add(new OrderDetail(rs.getString("order_id"), rs.getString("order_user_id"),
						restNmaes.get(rs.getInt("order_restaurant_id")), rs.getString("order_user_phone"),
						rs.getDate("order_date"), rs.getString("order_payment_method"),
						rs.getString("order_process_status"), rs.getDouble("order_total_amount"),
						rs.getString("order_deliverystaff_id")));
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

	public List<DeliveryStaff> getDeliveryStaff() {
		List<DeliveryStaff> orderDetailsList = new ArrayList();
		Session session;

		try {

			session = doSshTunnel("ubuntu", null, "3.16.170.89", 22, "127.0.0.1", nLocalPort, nRemotePort);
			Connection conn = getConnection();

			Statement stmt = conn.createStatement();

			System.out.println("conn successfully  " + conn.getCatalog());
			System.out.println(UNASSIGNED_DELIVERY_STAFF);
			ResultSet rs = stmt.executeQuery(UNASSIGNED_DELIVERY_STAFF);

			while (rs.next()) {

				// result = rs.getString("order_id");user_email
				// System.out.println("result: " + result);

				orderDetailsList.add(new DeliveryStaff(rs.getString("user_id"), rs.getString("user_email")));
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

	public List<DeliveryStaff> getAllDeliveryStaff() {
		List<DeliveryStaff> orderDetailsList = new ArrayList();
		Session session;

		try {

			session = doSshTunnel("ubuntu", null, "3.16.170.89", 22, "127.0.0.1", nLocalPort, nRemotePort);
			Connection conn = getConnection();

			Statement stmt = conn.createStatement();

			System.out.println("conn successfully  " + conn.getCatalog());
			System.out.println(ALL_DELIVERY_STAFF);
			ResultSet rs = stmt.executeQuery(ALL_DELIVERY_STAFF);

			while (rs.next()) {

				// result = rs.getString("order_id");user_email
				// System.out.println("result: " + result);

				orderDetailsList.add(new DeliveryStaff(rs.getString("user_id"), rs.getString("user_email")));
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

	public List<DeliveryStaff> getAssignedDeliveryStaff() {
		List<DeliveryStaff> deliveryStaffList = new ArrayList();
		Session session;

		try {

			session = doSshTunnel("ubuntu", null, "3.16.170.89", 22, "127.0.0.1", nLocalPort, nRemotePort);
			Connection conn = getConnection();

			Statement stmt = conn.createStatement();

			System.out.println("conn successfully  " + conn.getCatalog());
			System.out.println(ASSIGNED_DELIVERY_STAFF);
			ResultSet rs = stmt.executeQuery(ASSIGNED_DELIVERY_STAFF);

			while (rs.next()) {
				deliveryStaffList.add(new DeliveryStaff(rs.getString("user_id"), rs.getString("user_email")));
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
		return deliveryStaffList;
	}

	protected Connection getConnection() throws SQLException {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("bitemii_yoyumm_user");
		dataSource.setPassword("E0wbuikknShMJ0xZ");
		dataSource.setServerName("localhost");
		dataSource.setDatabaseName("bitemii_yoyumm_new");
		dataSource.setPort(nLocalPort);

		Connection conn = dataSource.getConnection();

		return conn;
	}

	public void assignOrderToDeliveryStaff() {
		List<OrderDetail> unAssignedOrders = getNewOrder();
		List<DeliveryStaff> unAssignedStaffs = getDeliveryStaff();
		System.out.println("unAssignedOrders " + unAssignedOrders);
		System.out.println("unAssignedStaffs " + unAssignedStaffs);

		for (int i = 0; i < unAssignedStaffs.size(); i++) {
			for (int j = 0; j < unAssignedOrders.size(); j++) {
				String orderId = unAssignedOrders.get(j).getOrderId();
				int deliveryStaff = Integer.parseInt(unAssignedStaffs.get(i).getUserId());
				int assignedBy = 200;
				if (!isOrderAssignedAlready(orderId)) {
					assignOrderToDeliveryStaff(orderId, deliveryStaff, assignedBy);
				}
			}
		}

	}

	public void assignOrderToDeliveryStaff(final String orderId, final int deliveryStaff, final int assignedBy) {
		Session session;
		PreparedStatement preparedStatement = null;
		try {

			session = doSshTunnel("ubuntu", null, "3.16.170.89", 22, "127.0.0.1", nLocalPort, nRemotePort);
			Connection conn = getConnection();
			preparedStatement = conn.prepareStatement(ASSIGN_ORDER_TO_DELIVERY_STAFF);

			preparedStatement.setInt(2, deliveryStaff);
			preparedStatement.setString(1, orderId);
			preparedStatement.setInt(4, assignedBy);
			preparedStatement.setTimestamp(3, getCurrentTimeStamp());

			// execute insert SQL stetement
			preparedStatement.executeUpdate();
			preparedStatement = conn.prepareStatement(UPDATE_ORDER_TO_DELIVERY_STAFF);
			preparedStatement.setInt(1, deliveryStaff);
			preparedStatement.setString(2, orderId);

			preparedStatement.executeUpdate();

			System.out.println("Record is inserted into DBUSER table!");
			preparedStatement.close();
			conn.close();
			session.disconnect();
			System.out.println("SQL session is disconnected");
		} catch (Exception e) {
			System.out.println("SQL Connect Exception");
			e.printStackTrace();
			// Your exception handling mechanism goes here.

		}

	}

	private static java.sql.Timestamp getCurrentTimeStamp() {

		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());

	}

	public Map<Integer, String> loadRestNames() {

		Map<Integer, String> restaurantMap = new HashMap<>();
		Session session;

		try {
			session = doSshTunnel("ubuntu", null, "3.16.170.89", 22, "127.0.0.1", nLocalPort, nRemotePort);

			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			System.out.println("conn successfully  " + conn.getCatalog());
			System.out.println(GET_REST_NAME);
			ResultSet rs = stmt.executeQuery(GET_REST_NAME);

			while (rs.next()) {
				restaurantMap.put(rs.getInt("restaurantlang_restaurant_id"), rs.getString("restaurant_name"));
			}
			System.out.println("restaurantMap --->  " + restaurantMap);

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
		return restaurantMap;

	}

	public Map<Integer, String> loadDeliveryStaff() {

		Map<Integer, String> restaurantMap = new HashMap<>();
		Session session;

		try {
			session = doSshTunnel("ubuntu", null, "3.16.170.89", 22, "127.0.0.1", nLocalPort, nRemotePort);

			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			System.out.println("conn successfully  " + conn.getCatalog());
			System.out.println(GET_REST_NAME);
			ResultSet rs = stmt.executeQuery(GET_REST_NAME);

			while (rs.next()) {
				restaurantMap.put(rs.getInt("restaurantlang_restaurant_id"), rs.getString("restaurant_name"));
			}
			System.out.println("delivery staff --->  " + restaurantMap);

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
		return restaurantMap;

	}

	public boolean isOrderAssignedAlready(String orderId) {
		Session session;
		boolean result = false;
		try {

			session = doSshTunnel("ubuntu", null, "3.16.170.89", 22, "127.0.0.1", nLocalPort, nRemotePort);
			Connection conn = getConnection();

			Statement stmt = conn.createStatement();
			String query="SELECT EXISTS ( SELECT 1 FROM bitemii_yoyumm_new.tbl_order_deliverystaff where od_order_id= '"+orderId + "') ;";
			System.out.println("conn successfully  " + conn.getCatalog());
			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			int rowCount = -1;
			while (rs.next()) {
				rowCount = rs.getInt(1);
				//String str = rs.getString("od_order_id");
				System.out.println("Order assigned already : " + rowCount );
				if(rowCount==1) result = true;
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
		return result;
	}
	
	/*public static void main(String args[]){
		MysqlConnect obj=new MysqlConnect();
		String[] strArray=new String[]{"YYM1549006984","YYM1549006982","YYM1549006986","YYM1549006989"};
		for(String str:strArray){
		System.out.println(obj.isOrderAssignedAlready(str));
		}
	}*/
}
