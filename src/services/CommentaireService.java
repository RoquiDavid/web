package services;

import java.sql.Connection;
import java.sql.SQLException;
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
	
	
	public static JSONObject getCommentaireList(String key, String m_id) throws JSONException {
		Connection c  = null;
		JSONObject json = null;
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
			
			AuthentificationTools.updateSession(key);
			
			
			ObjectId message_id = new ObjectId(m_id);
			if(!MessageTools.existMessage(message_id, coll)){
				return ErrorJSON.serviceRefused("not found", DBStatic.not_in_db_error);
			}
			
			ArrayList<String> commentsId = CommentaireTools.getCommentList(message_id, coll);
			
			json = ErrorJSON.serviceAccepted("commentsId", commentsId);			
			
			
		} catch(SQLException e) {
			return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.sql_error);
		} catch(JSONException e) {
			return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.json_error);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.sql_error);
			}
		}
		
		return json;
	}
	
	
	/**
	 * 
	 * @param key
	 * @param m_id
	 * @param c_id
	 * @return
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
			
			AuthentificationTools.updateSession(key);
			
			
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
			
			
		} catch(SQLException e) {
			return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.sql_error);
		} catch(JSONException e) {
			return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.json_error);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.sql_error);
			}
		}
		
		return json;
	}
	

	/**
	 * 
	 * @param key
	 * @param m_id
	 * @param comment
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject createCommentaire(String key, String m_id, String comment) throws JSONException {
		Connection c  = null;
		String c_id = null;
		JSONObject json = null;
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
			AuthentificationTools.updateSession(key);
			
			Integer author_id = UserTools.getUserIdFromKey(key, c);
			String author_login = UserTools.getUserLogin(author_id, c);
			ObjectId message_id = new ObjectId(m_id);
			if(!MessageTools.existMessage(message_id, coll)){
				return ErrorJSON.serviceRefused("not found", DBStatic.not_in_db_error);
			}
			
			c_id = CommentaireTools.createCommentaire(message_id,author_id, author_login, comment, coll);
			json = ErrorJSON.serviceAccepted("c_id", c_id);
			
		} catch(SQLException e) {
			return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.sql_error);
		} catch(JSONException e) {
			return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.json_error);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.sql_error);
			}
		}
		
		return json;
	}


	/**
	 * 
	 * @param key
	 * @param m_id
	 * @param c_id
	 * @return
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
			
			AuthentificationTools.updateSession(key);
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
			
		} catch(SQLException e) {
			return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.sql_error);
		} catch(JSONException e) {
			return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.json_error);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.sql_error);
			}
		}
		
		return ErrorJSON.serviceAccepted("c_id", c_id);
	}


	/**
	 * 
	 * @param key
	 * @param m_id
	 * @param c_id
	 * @return
	 * @throws JSONException 
	 */
	public static JSONObject modifyCommentaire(String key, String m_id, String c_id, String new_comment) throws JSONException {
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
			
			AuthentificationTools.updateSession(key);
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
			
		} catch(SQLException e) {
			return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.sql_error);
		} catch(JSONException e) {
			return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.json_error);
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return ErrorJSON.serviceRefused(e.getMessage(), DBStatic.sql_error);
			}
		}
		
		return json;
	}


	
}
