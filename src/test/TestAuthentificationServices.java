package test;

import org.json.JSONException;
import org.json.JSONObject;

import bd.LoadDataBase;
import services.AuthentificationServices;
import services.UserServices;

public class TestAuthentificationServices {
	public static void main(String[] args) {
		try {
			//load the sql database
			LoadDataBase.loadSQLDataBase();
			/* Renseigne les parmètres du service*/
			String login = "Gabriel15646_";
			String password = "password";
			String nom = "CAO";
			String prenom = login;
			/* On créer un utilisateur *//* Renseigne les parmètres du service*/
			System.out.println("L'utilisateur se crée un compte");
			System.out.println(UserServices.createUser(login, password, nom, prenom));
			System.out.println("L'utilisateur se connecte");
			JSONObject jsonLogin = AuthentificationServices.login(login, password);
			System.out.println(jsonLogin);
			System.out.println("L'utilisateur se déconnecte");
			JSONObject jsonLogout = AuthentificationServices.logout(jsonLogin.getString("key"));
			System.out.println(jsonLogout);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
