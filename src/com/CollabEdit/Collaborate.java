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
 * Servlet implementation class Collaborate
 */
@WebServlet("/Collaborate")
public class Collaborate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Collaborate() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /*
     * This will executed when a new File is being created, 
     * this will be called in order to insert data to the DB
     * 
     */
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//getting the session
		HttpSession session = request.getSession();
		String creatorEmail = (String) session.getAttribute("LoggedInUserEmail");
		String file = request.getParameter("nameoffile");
		String completefileName = (file + request.getParameter("select1")).toString();
		String emailid = request.getParameter("mailofmod"); // this is the mailID of that person with whom we will share the doc.
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		JSONObject json = new JSONObject();
		try
		{
			System.out.println("Checking for: "+creatorEmail);
			if(DatabaseClass.getInstance().CreateFile(creatorEmail,completefileName,emailid))
			{
				session.setAttribute("CurrentFile", completefileName); //setting the attribute
				json.put("response","success");
			}
		}
		catch(Exception e)
		{
			try 
			{
				json.put("response","fail");
			} 
			catch (JSONException e1) 
			{
				e1.printStackTrace();
			}
		}
		finally{
			out.write(json.toString());
			out.close();
		}
		
	}

}
