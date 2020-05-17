package tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendTools {
	
	/**
	 * Insert in the database using the specified connection the specified couple of id. This will create a friend relationship between
	 * the users who own those id
	 * @param myid the id of the current user
	 * @param friendid the id of another user 
	 * @param c the connection paired with a sql connection
	 * @throws SQLException
	 */
	public static void addFriend(int myid, int friendid,Connection c) throws SQLException {

		String query = "insert into friend values(?, ?);";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setInt(1, myid);
		pst.setInt(2, friendid);

		pst.executeUpdate();
		pst.close();
	}

	/**
	 * Delete in the database using the specified connection the specified couple of id. This will remove the relationship between
	 * the users who own those id
	 * @param myid the id of the current user
	 * @param friendid the id of the other user which should be friend with the current user
	 * @param c the connection paired with a sql connection
	 * @throws SQLException
	 */
	public static void deleteFriend(int myid, int friendid, Connection c) throws SQLException {

		String query = "delete from friend where id_2 = ? and id_1 = ?;";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setInt(1, friendid);
		pst.setInt(2, myid);

		pst.executeUpdate();
		pst.close();
		
	}

	/**
	 * Retrieve from the database the friend list of the user with the specified id
	 * @param id the if of the current user 
	 * @param c the connection paired with a sql connection
	 * @return a JSONArray containing the friend list of the user
	 * @throws SQLException
	 * @throws JSONException
	 */
	public static JSONArray getFriendList(int id, Connection c) throws SQLException, JSONException {
		// Get the friends of the user of the specified login
		JSONArray userList = new JSONArray();
		

		String query = "select prenom, nom from user U where "
				+ "exists(select * from friend F where F.id_1 = ? and U.id = F.id_2);";
		PreparedStatement st = c.prepareStatement(query);
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			String nom = rs.getString("nom");
			String prenom = rs.getString("prenom");
			userList.put(new JSONObject().put("nom", nom).put("prenom", prenom));
		}

		rs.close();
		st.close();
		
		return userList;
		
	}
	

	
	public static boolean isYourFiend(int idUser, int idUserCheck, Connection c) throws SQLException, JSONException {
		//On met la liste des amis dans un tableau
		JSONArray friendList = getFriendList(idUserCheck, c);
		//On vérifie que l'utilisateur selectioner est dans la liste d'amis de l'autre utilisateur
		for (int i = 0; i < friendList.length(); ++i) {
		    JSONObject rec = friendList.getJSONObject(i);
		    int id = rec.getInt("id");
		    if(id == idUser) {
		    	return true;
		    }
		}
		return false;
	}

}
