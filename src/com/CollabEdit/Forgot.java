package com.CollabEdit;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class Forgot
 */
@WebServlet("/Forgot")
public class Forgot extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Forgot() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
		String userId = request.getParameter("userEmail");
		JSONObject json = new JSONObject();
		
		try
		{
			if(Authentication.getInstance().checkEmail(userId))
			{
				json.put("result", "success");
			}
			else
			{
				json.put("result", "fail");
			}
		}
		catch(Exception e)
		{
			try {
				json.put("result", "fail");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		finally{
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
		    System.out.println("sending as a response this DisplaySEmails: "+json);
		    out.write(json.toString());
		    out.close();

		}
 
	}

}
