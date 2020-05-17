package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bd.DBStatic;
import bd.Database;
import tools.AuthentificationTools;
import tools.ErrorJSON;
import tools.UserTools;

public class UserServices {

	/**
	 * Returns a JSONObject that contains a message
	 * 
	 * @param login
	 *            the login of the user
	 * @param password
	 *            the password of the user
	 * @param prenom
	 * @param nom
	 * @return a JSONObject that contains a message
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject createUser(String login, String password, String nom, String prenom) throws JSONException {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			if (login == null || password == null || nom == null || prenom == null)
				return ErrorJSON.serviceRefused("field(s) empty", DBStatic.empty_field_error);

			if (UserTools.existUser(login, c))
				return ErrorJSON.serviceRefused("login already used by another user", DBStatic.already_in_db_error);

			UserTools.insertUser(login, password, nom, prenom, c);

			return ErrorJSON.serviceAccepted("message", "the user has been successfully created");
		} catch (Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		}

	}

	/**
	 * Returns a JSONObject signaling that the user with the specified login has
	 * successfully deleted its account
	 * 
	 * @param login
	 *            the login of the user
	 * @return a JSONObject signaling that the user with the specified login has
	 *         successfully deleted its account
	 * @throws JSONException
	 *             that should happen
	 */
	public static JSONObject deleteUser(String key) throws JSONException {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			if (key == null) {
				return ErrorJSON.serviceRefused("Null key", DBStatic.empty_field_error);
			}

			if(!AuthentificationTools.existKey(key, c))
				return ErrorJSON.serviceRefused("the key doesn't exist", DBStatic.not_in_db_error);
			int id = UserTools.getUserIdFromKey(key, c);
			UserTools.deleteUser(id, c);

			String message = "the account of the user " + key + " has been successfully deleted";
			return ErrorJSON.serviceAccepted("message", message);
		} catch (Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		}
	}

	/**
	 * Returns a JSONObject that contains the data of the user of the specified
	 * login
	 * 
	 * @param login
	 *            the login of the user
	 * @return a JSONObject that get the data of the user of the specified login
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject getUser(String login) throws JSONException {
		JSONArray userInfo;
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			if (!(UserTools.existUser(login, c)))
				return ErrorJSON.serviceRefused("the user doesn't exist", DBStatic.not_in_db_error);
			int id = UserTools.getUserId(login, c);
			userInfo = UserTools.getUser(id, c);
		} catch (Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		}

		return ErrorJSON.serviceAccepted("user", userInfo);
	}
	
	
	/**
	 * Returns a JSONObject that contains the data of the user of the specified
	 * login
	 * 
	 * @param login
	 *            the login of the user
	 * @return a JSONObject that get the data of the user of the specified login
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject blockUser(String key, String loginBlockUser) throws JSONException {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			if (key == null  || loginBlockUser == null) {
				return ErrorJSON.serviceRefused("Null key or login user or login which will be blocked", DBStatic.empty_field_error);
			}
			
			//On vérifie que la clé de l'utilisateur existe
			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", 12);
			//On vérifie que l'utilisateur est touours actif
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", 6);			
			}
			
			AuthentificationTools.updateSession(key);
			
			
			//Récupère le id des utilisateurs concernés
			int idCurrentUser = UserTools.getUserIdFromKey(key, c);
			int idBlockUser = UserTools.getUserId(loginBlockUser, c);
			boolean alreadyBlocked = false;
			ArrayList<Integer> idBlockedUser = new ArrayList<Integer>();
			idBlockedUser = UserTools.getBlockedUsers(idCurrentUser, c);
			for(int i = 0; i<idBlockedUser.size();i++) {
				if(idBlockUser==idBlockedUser.get(i)) {
					alreadyBlocked = true;
					System.out.println("user already blocked");
					return ErrorJSON.serviceRefused("Duplicate blocked user", DBStatic.sql_error);
				}
			}
			if(alreadyBlocked!=true) {
				UserTools.blockUser(idCurrentUser, idBlockUser, c);
			}
			
			
		} catch (Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		}

		return ErrorJSON.serviceAccepted("user blocked", loginBlockUser);
	}
	
	

	/**
	 * return a JSONObject that contains the login of all user of the website
	 * 
	 * @return a JSONObject that contains the login of all user of the website
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject getUserList() throws JSONException {
		JSONArray users;
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			users = UserTools.getUserList(c);
			return ErrorJSON.serviceAccepted("user list", users);
		} catch (Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		}

	}
	

	public static JSONObject getUserBlockList(String key) throws JSONException {
		ArrayList<Integer> users = new ArrayList<Integer>();
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			int idUser = UserTools.getUserIdFromKey(key, c);
			
			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", 12);
			
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", 6);			
			}
			
			AuthentificationTools.updateSession(key);
			//boolean existUser = UserTools.existUser(login, c);
			//if(existUser) {
				users = UserTools.getBlockedUsers(idUser, c);
			//}
			
			return ErrorJSON.serviceAccepted("user list", users);
		} catch (Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		}

	}
}

