package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

import bd.DBStatic;
import bd.Database;
import tools.AuthentificationTools;
import tools.ErrorJSON;
import tools.FriendTools;
import tools.MessageTools;
import tools.UserTools;

public class MessageServices {
	

	/**
	 * Returns a JSONObject that contains the message of a specific user

	 * 
	 * @param key
	 *            the authentification key of the user
	 * @param mid
	 * 			  the id of the message
	 * @param idUser
	 * 			  the id of the user who want to get the message
	 * @param     
	 *            the id of the user who has post the message   
	 * @return a JSONObject that contains the message of a specific user
	 *         
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject getMessage(String key, String mid, int idUser, int idUsercible) throws JSONException {
		Connection c = null;
		try {
			// Get the mongo connection
			c = Database.getMySQLConnection();
			MongoDatabase db = Database.getMongoDBConnection();
			MongoCollection<Document> coll = db.getCollection("message");
			//On vérifie si les arguments ne sont pas null
			if (key == null || mid == null || idUser == 0 || idUsercible == 0)
				return ErrorJSON.serviceRefused("key or mid or id user or id user targeted field empty", -1);
			//On vérifie que la clé de l'utilisateur existe
			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", 12);
			//On vérifie que l'utilisateur est touours actif
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", 6);			
			}
			
			ObjectId message_id = new ObjectId(mid);
			
			//On vérifie l'existance du message
			try {
				if(!MessageTools.existMessage(message_id, coll)){
					return ErrorJSON.serviceRefused("not found", 25);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			//On met à jour la date d'activité de l'utilisateur
			AuthentificationTools.updateSession(key);
			
			boolean isBlocked = false;
			//On vérifie si les utilisateur sont bloqués
			ArrayList<Integer> blockedUsers = UserTools.getBlockedUsers(idUser, c);
			if(!blockedUsers.isEmpty()) {
				for(int i = 0; i< blockedUsers.size();i++) {
					if(blockedUsers.get(i) == idUsercible) {
						isBlocked = true;
					}
				}
			}
			
			if(isBlocked) {
				return ErrorJSON.serviceAccepted("message"," l'utilisateur " + idUsercible + " est bloqué");
			}
			JSONArray user = UserTools.getUser(idUser, c);
			
			String message = MessageTools.getMessage(message_id, coll);
			//Retour du service accepté
			return ErrorJSON.serviceAccepted("L'utilisateur "+user+" post le message: ",  mid + " : " + message);
		} catch (JSONException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("json error : \n" + e.getMessage(), 100);
		} catch (SQLException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
		} catch (Exception e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("not found \n" + e.getMessage(), 24);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
			}
		}
	}
	
	
	/**
	 * Returns a JSONObjec which indicate if a message exist

	 * 
	 * @param key
	 *            the authentification key of the user
	 * @param mid
	 * 			  the id of the message 
	 * @return a JSONObjec which indicate if a message exist
	 *         
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject existMessage(String key, String mid) throws JSONException {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			MongoDatabase db = Database.getMongoDBConnection();
			MongoCollection<Document> coll = db.getCollection("message");
			if (key == null || mid == null)
				return ErrorJSON.serviceRefused("key or mid field empty", -1);

			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", 12);
			
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", 6);			
			}
			
			ObjectId message_id = new ObjectId(mid);
			if(!MessageTools.existMessage(message_id, coll))
				return ErrorJSON.serviceRefused("not found", 25);
			
			AuthentificationTools.updateSession(key);
			
			boolean message = MessageTools.existMessage(message_id, coll);

			return ErrorJSON.serviceAccepted("message", "message " + mid + " : " + message);
		} catch (JSONException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("json error : \n" + e.getMessage(), 100);
		} catch (SQLException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
		} catch (Exception e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("not found \n" + e.getMessage(), 24);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
			}
		}
	}
	
	
	/**
	 * Returns a JSONObject that indicate the message has been created

	 * 
	 * @param idUser
	 *            the id of the user who want to create the message
	 * @param authorNameString
	 * 			  the name of the author
	 * @param key
	 * 			  the key of the user
	 * @param content    
	 *            the content of the message   
	 * @return a JSONObject that indicate the message has been created
	 *         
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject createMessage(int idUser, String authorNameString, String key, String content) throws JSONException {
		Connection c = null;
		String mid;
		try {
			c = Database.getMySQLConnection();
			MongoDatabase db = Database.getMongoDBConnection();
			MongoCollection<Document> coll = db.getCollection("message");
			if (key == null || authorNameString == null || key == null || content == null)
				return ErrorJSON.serviceRefused("key or mid  field empty", -1);

			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", 12);
			
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", 6);			
			}
			
			AuthentificationTools.updateSession(key);

			int author_id = UserTools.getUserIdFromKey(key, c);
			String author_login = UserTools.getUserLogin(author_id, c);
			mid = MessageTools.createMessage(author_id,author_login, content, coll);


		
		} catch (JSONException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("json error : \n" + e.getMessage(), 100);
		} catch (SQLException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
		} catch (Exception e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("not found \n" + e.getMessage(), 24);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
			}
		}
	
		return ErrorJSON.serviceAccepted("mid", mid);
	}
	
	
	/**
	 * Returns a JSONObject that indicate the message has been created

	 * 
	 * @param i
	 *            the id of the user who want to update the message
	 * @param mid
	 * 			  the id of the message
	 * @param key
	 * 			  the key of the user
	 * @param content    
	 *            the content of the updated message   
	 * @return a JSONObject that indicate the message has been updated
	 *         
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject update(String key, String mid, String content, int idUser) throws JSONException {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			MongoDatabase db = Database.getMongoDBConnection();
			MongoCollection<Document> coll = db.getCollection("message");
			if (key == null || mid == null)
				return ErrorJSON.serviceRefused("key or mid field empty", -1);

			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", 12);
			
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", 6);			
			}
			
			ObjectId message_id = new ObjectId(mid);
			
			//Si le contenu est vide on suppose que le message doit être supprimé
			if(content == null) {
				MessageTools.deleteMessage(message_id, coll);
			}
			AuthentificationTools.updateSession(key);
			System.out.println("message id update  "+message_id+ "    "+idUser);
			if(MessageTools.verif(message_id,idUser,coll)) {
				System.out.println("enter 1111111111111111");
				MessageTools.updateMessage(message_id,content, coll);
			}
			

		
		} catch (JSONException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("json error : \n" + e.getMessage(), 100);
		} catch (SQLException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
		} catch (Exception e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("not found \n" + e.getMessage(), 24);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
			}
		}
	
		return ErrorJSON.serviceAccepted("message", "message " + mid + " : " + content);
	}

	
	/**
	 * Returns a JSONObject that indicate the message has been deleted

	 * 
	 * @param key
	 * 			  the key of the user
	 * @param mid    
	 *            the id of the message   
	 * @return a JSONObject that indicate the message has been created
	 *         
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject deleteMessage(String key, String mid, int idUser) throws JSONException {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			MongoDatabase db = Database.getMongoDBConnection();
			MongoCollection<Document> coll = db.getCollection("message");
			if (key == null || mid == null)
				return ErrorJSON.serviceRefused("key or mid or login field empty", -1);

			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", 12);
			
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", 6);			
			}
			
			AuthentificationTools.updateSession(key);
			ObjectId message_id = new ObjectId(mid);
			
			if(MessageTools.verif(message_id, idUser,coll)) {
				MessageTools.deleteMessage(message_id, coll);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("json error : \n" + e.getMessage(), 100);
		} catch (SQLException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
		} catch (Exception e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("not found \n" + e.getMessage(), 24);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
			}
		}
	
		return ErrorJSON.serviceAccepted("message", "message " + mid);
	}
	
	
	/**
	 * Returns a JSONObject that indicate the message has been created

	 * 
	 * @param idUsercible
	 *            the id of the user who will have a like
	 * @param key
	 * 			  the key of the user
	 * @param mid    
	 *            the id of the message   
	 * @return a JSONObject that indicate the message has been liked
	 *         
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject like(String key, String mid, int idUsercible) throws JSONException {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			MongoDatabase db = Database.getMongoDBConnection();
			MongoCollection<Document> coll = db.getCollection("message");
			if (key == null || mid == null)
				return ErrorJSON.serviceRefused("key or mid field empty", -1);

			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", 12);
			
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", 6);			
			}
			ObjectId message_id = new ObjectId(mid);
			
			
			AuthentificationTools.updateSession(key);
			
			boolean isBlocked = false;
			ArrayList<Integer> blockedUsers = UserTools.getBlockedUsers(idUsercible, c);
			if(!blockedUsers.isEmpty()) {
				for(int i = 0; i< blockedUsers.size();i++) {
					if(blockedUsers.get(i) == idUsercible) {
						isBlocked = true;
					}
				}
			}
			
			if(isBlocked) {
				return ErrorJSON.serviceAccepted("message"," l'utilisateur " + idUsercible + " est bloqué");
			}
			if(MessageTools.existMessage(message_id, coll)) {
				MessageTools.likeMessage(message_id, idUsercible, coll);
				MessageTools.idLikeMessage(message_id, idUsercible, coll);
			}
			

		
		} catch (JSONException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("json error : \n" + e.getMessage(), 100);
		} catch (SQLException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
		} catch (Exception e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("not found \n" + e.getMessage(), 24);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
			}
		}
	
		return ErrorJSON.serviceAccepted("message", "message " + mid + " : liked ");
	}
	
	/**
	 * Returns a JSONObject that get the number of like for a specific message with some stats
	 * 
	 * @param idUsercible
	 *            the id of the user who will return his likes
	 * @param key
	 * 			  the key of the user
	 * @param mid    
	 *            the id of the message   
	 * @return a JSONObject that get the number of like for a specific message with some stats a
	 *         
	 * @throws JSONException
	 *             that shouldn't happen
	 */
	public static JSONObject getLike(String key, String mid, int idUsercible) throws JSONException {
		Connection c = null;
		try {
			c = Database.getMySQLConnection();
			MongoDatabase db = Database.getMongoDBConnection();
			MongoCollection<Document> coll = db.getCollection("message");
			if (key == null || mid == null)
				return ErrorJSON.serviceRefused("key or mid field empty", -1);

			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", 12);
			
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", 6);			
			}
			
			ObjectId message_id = new ObjectId(mid);

			try {
				if(!MessageTools.existMessage(message_id, coll)){
					return ErrorJSON.serviceRefused("not found", 25);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}

			AuthentificationTools.updateSession(key);
			
			boolean isBlocked = false;
			//int idUserCible = MessageTools.getUserIdByMid(message_id, coll);
			ArrayList<Integer> blockedUsers = UserTools.getBlockedUsers(idUsercible, c);
			if(!blockedUsers.isEmpty()) {
				for(int i = 0; i< blockedUsers.size();i++) {
					if(blockedUsers.get(i) == idUsercible) {
						isBlocked = true;
					}
				}
			}
			
			if(isBlocked) {
				return ErrorJSON.serviceAccepted("message"," l'utilisateur " + idUsercible + " est bloqué");
			}
			String message = MessageTools.getMessage(message_id, coll);
			
			int like = MessageTools.getLike(message_id, coll);

			String statsLikes= MessageTools.percentAppreciationFriend(idUsercible,coll);
			System.out.println("try "+statsLikes);
			Object idLike =MessageTools.getIdLike(message_id, coll);
			System.out.println("test return array id like "+MessageTools.getIdLike(message_id, coll));
			
			return ErrorJSON.serviceAccepted("message", "message " + mid + " : " + message+" Like :"+ like+" User id: "+idLike+" \n\n  stats: "+ statsLikes);
		} catch (JSONException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("json error : \n" + e.getMessage(), 100);
		} catch (SQLException e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
		} catch (Exception e) {
			e.printStackTrace();
			return ErrorJSON.serviceRefused("not found \n" + e.getMessage(), 24);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused("sql error : \n" + e.getMessage(), 1000);
			}
		}
	}	
	
}

