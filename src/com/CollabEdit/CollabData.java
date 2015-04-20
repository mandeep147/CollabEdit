package com.CollabEdit;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class CollabData
 */
@WebServlet("/CollabData")
public class CollabData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CollabData() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);	    
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String json=null;
		try
		{
			String username = session.getAttribute("LoggedInUserEmail").toString();
			String file =  session.getAttribute("CurrentFile").toString();
			
			json = Authentication.getInstance().getCollabData(username, file);
			System.out.println("----in CollabDATA--------------------");
			System.out.println("username: "+username+" file: "+file+" json: "+json);
			
		}catch(Exception e)
		{
			JSONObject obj = new JSONObject();
			try {
				obj.put("null", "null");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			json = obj.toString();
			System.out.println("Exception in CollabDData servlet: "+e);
		}
		finally
		{
			System.out.println("CollabData: json: "+json);
			out.write(json);
		    out.close();
		}

	}
}
