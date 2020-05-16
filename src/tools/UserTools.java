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
	 * Returns true if the login exist in the database
	 * 
	 * @param login
	 *            the login to check
	 * @param c
	 * @return true if the login exist in the database
	 * @throws SQLException
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
	 * Insert in the database the login paired with the password of the user
	 * 
	 * @param login
	 *            the login of the user
	 * @param password
	 *            the password of the user
	 * @param prenom
	 * @param nom
	 * @param c
	 * @throws SQLException
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
	 * Insert in the database the id of the blocked user
	 * 
	 * @param idCurrentUser
	 *            the id of the user
	 * @param idBlockUser
	 *            the id of the blocked user
	 * @throws SQLException
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
	 * Get the id of all blocked user by the current user
	 * 
	 * @param idCurrentUser
	 *            the id of the user
	 * @param idBlockUser
	 *            the id of the blocked user
	 * @throws SQLException
	 * @throws JSONException 
	 */
	public static ArrayList<Integer> getBlockedUsers(int idCurrentUser, Connection c)throws SQLException, JSONException {
		
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
	 * Returns all information about the user who has the specified login
	 * 
	 * @param login
	 *            the login of the user
	 * @return all information about the user who has the specified login
	 * @throws SQLException
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
	 * @param id
	 *            the id of the user
	 * @param c
	 * @throws SQLException
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
	 * @param login
	 *            the login of the user
	 * @param c
	 * @return the id paired with the specified login
	 * @throws SQLException
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

