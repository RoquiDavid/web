package test;

import org.json.JSONException;
import org.json.JSONObject;

import bd.LoadDataBase;
import services.AuthentificationServices;
import services.FriendServices;
import services.UserServices;

public class TestFriendServices {
	public static void main(String[] args) {

		try {
			LoadDataBase.loadSQLDataBase();
			/*Creating 2 users*/
			System.out.println("CAO Gabriel created an account as Mathieu");
			System.out.println(UserServices.createUser("Mathieu", "ABCDE", "CAO", "Gabriel"));
			
			System.out.println("Bilal Lina created an account as Laurine");
			System.out.println(UserServices.createUser("Laurine", "BCHDI", "Bilal", "Lina"));
			
			/*The first user logged in*/
			System.out.println("Mathieu logged in");
			JSONObject o1 = AuthentificationServices.login("Mathieu", "ABCDE");
			System.out.println(o1);
			
			/*He added the second user as a friend*/
			System.out.println("Mathieu add Laurine as his friend");
			System.out.println(FriendServices.addFriend(o1.getString("key"), "Laurine"));
			
			/*look in the database if the friend has been added*/
			System.out.println("Mathieu get his friend list");
			System.out.println(FriendServices.getFriendList(o1.getString("key")));
		
			
			/*the user delete the friend*/
			System.out.println("Mathieu remove Laurine from  his friend list");
			System.out.println(FriendServices.removeFriend(o1.getString("key"), "Laurine"));
			
			/*look in the database if the friend has been successfully deleted*/
			System.out.println("Mathieu get his friend list to check if Laurine is still in his friend list");
			System.out.println(FriendServices.getFriendList(o1.getString("key")));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
