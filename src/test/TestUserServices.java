package test;

import org.json.JSONException;
import org.json.JSONObject;

import bd.LoadDataBase;
import services.AuthentificationServices;
import services.UserServices;

public class TestUserServices {
	public static void main(String[] args) {
		try {
			
			LoadDataBase.loadSQLDataBase();
			
			/* Renseigne les parmètres du service*/
			String login = "Gabriel1254";
			String password = "123456";
			String nom = "CAO";
			String prenom = "Gabriel";
			
			/* Création d'un utilisateur */
			System.out.println("L'utilisateur se crée un compte");
			System.out.println(UserServices.createUser(login, password, nom, prenom));
			
			
			/* L'utisateur cherche les informations liés à son compte */
			System.out.println("L'utilisateur affiche ses informations personnelles");
			System.out.println(UserServices.getUser(login));
			/* L'utilisateur cherche tout les utilisateurs du site */
			System.out.println("L'utilisateur récupère la liste de tout les utilisateurs");
			System.out.println(UserServices.getUserList());
			/* L'utilisateur se login */
			System.out.println("L'utilisateur se connecte");
			/* L'utilisateur récupère sa clef de sessoin */
			JSONObject o = AuthentificationServices.login(login, password);
			System.out.println(o);
			/* L'utilisateur supprime son compte */
			System.out.println("L'utilisateur supprime son compte");
			System.out.println(UserServices.deleteUser(o.getString("key")));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}
}

