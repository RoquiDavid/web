package test;

import org.json.JSONException;
import org.json.JSONObject;

import bd.LoadDataBase;
import services.AuthentificationServices;
import services.MessageServices;
import services.UserServices;

public class TestMessageServices {
	public static void main(String[] args) {

		try {

			/* load the data base*/
			LoadDataBase.loadSQLDataBase();
			//return the id of the message created
			LoadDataBase.loadMongoDataBase();
			
			/* Create the test users */
			UserServices.createUser("Gabriel", "1234", "CAO", "Gabriel");
			UserServices.createUser("David", "RYTUH", "ROQUI", "DAVID");
			UserServices.createUser("Quentin", "VBNKL", "Carreno", "QUENTIN");
			UserServices.createUser("Jeremy", "DFGHJKL", "FERON", "JEREMY");
			
			
			/* get the login key of each user */
			String key = AuthentificationServices.login("Gabriel", "1234").getString("key");
			String key2 = AuthentificationServices.login("DAVID", "RYTUH").getString("key");
			String key3 = AuthentificationServices.login("Quentin", "VBNKL").getString("key");
			String key4 = AuthentificationServices.login("Jeremy", "DFGHJKL").getString("key");
			
		
			//Partie création, récupération, update de message + blocage user
			JSONObject jsonPostCreateMessage = MessageServices.createMessage(key, "boujour");
			System.out.println("Results for create :" + jsonPostCreateMessage);
			/* L'utilisateur 1 veut savoir ce qu'a dit l'utilisateur 1*/
			JSONObject jsonGetMessage = MessageServices.getMessage(key, jsonPostCreateMessage.getString("mid"),1);
			System.out.println("Results for get 1:1" + jsonGetMessage);
			
			
			
			JSONObject jsonPostCreateMessage2 = MessageServices.createMessage(key2, "ça va ?");
			System.out.println("Results for create :" + jsonPostCreateMessage2);
			/* L'utilisateur 1 veut savoir ce qu'a dit l'utilisateur 7*/
			JSONObject jsonGetMessage2 = MessageServices.getMessage(key, jsonPostCreateMessage2.getString("mid"),7);
			System.out.println("Results for get 1:7 :" + jsonGetMessage2);
			
			JSONObject jsonPostCreateMessage2Bis = MessageServices.createMessage(key2, "ça va 1?");
			System.out.println("Results for create :" + jsonPostCreateMessage2Bis);
			
			JSONObject jsonPostCreateMessage2BisBis = MessageServices.createMessage(key2, "ça va 2?");
			System.out.println("Results for create :" + jsonPostCreateMessage2BisBis);
			
			
			/* L' utilisateur Gabriel bloque l'utilisateur Davi ce qui fait qu'ils ne peuvent respictivement plus voir leurs messages*/
			JSONObject block = UserServices.blockUser(key, "DAVID");
			System.out.println("Result block: "+ block);
			/* Résultat lorsqu'une user veut voir les message d'un autre user qu'il a bloqué*/
			JSONObject jsonGetMessageBlock = MessageServices.getMessage(key, jsonPostCreateMessage2.getString("mid"),7);
			System.out.println("resulat post blocage  "+jsonGetMessageBlock);
			/* Renseigne les parmètres du service*/
			
			
			JSONObject jsonPostCreateMessage4 = MessageServices.createMessage(key3, "hello tout le monde");
			System.out.println("Results for create :" + jsonPostCreateMessage4);
			/* L'utilisateur 2 veut savoir ce qu'a dit l'utilisateur 8*/
			JSONObject jsonGetMessage4 = MessageServices.getMessage(key, jsonPostCreateMessage4.getString("mid"), 8);
			System.out.println("Results for get :" + jsonGetMessage4);
			
			JSONObject jsonPostCreateMessage4Bis = MessageServices.createMessage(key3, "boujour2");
			System.out.println("Results for create :" + jsonPostCreateMessage4Bis);
			
			JSONObject jsonPostCreateMessage5 = MessageServices.createMessage(key4, "ça va ?");
			System.out.println("Results for create :" + jsonPostCreateMessage5);
			/* L'utilisateur 8 veut savoir ce qu'a dit l'utilisateur */
			JSONObject jsonGetMessage5 = MessageServices.getMessage(key4, jsonPostCreateMessage5.getString("mid"), 9);
			System.out.println("Results for get :" + jsonGetMessage5);
			
			
			/* Deuxième test pour les bloquages*/
			JSONObject block2 = UserServices.blockUser(key3, "Jeremy");
			System.out.println("Result block: "+ block2);
			JSONObject jsonGetMessage6 = MessageServices.getMessage(key3, jsonPostCreateMessage5.getString("mid"),9);
			System.out.println("resulat post blocage 2 "+jsonGetMessage6);
			/* Test de redondance pour le bloquage*/
			JSONObject block3 = UserServices.blockUser(key3, "Jeremy");
			System.out.println("redon block "+block3);
			
			
			
			
			/* Test de l'update d'un message */
			System.out.println("Before update: " + jsonGetMessage4);
			/* MAJ du message */
			JSONObject jsonPostUpdate = MessageServices.update(key3, jsonPostCreateMessage4.getString("mid"), "j'ai update");
			System.out.println("Result update: "+ jsonPostUpdate);
			/* Vétification après MAJ du message*/
			JSONObject jsonGetMessageUpdate = MessageServices.getMessage(key3, jsonPostCreateMessage4.getString("mid"), 8);
			System.out.println("after update: "+ jsonGetMessageUpdate);
			
			//Partie like et stats
			JSONObject jsonPostLike = MessageServices.like(key, jsonPostCreateMessage4.getString("mid"), 1);
			System.out.println("Results for like :" + jsonPostLike);
			
			/* L'utilisateur like un message*/
			JSONObject jsonPostLike2 = MessageServices.like(key2, jsonPostCreateMessage4Bis.getString("mid"), 7);
			/*On signal à l'utilisateur que le message a bien été like*/
			System.out.println("Results for like :" + jsonPostLike2);
			/* Affichage de la popularité de vos publications*/
			JSONObject jsonGetLike2 = MessageServices.getLike(key3, jsonPostCreateMessage4Bis.getString("mid"),8);
			System.out.println("Results for get like 2: " + jsonGetLike2);
			
			
			
			
			JSONObject jsonPostLike3 = MessageServices.like(key, jsonPostCreateMessage2.getString("mid"), 1);
			System.out.println("Results for like :" + jsonPostLike3);
			
			
			JSONObject jsonPostLike4 = MessageServices.like(key, jsonPostCreateMessage2Bis.getString("mid"), 1);
			System.out.println("Results for like 5: " + jsonPostLike4);
			
			JSONObject jsonPostLike5 = MessageServices.like(key4, jsonPostCreateMessage2BisBis.getString("mid"), 9);
			System.out.println("Results for like 6: " + jsonPostLike5);
			JSONObject jsonGetLike5 = MessageServices.getLike(key2, jsonPostCreateMessage2BisBis.getString("mid"),7);
			System.out.println("Results for get like 6: " + jsonGetLike5);
			
			
			
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

	
	}
}

