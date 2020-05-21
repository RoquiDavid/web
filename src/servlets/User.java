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

import services.UserServices;

/**
 * Servlet implementation class User
 */
@WebServlet("/User")
public class User extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	/**
	 * getUserList
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			
			JSONObject json = null;
			String login = request.getPathInfo();
			
				if(request.getPathInfo() == null) {
					json = UserServices.getUserList();
				} else {
					
					json = UserServices.getUser(login.replaceAll("/", ""));
				}
				
			
			
			print(json, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * createUser
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* Access to parameter */
		
		//Need a login and a password to create the user, saving it in the database for his future connection
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		String nom = request.getParameter("nom");
		String prenom = request.getParameter("prenom");
		String key = request.getParameter("key");
		try {
			JSONObject json = null;
			if(key != null) {
				 json = UserServices.blockUser(key, login);
			} else {
				 json = UserServices.createUser(login, password, nom, prenom);
			}
			
			/* print the output in the response */
			print(json, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * deleteUser
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {

			//checking before deleting the account of the user in the database 
			String key = request.getParameter("key");
			JSONObject json = UserServices.deleteUser(key);

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
