package tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONException;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;


public class MessageTools{

	
	/**
	 * Returns a string that contains the message with the specified mid located in the specified collection
	 * 
	 * @param mid  message id
	 * @param coll the mongo collection   
	 * @return a String contain the message wanted
	 *         
	 * @throws JSONException that shouldn't happen
	 */
	public static String getMessage(ObjectId mid, MongoCollection<Document> coll) throws Exception{
		Document doc = new Document();
		doc.append("_id", mid);
		System.out.println(doc.toString());
		MongoCursor<Document> cursor = coll.find(doc).iterator();
		String message = null;
		if(cursor.hasNext()) {
			message = cursor.next().getString("contenu");
		} else {
			throw new Exception();
		}
		return message;
		
	}
	
	/**
	 * Returns the id of the author of the message with the specified mid located in the specified collection
	 * 
	 * @param mid message id
	 * @param coll the mongo collection   
	 * @return Returns the id of the author of the message with the specified mid located in the specified collection
	 * @throws JSONException that shouldn't happen
	 */
	public static int getMessageAuthor(ObjectId mid, MongoCollection<Document> coll) throws Exception{
		Document doc = new Document();
		doc.append("_id", mid);
		System.out.println(doc.toString());
		MongoCursor<Document> cursor = coll.find(doc).iterator();
		int authorId;
		if(cursor.hasNext()) {
			authorId = cursor.next().getInteger("author_id");
		} else {
			throw new Exception();
		}
		return authorId;
		
	}
	
	
	
	/**
	 * Returns a String that indicate if the message has been created or no

	 * 
	 * @param authorId
	 * 			  the id of the author
	 * @param authorName    
	 *            the name of the author
	 * @param content    
	 *            the content of the message      
	 * @return a String that indicate if the message has been created or no
	 *         
	 */
	public static String createMessage(int authorId, String authorName,String content, MongoCollection<Document> coll) {
		Document doc = new Document();
		ArrayList<Document> comments = new ArrayList<Document>();
		doc.append("author_id", authorId);
		doc.append("author_name", authorName);
		GregorianCalendar calendar = new GregorianCalendar();
		Date date = calendar.getTime();
		doc.append("date", date);
		doc.append("contenu", content);
		doc.append("likes", 0);
		doc.append("comments", comments);
		coll.insertOne(doc);
		return doc.getObjectId("_id").toHexString();
	}

	/**
	 * Delete the message with the specified message_id located in the specified collection
	 * @param message_id the id of the message
	 * @param coll the collection paired with the mongo database
	 */
	public static void deleteMessage(ObjectId message_id, MongoCollection<Document> coll) {
		Document doc = new Document();
		doc.put("message_id", message_id);
		coll.deleteOne(doc);
		
	}

	/**
	 * Returns true if the message of the specified m
	 * @param message_id the id of the message
	 * @param coll the mongo collection
	 * @return a boolean that indicate if the message exists or no
	 *         
	 */
	public static boolean existMessage(ObjectId message_id,MongoCollection<Document> coll) {
		Document doc = new Document("_id", message_id);
		MongoCursor<Document> cursor = coll.find(doc).iterator();
		String message = null;
		if(cursor.hasNext()) {
			message = cursor.next().getString("contenu");
			
		} else {
			return false;
		}
		if(message != null) {
			return true;
		}
		System.out.println(message);
		return false;
	}

	/**
	 * Returns the status of the message modification. The message modified is identified by the specified message_id, the new contents of the message
	 * should be the specified content and the message is located in the specified mongo collection
	 * @param message_id the message id
	 * @param content the new content of the message
	 * @param coll the mongo collection
	 * @return the status of the message modification
	 */
	public static UpdateResult updateMessage(ObjectId message_id, String content, MongoCollection<Document> coll){
		UpdateResult updateResult = null;
		
		Document doc = new Document("_id", message_id);
		Document found = coll.find(doc).first();
		
		if(found != null) {
			 System.out.println("message found");
			 Bson updatevalue  = new Document("contenu", content);
			 Bson updateOperation = new Document("$set", updatevalue);
			 coll.updateOne(found,updateOperation);
			 updateResult = coll.updateOne(found, updateOperation); 
			 System.out.println("Message Updated");
		}
		return updateResult;
			
	}

