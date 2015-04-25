package com.CollabEdit;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class UsersDb
 */
@WebServlet("/UsersDb")
public class UsersDb extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UsersDb() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		HttpSession session = request.getSession(true);
		
		try
		{
			String userID = session.getAttribute("LoggedInUserEmail").toString();
			System.out.println("DO NOT INCLUDE THIS: "+userID);
			String json = Authentication.getInstance().getUsers(userID);
			PrintWriter out = response.getWriter();
			 
			response.setContentType("application/json");
		    System.out.println("sending as a response this DisplaySEmails: "+json);
		    out.write(json);
		    out.close();
		}
		catch(Exception e)
		{
			
		}

		
	}

}
