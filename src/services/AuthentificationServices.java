package services;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import bd.DBStatic;
import bd.Database;
import tools.AuthentificationTools;
import tools.ErrorJSON;
import tools.UserTools;

public class AuthentificationServices {

	/**
	 * Return a JSONObject signaling that the user with the specified login has successfully logged in
	 * @param login the login of the user
	 * @param password the password of the user
	 * @return a JSONObject signaling that the user with the specified login has successfully logged in
	 * @throws JSONException that shouldn't happen
	 */
	public static JSONObject login(String login, String password) throws JSONException {
		Connection c = null;
		String key = null;
		try {
			c = Database.getMySQLConnection();
			if (login == null || password == null)
				return ErrorJSON.serviceRefused("login or password field empty", DBStatic.empty_field_error);
			
			if(!(UserTools.existUser(login,c)))
				return ErrorJSON.serviceRefused("user doesn't exist", DBStatic.not_in_db_error);
			
			
			if (!AuthentificationTools.checkPassword(login, password))
				return ErrorJSON.serviceRefused("wrong password", DBStatic.check_error);
			
			int id_user = UserTools.getUserId(login, c);
			
			
			key = AuthentificationTools.insertSession(id_user);
			
			return ErrorJSON.serviceAccepted("key", key);
			
		} catch (Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		}
		
	}

	
	/**
	 * Returns a JSONObject signaling that the user with the specified login has successfully logged out
	 * @param login the login of the user
	 * @return a JSONObject signaling that the user with the specified login has successfully logged out
	 * @throws JSONException that shouldn't happen
	 */
	public static JSONObject logout(String key) throws JSONException {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			if (key == null || key.equals(""))
				return ErrorJSON.serviceRefused("key empty", DBStatic.empty_field_error);
			if(!AuthentificationTools.existKey(key, c))
				return ErrorJSON.serviceRefused("the key doesn't exist in the database", DBStatic.not_in_db_error);
			
			AuthentificationTools.removeSession(key, c);
			String message = "The user is deconnected";
			
			return ErrorJSON.serviceAccepted("message", message);
		} catch (Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		
		}
		
	}

}