	/**
	 * Returns update result who indicate the status of the updated id like user array
	 * @param message_id the id of the message
	 * @param idUser the id of the user
	 * @param coll the mongo collection
	 * @return a update result who indicate the status of the updated like
	 */
	public static UpdateResult likeMessage(ObjectId message_id, int idUser, MongoCollection<Document> coll) throws Exception {
		UpdateResult updateResult = null;

		
		Document doc = new Document("_id", message_id);
		Document found = coll.find(doc).first();
		System.out.println("enter");
		if(found != null) {
			
			doc.append("_id", message_id);
			MongoCursor<Document> cursor = coll.find(doc).iterator();
			int nbLike = 0;
			ArrayList<Integer> idUserArray = new ArrayList<Integer>();
			if(cursor.hasNext()) {
				nbLike = cursor.next().getInteger("likes");
			} else {
				throw new Exception();
			}
			idUserArray.add(idUser);
			nbLike+=1;
			System.out.println("message found");
			Bson updatevalue  = new Document("likes", nbLike);
			Bson updateOperation = new Document("$set", updatevalue);
			
			
			coll.updateOne(found,updateOperation);
			
			updateResult = coll.updateOne(found, updateOperation); 
			System.out.println("Message liked");
		}
		
		return updateResult;
		
	}
	
	/**
	 * Returns update result who indicate the status of the updated like
	 * @param message_id the id of the message
	 * @param idUser the id of the user
	 * @param coll the mongo collection
	 * @return a update result who indicate the status of the updated like
	 *         
	 */
	public static UpdateResult idLikeMessage(ObjectId message_id, int idUser, MongoCollection<Document> coll) throws Exception {
		
		boolean checkLiked = false;
		UpdateResult updateResult = null;
		ArrayList<Integer> allIdLikeArray = new ArrayList<Integer>();
		Document doc = new Document("_id", message_id);
		Document found = coll.find(doc).first();
		boolean empty = false;
		MongoCursor<Document> cursor = coll.find(doc).iterator();
		
		//Récupératino de la liste des id user qui ont like
		while (cursor.hasNext()) {
			//Pour chaque message que l'utilisateur a écrit on va stocké l'id des usuers ayant like
		    Document str = cursor.next();
		    @SuppressWarnings("unchecked")
				List<String> idList = (List<String>)str.get("idLikes");
		    	//Stockage des id
		    	if(idList == null) {
		    		empty = true;
		    	}
		    	if(!empty) {
		    		for (int i = 1; i < idList.size(); i++) {
			    		allIdLikeArray.add(Integer.parseInt(idList.toArray()[i].toString()));
			    	}
		    	}
			    	
		}
		
		// On vérifie si l'utilisateur à déjà like le message
		for (int j = 0; j<allIdLikeArray.size();j++) {
			if(allIdLikeArray.get(j)==idUser) {
				checkLiked = true;
			}
		}
		if(!checkLiked) {
			if(found != null) {
				
				doc.append("_id", message_id);
			
				System.out.println("id: "+idUser);
				Bson updatevalue  = new Document("idLikes", idUser);
				Bson updateOperation = new Document("$push", updatevalue);
				coll.updateOne(found, updateOperation);
				
				updateResult = coll.updateOne(found, updateOperation); 
				
				//updateResult = coll.updateOne(found, update); 
				System.out.println("ArrayLike Updated");
			}
		}
		
		return updateResult;
		
	}
	
