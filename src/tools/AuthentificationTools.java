package tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import bd.Database;

public class AuthentificationTools {
	private static final int KEY_LENGTH = 32;
	private static String validChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static Random gen = new Random();
	/**
	 * Returns true if the password is the password of the specified login
	 * @param login the login of the user
	 * @param password the password of the user
	 * @return Returns true if the password is the password of the specified login
	 * @throws SQLException 
	 */
	public static boolean checkPassword(String login, String password) throws SQLException {
		//Creating the connection
		Connection c = Database.getMySQLConnection();
		
		//State the query
		String query = "select * from user where login= ? and password = ?";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setString(1, login);
		pst.setString(2, password);
		ResultSet rs = pst.executeQuery();
		
		//Treatment of the query
		boolean passwordOK = rs.next();
		
		//Closing the streams
		rs.close();
		pst.close();
		c.close();
		
		return passwordOK;
	}
	
	/**
	 * Create a session paired with the specified login and returns a random key of 32 characters
	 * @param id_user the id of the user which will be paired with the session
	 * @return the key of the session
	 * @throws SQLException 
	 */
	public static String insertSession(int id_user) throws SQLException {
		
		
		//Creating the connection
		Connection c = Database.getMySQLConnection();
		
		//Stating the query
		String query = "insert into session(id_user, key_session, time) values (? , ?, now());";
		PreparedStatement pst = c.prepareStatement(query);
		
		//Treating the query : the key should be unique so generating it while it exists
		int inserted = 0;
		String key;
		do {
			key = generateKey();
			pst.setInt(1, id_user);
			pst.setString(2, key);
			inserted = pst.executeUpdate();
		} while(inserted == 0);
		
		return key;
	}
	
	public static void updateSession(String key, Connection c) throws SQLException {
		
		//Stating the query
		String query = "update session set time = now()  where key_session = ?";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setString(1, key);
		
		//Treating the query
		pst.executeUpdate();
		
		pst.close();
	}

	/**
	 * remove the key session from the database
	 * @param keySession the key session to remove
	 * @param c2 
	 * @throws SQLException 
	 */
	public static void removeSession(String keySession, Connection c) throws SQLException {
		//Creating the connection
		
		//Stating the query
		String query = "delete from session where key_session = ?";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setString(1, keySession);
		
		//Treating the query
		pst.executeUpdate();
		pst.close();
		
	}
	
	/**
	 * create a random key session of 32 characters
	 * @return 
	 */
	private static String generateKey() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < AuthentificationTools.KEY_LENGTH; i++) {
			sb.append(validChar.charAt(gen.nextInt(validChar.length())));
		}
		return sb.toString();
		
	}

	public static boolean existKey(String key, Connection c) throws SQLException {
		String query = "select * from session where key_session = ?";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setString(1, key);
		ResultSet rs = pst.executeQuery();
		 boolean exist = rs.next();
		 rs.close();
		 pst.close();
		return exist;
	}
}
