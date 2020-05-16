package test;

import org.json.JSONException;
import org.json.JSONObject;

import services.AuthentificationServices;
import services.UserServices;

public class TestUserServices {
	public static void main(String[] args) {
		try {
			
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
			
			JSONObject jsonGetAll = UserServices.getUserList();
			/* L'utilisateur se login */
			JSONObject o = AuthentificationServices.login(login, password);
			/* L'utilisateur récupère sa clef de sessoin */
			System.out.println(o.get("key"));
			/* L'utilisateur supprime son compte */
			JSONObject jsonDelete = UserServices.deleteUser(o.getString("key")+"srfez");
			
			
			System.out.println("Results for jsonGetAll :" + jsonGetAll);
			System.out.println("Results for jsonDelete :" + jsonDelete);
			
			
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}
}

