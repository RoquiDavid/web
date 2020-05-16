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

import services.AuthentificationServices;

/**
 * Servlet implementation class Authentification
 */
@WebServlet("/Authentification")
public class Authentification extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * LOGIN
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* Access to parameter */
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		try {

			/* Code to change : access to service */
			JSONObject json = AuthentificationServices.login(login, password);
			/* print the output in the response */
			print(json, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Logout
	 */
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {

			String key = req.getParameter("key");
			/* Code to change : access to service */
			JSONObject json = AuthentificationServices.logout(key);

			/* print the output in the response */
			print(json, resp);
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
