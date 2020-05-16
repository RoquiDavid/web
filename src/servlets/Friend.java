package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import services.FriendServices;

/**
 * Servlet implementation class Friend
 */
@WebServlet("/Friend")
public class Friend extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * getFriendList
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {

			String key = request.getParameter("key");
			/* Code to change : access to service */
			JSONObject json = FriendServices.getFriendList(key);

			/* print the output in the response */
			print(json, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * createFriend
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* Access to parameter */
		String friendlogin = request.getParameter("friendlogin");
		String key = request.getParameter("key");

		try {

			/* Code to change : access to service */
			JSONObject json = FriendServices.addFriend(key, friendlogin);

			/* print the output in the response */
			print(json, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * removeFriend
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			
			String friendlogin = request.getParameter("friendlogin");
			String key = request.getParameter("key");
			
			/* Code to change : access to service */
			JSONObject json = FriendServices.removeFriend(key, friendlogin);

			/* print the output in the response */
			print(json, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void print(JSONObject json, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(json.toString());

	}

}
