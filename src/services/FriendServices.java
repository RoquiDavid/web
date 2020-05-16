package services;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bd.DBStatic;
import bd.Database;
import tools.AuthentificationTools;
import tools.ErrorJSON;
import tools.FriendTools;
import tools.UserTools;

public class FriendServices {

	/**
	 * Returns a JSONObject that contains the friend list of the user owning the
	 * specified login
	 * 
	 * @param login
	 *            the login of the user
	 * @return a JSONObject that contains the friend list of the user owning the
	 *         specified login
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject getFriendList(String key) throws JSONException {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			if (key == null)
				return ErrorJSON.serviceRefused("key field empty", DBStatic.empty_field_error);

			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", DBStatic.not_in_db_error);
			
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", DBStatic.outdatedkey_error);			
			}
			
			AuthentificationTools.updateSession(key);
			
			int id = UserTools.getUserIdFromKey(key, c);
			JSONArray friends = FriendTools.getFriendList(id);

			return ErrorJSON.serviceAccepted("friend list", friends);
		} catch (JSONException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("json error : \n" + e.getMessage(), DBStatic.json_error);
		} catch (SQLException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), DBStatic.sql_error);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), DBStatic.sql_error);
			}
		}
	}

	/**
	 * Returns a JSONObject that signals to the user that the specified friend has
	 * been successfully added to his friend list
	 * 
	 * @param login
	 *            the login of the user
	 * @param friend
	 *            the login of the friend
	 * @return a JSONObject that signals to the user that the specified friend has
	 *         been successfully added to his friend list
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject addFriend(String key, String friendlogin) throws JSONException {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			if(key == null || friendlogin == null)
				return ErrorJSON.serviceRefused("addFriend : argumets null", DBStatic.empty_field_error);
			if(!UserTools.existUser(friendlogin, c))
				return ErrorJSON.serviceRefused("the username selected doesn't exist", DBStatic.not_in_db_error);
			if(!AuthentificationTools.existKey(key, c))
				return ErrorJSON.serviceRefused("key doesn't exist", DBStatic.not_in_db_error);
			int myid = UserTools.getUserIdFromKey(key, c);
			int friendid = UserTools.getUserId(friendlogin, c);
			FriendTools.addFriend(myid, friendid);

			String message = "friend added";
			return ErrorJSON.serviceAccepted("message", message);
		} catch (JSONException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("json error : \n" + e.getMessage(), DBStatic.json_error);
		} catch (SQLException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), DBStatic.sql_error);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), DBStatic.sql_error);
			}
		}
	}

	/**
	 * Returns a JSONObject that signals to the user that the specified friend has
	 * been successfully removed from his friend list
	 * 
	 * @param login
	 *            the login of the user
	 * @param friend
	 *            the login of the friend
	 * @return Returns a JSONObject that signals to the user that the specified
	 *         friend has been successfully removed from his friend list
	 * @throws JSONException
	 */

	public static JSONObject removeFriend(String key, String friendlogin) throws JSONException {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			if(key == null || friendlogin == null)
				return ErrorJSON.serviceRefused("addFriend : argumets null", DBStatic.empty_field_error);
			if(!UserTools.existUser(friendlogin, c))
				return ErrorJSON.serviceRefused("the username selected doesn't exist", DBStatic.not_in_db_error);
			if(!AuthentificationTools.existKey(key, c))
				return ErrorJSON.serviceRefused("key doesn't exist", DBStatic.not_in_db_error);
			int myid = UserTools.getUserIdFromKey(key, c);
			int friendid = UserTools.getUserId(friendlogin, c);
			FriendTools.deleteFriend(myid, friendid, c);

			String message = "friend removed";
			return ErrorJSON.serviceAccepted("message", message);
		} catch (JSONException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("json error : \n" + e.getMessage(), DBStatic.json_error);
		} catch (SQLException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), DBStatic.sql_error);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), DBStatic.sql_error);
			}
		}
	}

}
