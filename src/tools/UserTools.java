package tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserTools {
	/**
	 * Returns true if the login exist in the database provided by the connection
	 * @param login the login of the user
	 * @param c the connection linked to the database
	 * @return true if the login exist in the database
	 * @throws SQLException if the query isn't properly executed
	 */
	public static boolean existUser(String login, Connection c) throws SQLException {

		String query = "select login from user where login= ?";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setString(1, login);

		ResultSet rs = pst.executeQuery();
		boolean exist = rs.next();

		pst.close();
		rs.close();

		return exist;

	}

	/**
	 * Insert the specified data in the database
	 * @param login the login of the user
	 * @param password the password of the user
	 * @param nom the name of the user
	 * @param prenom the surname of the user
	 * @param c the connection linked to the database
	 * @throws SQLException if the query isn't properly executed
	 */
	public static void insertUser(String login, String password, String nom, String prenom, Connection c)
			throws SQLException {

		String query = "insert into user (login, password, nom, prenom) values(?, ?, ?, ?);";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setString(1, login);
		pst.setString(2, password);
		pst.setString(3, nom);
		pst.setString(4, prenom);

		pst.executeUpdate();

		pst.close();

	}
	
	/**
	 * Insert in the database the couple of id specified
	 * @param idCurrentUser the id of the user
	 * @param idBlockUser the id of the blocked user
	 * @param c the connection linked to the database
	 * @throws SQLException if the query isn't properly executed
	 */
	public static void blockUser(int idCurrentUser, int idBlockUser, Connection c)throws SQLException {

		String query = "insert into block (idCurrentUser, idBlockedUser) values(?, ?);";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setInt(1, idCurrentUser);
		pst.setInt(2, idBlockUser);
		pst.executeUpdate();
		pst.close();
	}
	
	/**
	 * Get all user who were blacklisted by the current user
	 * 
	 * @param idCurrentUser the id of the user       
	 *            
	 * @throws SQLException if the query isn't properly executed
	 */
	public static ArrayList<Integer> getBlockedUsers(int idCurrentUser, Connection c)throws SQLException {
		
		ArrayList<Integer> usersBlocked = new ArrayList<Integer>();
		String query = "select idBlockedUser from block where idCurrentUser = ?;";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setInt(1, idCurrentUser);
		
		ResultSet rs = pst.executeQuery();
		while (rs.next()) {
			int id = rs.getInt("idBlockedUser");
			usersBlocked.add(id);
		}

		rs.close();
		pst.close();

		return usersBlocked;

	}

	/**
	 * Get the data of the specified user
	 * @param id the id of the user
	 * @param c the connection linked to the database
	 * @return a JSONObject containing the name and the surname of the user
	 * @throws SQLException if the query isn't properly executed
	 * @throws JSONException 
	 */
	public static JSONArray getUser(int id, Connection c) throws SQLException, JSONException {


		String query = "select prenom, nom from user where id =  ?";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setInt(1, id);

		ResultSet rs = pst.executeQuery();
		rs.next();
		JSONArray userInfo = new JSONArray();
		String nom = rs.getString("nom");
		String prenom = rs.getString("prenom");
		
		
		userInfo.put(new JSONObject().put("nom", nom).put("prenom", prenom));

		rs.close();
		pst.close();

		return userInfo;
	}

	/**
	 * Returns a list containing the login of all user of the website
	 * 
	 * @return a list containing the login of all user of the website
	 * @throws SQLException
	 * @throws JSONException
	 */
	public static JSONArray getUserList(Connection c) throws SQLException, JSONException {
		JSONArray userList = new JSONArray();


		String query = "select prenom, nom from user;";
		PreparedStatement st = c.prepareStatement(query);

		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			String nom = rs.getString("nom");
			String prenom = rs.getString("prenom");
			userList.put(new JSONObject().put("nom", nom).put("prenom", prenom));
		}

		rs.close();
		st.close();

		return userList;

	}

	/**
	 * remove all data of the user with specified login in the database
	 * 
	 * @param id the id of the user
	 *            
	 * @param c the connection linked to the database
	 * @throws SQLException Something went wrong when query was executed
	 */
	public static void deleteUser(int id, Connection c) throws SQLException {

		String query = "delete from user where id = ?";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setInt(1, id);

		pst.executeUpdate();

		pst.close();

	}

	/**
	 * Returns the id paired with the specified login
	 * 
	 * @param login the login of the user
	 *            
	 * @param c the connection paired with the database
	 * @return the id paired with the specified login
	 * @throws SQLException if something went wrong when the query was executed
	 */
	public static int getUserId(String login, Connection c) throws SQLException {

		// Creation of the connection

		String query = "select id from user where login = ?";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setString(1, login);

		ResultSet rs = pst.executeQuery();
		rs.next();
		int id = rs.getInt("id");

		rs.close();
		pst.close();

		return id;

	}

	/**
	 * Get the id of the user using the key session and the database paired with the connection
	 * @param key the key session of the suer
	 * @param c the connection paired with the connection
	 * @return the id of the user who own the key 
	 * @throws SQLException if the specified arguments are wrong 
	 */
	public static int getUserIdFromKey(String key, Connection c) throws SQLException {

		String query = "select id_user from session where key_session = ?";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setString(1, key);

		ResultSet rs = pst.executeQuery();
		rs.next();
		int id = rs.getInt("id_user");

		rs.close();
		pst.close();

		return id;
	}

	/**
	 * Check if the key session is still valid
	 * @param key the key session of the user
	 * @param c the connection paired with the database
	 * @return true if the last time the key session was used was under 30 minutes
	 * @throws SQLException if the arguments are wrong
	 */
	public static boolean isValid(String key, Connection c) throws SQLException {


		String query = "select time from session where key_session = ?";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setString(1, key);

		ResultSet rs = pst.executeQuery();
		rs.next();
		Timestamp keyTime = rs.getTimestamp("time");

		java.util.Date date = new java.util.Date();
		Timestamp timestamp1 = new Timestamp(date.getTime());
		boolean valid =(timestamp1.getTime() - keyTime.getTime()) < 30 * 60 * 1000 ;
		pst.close();
		rs.close();
		return valid;
	}


	/**
	 * Get the user login through the id of the user
	 * @param author_id the id of the user
	 * @param c the connection paired with the database
	 * @return the login of the user
	 * @throws SQLException if the arguments are wrong
	 */
	public static String getUserLogin(Integer author_id, Connection c) throws SQLException {
		String query = "select login from user where id = ?";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setString(1, String.valueOf(author_id));
		
		ResultSet rs = pst.executeQuery();
		rs.next();
		String login = rs.getString("login");
		return login;
	}

}

