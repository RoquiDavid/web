package tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class CommentaireTools {

	/**
	 * 
	 * @param m_id the id of the message in the collection
	 * @param comment the comment we want to create
	 * @param coll the collection the message is in
	 * @return the id of the comment generated
	 */
	public static String createCommentaire(ObjectId message_id, Integer author_id, String author_login,String comment, MongoCollection<Document> coll) {
		ArrayList<Document> comments = null;
		/*Récupération des anciens commentaires*/
			//Récupération du message
		Document queryComment = new Document();
		queryComment.append("_id", message_id);
			//Récupération des commentaire du message
		MongoCursor<Document> cursor = coll.find(queryComment).iterator();
		
		if(cursor.hasNext()) {
			
			Document tmp =  cursor.next();
			
			comments = (ArrayList<Document>) tmp.get("comments");
		}
		
		/*Création du nouveau commentaire*/
		GregorianCalendar calendar = new GregorianCalendar();
		Date date = calendar.getTime();
		Document doc2 = new Document();
		String s = new ObjectId().toHexString();
		doc2.append("c_id", s);
		doc2.append("author_id", author_id);
		doc2.append("author_login", author_login);
		doc2.append("text", comment);
		doc2.append("date", date);
		comments.add(doc2);
		
		/*Mise a jour du message*/
		Document setData = new Document();
		setData.append("comments", comments);
		Document update = new Document();
		update.append("$set", setData);
		coll.updateOne(queryComment, update);
		//System.out.println(s);
		return s;
	}

	/**
	 * 
	 * @param message_id
	 * @param c_id
	 * @param coll
	 * @return
	 */
	public static boolean existCommentaire(ObjectId message_id, String c_id, MongoCollection<Document> coll) {
		Document query = new Document();
		query.append("_id", message_id);
		MongoCursor<Document> cursor = coll.find(query).iterator();
		ArrayList<Document> comments = null;
		if(cursor.hasNext()) {
			Document tmp = cursor.next();
			comments = (ArrayList<Document> )tmp.get("comments");
			
		}
		for(Document doc : comments) {
			if(doc.getString("c_id").equals(c_id))
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param message_id
	 * @param c_id
	 * @param coll
	 * @param new_comment
	 */
	public static void modifyCommentaire(ObjectId message_id, String c_id, MongoCollection<Document> coll, String new_comment) {
		ArrayList<Document> comments = null;
		/*Récupération des anciens commentaires*/
			//Récupération du message
		Document queryComment = new Document();
		queryComment.append("_id", message_id);
			//Récupération des commentaire du message
		MongoCursor<Document> cursor = coll.find(queryComment).iterator();
		
		if(cursor.hasNext()) {
			
			Document tmp =  cursor.next();
			
			comments = (ArrayList<Document>) tmp.get("comments");
		}
		
		/*Altération du commentaire*/

		for(int i = 0; i < comments.size(); i++) {
			Document doc = comments.get(i);
			if(doc.getString("c_id").equals(c_id))
				doc.put("text", new_comment);
			
		}
	
		
		/*Mise a jour du message*/
		Document setData = new Document();
		setData.append("comments", comments);
		Document update = new Document();
		update.append("$set", setData);
		coll.updateOne(queryComment, update);
		
	}
	
	/**
	 * 
	 * @param message_id
	 * @param c_id
	 * @param coll
	 */
	public static void deleteCommentaire(ObjectId message_id, String c_id, MongoCollection<Document> coll) {
		ArrayList<Document> comments = null;
		/*Récupération des anciens commentaires*/
			//Récupération du message
		Document queryComment = new Document();
		queryComment.append("_id", message_id);
			//Récupération des commentaire du message
		MongoCursor<Document> cursor = coll.find(queryComment).iterator();
		
		if(cursor.hasNext()) {
			
			Document tmp =  cursor.next();
			
			comments = (ArrayList<Document>) tmp.get("comments");
		}
		
		/*Suppression du commentaire*/
		int index = 0;
		for(int i = 0; i < comments.size(); i++) {
			Document doc = comments.get(i);
			if(doc.getString("c_id").equals(c_id))
				index = i;
		}
		comments.remove(index);
		
		/*Mise a jour du message*/
		Document setData = new Document();
		setData.append("comments", comments);
		Document update = new Document();
		update.append("$set", setData);
		coll.updateOne(queryComment, update);
		
	}

	
	/**
	 * 
	 * @param message_id
	 * @param c_id
	 * @param coll
	 * @return
	 */
	public static Document getCommentaire(ObjectId message_id, String c_id, MongoCollection<Document> coll) {
		Document comment = null;
		ArrayList<Document> comments = null;
		/*Récupération des anciens commentaires*/
			//Récupération du message
		Document queryComment = new Document();
		queryComment.append("_id", message_id);
			//Récupération des commentaire du message
		MongoCursor<Document> cursor = coll.find(queryComment).iterator();
		
		if(cursor.hasNext()) {
			
			Document tmp =  cursor.next();
			
			comments = (ArrayList<Document>) tmp.get("comments");
		}
		
		/*Recherche du commentaire*/
		int index = 0;
		for(int i = 0; i < comments.size(); i++) {
			Document doc = comments.get(i);
			if(doc.getString("c_id").equals(c_id))
				index = i;
		}
		
		comment = comments.get(index);
		return comment;
		
	}

	/**
	 * 
	 * @param message_id
	 * @param coll
	 * @return
	 */
	public static ArrayList<String> getCommentList(ObjectId message_id, MongoCollection<Document> coll) {
		Document comment = null;
		ArrayList<Document> comments = null;
		/*Récupération des anciens commentaires*/
			//Récupération du message
		Document queryComment = new Document();
		queryComment.append("_id", message_id);
			//Récupération des commentaire du message
		MongoCursor<Document> cursor = coll.find(queryComment).iterator();
		
		if(cursor.hasNext()) {
			
			Document tmp =  cursor.next();
			
			comments = (ArrayList<Document>) tmp.get("comments");
		}
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(Calendar.HOUR, -5);
		Date date = calendar.getTime();
		ArrayList<String> commentsId = new ArrayList<>();
		for(Document doc : comments) {
			if(doc.getDate("date").after(date)) {
				commentsId.add(doc.getString("c_id"));
			}
		}
		return commentsId;
	}

	
	public static void main(String[] args) {
		Document doc = new Document();
		doc.append("test", "test");
		doc.append("test", "changed");
		System.out.println(doc);
	}
	
	
	

}
