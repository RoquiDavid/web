package test;




import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import bd.Database;

public class TestMongo {
	public static void main(String[] args) {
		MongoDatabase db = Database.getMongoDBConnection();
		MongoCollection<Document> coll = db.getCollection("message");
		Document doc = new Document();
		doc.append("id_author", 1);
		doc.append("name", "chan");
		coll.insertOne(doc);
		
		Document query = new Document("name", "chan");
		MongoCursor<Document> cursor = coll.find(query).iterator();
		while(cursor.hasNext()) {
			Document d = cursor.next();
			System.out.println(d);
			System.out.println(d.getObjectId("_id").toHexString());
		}
		
		
	}
}
