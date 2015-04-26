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
	 * Method will get the data from the database and send it back to the Ajax request
	 * This will set the data to the CodeMirror if the file wass saved previously.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);	    
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String json=null;
		JSONObject mainJson = null;
		String file =null;
		try
		{
			String username = session.getAttribute("LoggedInUserEmail").toString();
			file =  session.getAttribute("CurrentFile").toString();
			
			mainJson = Authentication.getInstance().getCollabData(username, file);
			json = mainJson.toString();
		}
		catch(Exception e)
		{
			JSONObject obj = new JSONObject();
			try {
				obj.put("null", "null");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			json = obj.toString();
		}
		finally
		{
			out.write(json);
		    out.close();
		}

	}

}
