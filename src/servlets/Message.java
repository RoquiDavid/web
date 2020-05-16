
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

import services.MessageServices;
import services.UserServices;

/**
 * Servlet implementation class Message
 */
@WebServlet("/Message")
public class Message extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * getMessage
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
				
			JSONObject json = new JSONObject();
			JSONObject jsonLike;
			String url  = request.getPathInfo().replace("/", "");
			
			
			String arrayUrl[] = url.split("-");
			if(request.getPathInfo() == null) {
				json.append("res", "null");
				return;
			} else {
				json = MessageServices.getMessage(arrayUrl[0], arrayUrl[1],Integer.parseInt(arrayUrl[2]), Integer.parseInt(arrayUrl[3]));
				jsonLike = MessageServices.getLike(arrayUrl[0], arrayUrl[1],Integer.parseInt(arrayUrl[2]));
			}
			json.put("url", url);
			print(json, response);
			print(jsonLike,response);
			

			/* print the output in the response */
			print(json, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * createMessage
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* Access to parameter */

		String key = request.getParameter("key");
		String login = request.getParameter("login");

		String mid = request.getParameter("mid");

		String idUsercible = request.getParameter("author_id");
		String content = request.getParameter("content");

		try {
			
			if(mid != null) {
				JSONObject jsonPostLike = MessageServices.like(key, mid, Integer.parseInt(idUsercible));
				print(jsonPostLike,response);
			}
			if(mid == null) {
				int id = Integer.parseInt(idUsercible);
				JSONObject json = MessageServices.createMessage(id, login, key, content);
				print(json, response);
			}
			/* Code to change : access to service */
			
			
			/* print the output in the response */
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* Access to parameter */
		//String key, String mid, String idUser , String content
		
		String key = request.getParameter("key");
		String mid = request.getParameter("mid");
		String idUser = request.getParameter("idUser");
		String content = request.getParameter("content");

		try {

			/* Code to change : access to service */
			JSONObject json = MessageServices.update(key, mid, content ,Integer.parseInt(idUser));

			/* print the output in the response */
			print(json, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * deleteMessage
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {

			//Need the key session of the user and the id of the message to delete the message
			String key = request.getParameter("key");
			String mid = request.getParameter("mid");
			String idUser = request.getParameter("idUser");
			/* Code to change : access to service */
			JSONObject json = MessageServices.deleteMessage(key, mid, Integer.parseInt(idUser));

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