	/**
	 * Returns number of like of  specific message
	 * @param message_id the id of the message
	 * @param coll the mongo collection
	 * @return number of like of specific message
	 */
	public static int getLike(ObjectId mid, MongoCollection<Document> coll) throws Exception {
		Document doc = new Document();
		doc.append("_id", mid);
		MongoCursor<Document> cursor = coll.find(doc).iterator();
		int nbLike = 0;
		if(cursor.hasNext()) {
			nbLike = cursor.next().getInteger("likes");
		} else {
			throw new Exception();
		}
		return nbLike;
	}
	/**
	 * Returns an object who contain the id array of user who has liked a specific message
	 * @param message_id the id of the message
	 * @param coll the mongo collection
	 * @return an object who contain the id array of user who has liked a specific message
	 */
	public static  Object getIdLike(ObjectId message_id, MongoCollection<Document> coll) throws Exception {
		Document doc = new Document();
		doc.append("_id", message_id);
		Object idUserArray = new ArrayList<Integer>();
		MongoCursor<Document> cursor = coll.find(doc).iterator();
		if(cursor.hasNext()) {
			idUserArray = cursor.next().get("idLikes");
		} else {
			throw new Exception();
		}
		return idUserArray;
	}
	
	/**
	 * Returns a chain of characters containing some statistics of the user with the specified id using the specified mongo collection
	 * @param id_user the id of the current user
	 * @param coll the mongo collection
	 * @return Returns a chain of characters containing some statistics
	 */
	public static String percentAppreciationFriend(int idUser,MongoCollection<Document> coll) throws NumberFormatException, SQLException, JSONException {
		Document doc = new Document();
		doc.append("author_id", idUser);
		ArrayList<Integer> allIdLikeArray = new ArrayList<Integer>();
		boolean empty = false;
        String stat = "";
		MongoCursor<Document> cursor = coll.find(doc).iterator();
		//On trouve l'auteur du message
		while (cursor.hasNext()) {
			//Pour chaque message que l'utilisateur a écrit on va stocké l'id des usuers ayant like
		    Document str = cursor.next();
		    @SuppressWarnings("unchecked")
				List<String> idList = (List<String>)str.get("idLikes");
		    	//Stockage des id
		    	if(idList == null) {
		    		empty = true;
		    	}
		    	if(!empty) {
		    		for (int i = 1; i < idList.size(); i++) {
			    		allIdLikeArray.add(Integer.parseInt(idList.toArray()[i].toString()));
			    	}
		    	}
			    	
		}
		if(!empty) {
		//On calcul la fréquence d'apparition des id dans le tableau
    	int [] fr = new int [allIdLikeArray.size()];  
        int visited = -1;  
        for(int i = 0; i < allIdLikeArray.size(); i++){  
            int count = 1;  
            for(int j = i+1; j < allIdLikeArray.size(); j++){  
                if(allIdLikeArray.get(i) == allIdLikeArray.get(j)){  
                    count++;  
                    //To avoid counting same element again  
                    fr[j] = visited;  
                }  
            }  
            if(fr[i] != visited)  
                fr[i] = count;  
        }
        //Calcul du pourcentage
        for(int i = 0; i <fr.length; i++){

            if(fr[i] != visited)  
            	stat = stat+"\n L'utilisateur "+ allIdLikeArray.get(i) +" aime à: "+(100*fr[i])/allIdLikeArray.size()+"% vos publications";
               
        }
        
	}
		return stat;		
	}

	/**
	 * returns true if the specified message was created by the user with the specified idUser using the specified collection
	 * @param message_id the id of the message
	 * @param idUser the id of the user
	 * @param coll the mongo collection
	 * @return true if the specified message was created by the user with the specified idUser
	 */
public static boolean verif(ObjectId message_id,int idUser,MongoCollection<Document> coll) {
		
		try {
			boolean exists = MessageTools.existMessage(message_id, coll);
			//check si le message existe
			if(exists) {
				System.out.println("message exists");
				//Check si l'utilisateur est habilité a supprimé le message
				System.out.println(MessageTools.getMessageAuthor(message_id, coll)+" "+idUser);
				if(MessageTools.getMessageAuthor(message_id, coll)==idUser) {
					System.out.println("user habilité");
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	
	}
}

