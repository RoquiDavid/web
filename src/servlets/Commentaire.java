package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import services.CommentaireService;

/**
 * Servlet implementation class Commentaire
 */
public class Commentaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   
    public Commentaire() {
        super();
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url  = request.getPathInfo().replace("/", "");
		JSONObject json = null;
		String key;
		String m_id;
		String c_id;
		if(url != null) {
			String[] params = url.split("-");
			switch (params.length) {
			case 2:
				 key = params[0];
				 m_id = params[1];
				 try {
						json = CommentaireService.getCommentaireList(key, m_id);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				break;
			case 3:
				 key = params[0];
				 m_id = params[1];
				 c_id = params[2];
				
				try {
					json = CommentaireService.getCommentaire(key, m_id, c_id);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
			
		}
		print(json, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter("key");
		String m_id = request.getParameter("m_id");
		String comment = request.getParameter("comment");
		JSONObject json = null;
		try {
			json = CommentaireService.createCommentaire(key, m_id, comment);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		print(json, response);
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter("key");
		String m_id = request.getParameter("m_id");
		String c_id = request.getParameter("c_id");
		JSONObject json = null;
		try {
			json = CommentaireService.deleteCommentaire(key, m_id, c_id);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		print(json, response);
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter("key");
		String m_id = request.getParameter("m_id");
		String c_id = request.getParameter("c_id");
		String new_comment = request.getParameter("new_comment");
		JSONObject json = null;
		try {
			json = CommentaireService.modifyCommentaire(key, m_id, c_id, new_comment);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		print(json, response);
	}
	
	private void print(JSONObject json, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(json.toString());

	}
}
