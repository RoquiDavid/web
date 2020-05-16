package test;

import org.json.JSONException;
import org.json.JSONObject;

import services.AuthentificationServices;
import services.FriendServices;

public class TestFriendServices {
	public static void main(String[] args) {

		try {
			/*Creating 2 users*/
			//UserServices.createUser("Mathieu", "ABCDE", "CAO", "Gabriel");
			//UserServices.createUser("Laurine", "BCHDI", "Bilal", "Lina");
			
			/*The first user logged in*/
			JSONObject o1 = AuthentificationServices.login("Mathieu", "ABCDE");
			
			//JSONObject o1 = new JSONObject().put("key", "4LEIM1DER24MKC5HXQJD9C257T3N2BXA");
			/*He added the second user as a friend*/
			FriendServices.addFriend(o1.getString("key"), "Laurine");
			
			/*look in the database if the friend has been added*/
			JSONObject jsonGet = FriendServices.getFriendList(o1.getString("key"));
			System.out.println("Results for jsonGet :" + jsonGet);
			
			/*the user delete the friend*/
			FriendServices.removeFriend(o1.getString("key"), "Laurine");
			
			/*look in the database if the friend has been successfully deleted*/
			jsonGet = FriendServices.getFriendList(o1.getString("key"));
			System.out.println("Results for jsonGet :" + jsonGet);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
