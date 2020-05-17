package services;

import java.sql.Connection;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import bd.DBStatic;
import bd.Database;
import tools.AuthentificationTools;
import tools.CommentaireTools;
import tools.ErrorJSON;
import tools.MessageTools;
import tools.UserTools;

public class CommentaireService {
	
	/**
	 * Returns a JSONObject that contains an array of id which are the if of all commentary of the specified message
	 * @param key the key session
	 * @param m_id the message id
	 * @return a JSONObject that contains an array of id which are the if of all commentary of the specified message
	 * @throws JSONException
	 */
	public static JSONObject getCommentaireList(String key, String m_id) throws JSONException {
		Connection c  = null;
		
		try {
			c = Database.getMySQLConnection();
			MongoDatabase db = Database.getMongoDBConnection();
			
			MongoCollection<Document> coll = db.getCollection("message");
			if(key == null || m_id == null) {
				return ErrorJSON.serviceRefused("fields empty", DBStatic.empty_field_error);
			}
			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", DBStatic.not_in_db_error);
			
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", DBStatic.outdatedkey_error);			
			}
			
			AuthentificationTools.updateSession(key,c);
			
			
			ObjectId message_id = new ObjectId(m_id);
			if(!MessageTools.existMessage(message_id, coll)){
				return ErrorJSON.serviceRefused("not found", DBStatic.not_in_db_error);
			}
			
			ArrayList<String> commentsId = CommentaireTools.getCommentList(message_id, coll);
			
			return ErrorJSON.serviceAccepted("commentsId", commentsId);			
			
			
		} catch (Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		}
		
		
	}
	
	
	/**
	 * Return a JSONObject that contains data of the commentary
	 * @param key the key session
	 * @param m_id the message id
	 * @param c_id the commentary id
	 * @return a JSONObject that contains data of the commentary
	 * @throws JSONException
	 */
	public static JSONObject getCommentaire(String key, String m_id, String c_id) throws JSONException {
		Connection c  = null;
		JSONObject json = null;
		try {
			c = Database.getMySQLConnection();
			MongoDatabase db = Database.getMongoDBConnection();
			
			MongoCollection<Document> coll = db.getCollection("message");
			if(key == null || m_id == null || c_id == null) {
				return ErrorJSON.serviceRefused("fields empty", DBStatic.empty_field_error);
			}
			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", DBStatic.not_in_db_error);
			
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", DBStatic.outdatedkey_error);			
			}
			
			AuthentificationTools.updateSession(key, c);
			
			
			ObjectId message_id = new ObjectId(m_id);
			if(!MessageTools.existMessage(message_id, coll)){
				return ErrorJSON.serviceRefused("not found", DBStatic.not_in_db_error);
			}
			
			if(!CommentaireTools.existCommentaire(message_id, c_id, coll))
				return ErrorJSON.serviceRefused("not found", DBStatic.not_in_db_error);
			
			
			Document doc = CommentaireTools.getCommentaire(message_id, c_id, coll);
			json = ErrorJSON.serviceAccepted();
			ErrorJSON.addToJSON("author_id", doc.getInteger("author_id"), json);
			ErrorJSON.addToJSON("author_login", doc.getString("author_login"), json);
			ErrorJSON.addToJSON("text", doc.getString("text"), json);
			ErrorJSON.addToJSON("date", doc.get("date"), json);
			return json;
			
		} catch(Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		}
		
	}
	

	/**
	 * Insert in the database a commentary with the specified comment for the specified message
	 * @param key the key session
	 * @param m_id the message id
	 * @param comment the comment content
	 * @return a JSONObject containing the c_id of the comment
	 * @throws JSONException
	 */
	public static JSONObject createCommentaire(String key, String m_id, String comment) throws JSONException {
		Connection c  = null;
		String c_id = null;
		
		try {
			c = Database.getMySQLConnection();
			MongoDatabase db = Database.getMongoDBConnection();
			MongoCollection<Document> coll = db.getCollection("message");
			
			if(key == null || m_id == null || comment == null) {
				return ErrorJSON.serviceRefused("fields empty", DBStatic.empty_field_error);
			}
			
			
			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", DBStatic.not_in_db_error);
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", DBStatic.outdatedkey_error);			
			}
			AuthentificationTools.updateSession(key,c);
			
			Integer author_id = UserTools.getUserIdFromKey(key, c);
			String author_login = UserTools.getUserLogin(author_id, c);
			ObjectId message_id = new ObjectId(m_id);
			if(!MessageTools.existMessage(message_id, coll)){
				return ErrorJSON.serviceRefused("not found", DBStatic.not_in_db_error);
			}
			
			c_id = CommentaireTools.createCommentaire(message_id,author_id, author_login, comment, coll);
			return ErrorJSON.serviceAccepted("c_id", c_id);
			
		} catch(Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		}
		
		
	}


	/**
	 * Delete in the database a commentary with the specified c_id for the specified message m_id
	 * @param key the key session
	 * @param m_id the message id
	 * @param c_id the comment id
	 * @return a JSONObject that a message signaling that the comment has been successfully deleted
	 * @throws JSONException
	 */
	public static JSONObject deleteCommentaire(String key, String m_id, String c_id) throws JSONException {
		Connection c  = null;
		try {
			
			c = Database.getMySQLConnection();
			MongoDatabase db = Database.getMongoDBConnection();
			MongoCollection<Document> coll = db.getCollection("message");
			if(key == null || m_id == null || c_id == null) {
				return ErrorJSON.serviceRefused("fields empty", DBStatic.empty_field_error);
			}
			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", DBStatic.not_in_db_error);
			
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", DBStatic.outdatedkey_error);			
			}
			
			AuthentificationTools.updateSession(key,c);
			ObjectId message_id = new ObjectId(m_id);
			Integer author_id = UserTools.getUserIdFromKey(key, c);
			
			if(!MessageTools.existMessage(message_id, coll)){
				return ErrorJSON.serviceRefused("not found", DBStatic.not_in_db_error);
			}
		
			if(!CommentaireTools.existCommentaire(message_id, c_id, coll))
				return ErrorJSON.serviceRefused("not found", DBStatic.not_in_db_error);
			
			if(CommentaireTools.getCommentaire(message_id, c_id, coll).getInteger("author_id") != author_id)
				return ErrorJSON.serviceRefused("permission denied", DBStatic.permission_error);
			CommentaireTools.deleteCommentaire(message_id, c_id, coll);
			return ErrorJSON.serviceAccepted("message", "The comment " + c_id + " has been deleted");
			
		} catch(Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		}
		
	}


	/**
	 * Modify in the database a commentary with the specified c_id for the specified message m_id with the specified new content
	 * @param key the key session
	 * @param m_id the message_id
	 * @param c_id the comment id
	 * @return a JSONObject containing a message showing that the comment has been mofified
	 * @throws JSONException 
	 */
	public static JSONObject modifyCommentaire(String key, String m_id, String c_id, String new_comment) throws JSONException {
		Connection c  = null;
	
		try {
			c = Database.getMySQLConnection();
			MongoDatabase db = Database.getMongoDBConnection();
			MongoCollection<Document> coll = db.getCollection("message");
			
			
			if(key == null || m_id == null || c_id == null) {
				return ErrorJSON.serviceRefused("fields empty", DBStatic.empty_field_error);
			}
			if(!AuthentificationTools.existKey(key,c))
				return ErrorJSON.serviceRefused("the key doesn't exist", DBStatic.not_in_db_error);
			
			if(!UserTools.isValid(key, c)) {
				AuthentificationTools.removeSession(key, c);
				return ErrorJSON.serviceRefused("you have been disconnected, key too old", DBStatic.outdatedkey_error);			
			}
			
			AuthentificationTools.updateSession(key, c);
			ObjectId message_id = new ObjectId(m_id);
			Integer author_id = UserTools.getUserIdFromKey(key, c);
			
			if(!MessageTools.existMessage(message_id, coll)){
				return ErrorJSON.serviceRefused("not found", DBStatic.not_in_db_error);
			}
		
			if(!CommentaireTools.existCommentaire(message_id, c_id, coll))
				return ErrorJSON.serviceRefused("not found", DBStatic.not_in_db_error);
			
			if(CommentaireTools.getCommentaire(message_id, c_id, coll).getInteger("author_id") != author_id)
				return ErrorJSON.serviceRefused("permission denied", DBStatic.permission_error);
			
			CommentaireTools.modifyCommentaire(message_id, c_id, coll, new_comment);
			return ErrorJSON.serviceAccepted("message", "The commentary " + c_id +" of the message " + m_id  +"has been modified" );
		} catch(Exception e) {
			return ErrorJSON.exceptionHandler(e);
		} finally {
			Database.closeSQLConnection(c);
		}
		
		
	}


	
}
