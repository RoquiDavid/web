package tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bd.Database;

public class FriendTools {
	
	public static void addFriend(int myid, int friendid) throws SQLException {
		Connection c = Database.getMySQLConnection();

		String query = "insert into friend values(?, ?);";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setInt(1, myid);
		pst.setInt(2, friendid);

		pst.executeUpdate();
		pst.close();
		c.close();
	}

	public static void deleteFriend(int myid, int friendid, Connection c) throws SQLException {

		String query = "delete from friend where id_2 = ? and id_1 = ?;";
		PreparedStatement pst = c.prepareStatement(query);
		pst.setInt(1, friendid);
		pst.setInt(2, myid);

		pst.executeUpdate();
		pst.close();
		
	}

	public static JSONArray getFriendList(int id) throws SQLException, JSONException {
		// Get the friends of the user of the specified login
		JSONArray userList = new JSONArray();
		
		Connection c = Database.getMySQLConnection();

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
		c.close();

		return userList;
		
	}
	

	
	public static boolean isYourFiend(int idUser, int idUserCheck) throws SQLException, JSONException {
		//On met la liste des amis dans un tableau
		JSONArray friendList = getFriendList(idUserCheck);
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
