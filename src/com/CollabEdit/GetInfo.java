package com.CollabEdit;



import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * Servlet implementation class GetInfo
 * THIS SERVLET JUST RETURNS THE NAME/USERID OF THE LOGGEDIN USER IN THE JSON FORMAT
 */
@WebServlet("/GetInfo")
public class GetInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
      JSONObject obj = null; 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetInfo() {
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
		
		PrintWriter out = response.getWriter();
		System.out.println("DO POST STARTED IN GET INFO");

		//getting the session
		HttpSession session = request.getSession(true);
		System.out.println("session get");
		String username = (String)session.getAttribute("LoggedInUserEmail");
		String fileName = (String)session.getAttribute("CurrentFile");

		System.out.println("INGETINFO " + username);
		
		
		obj = new JSONObject();
		
		//if session exists then
		if(username!=null&&fileName!=null)
		{
			try
			{
				System.out.println("IN TRY");
				
				obj.put("email", username);
				obj.put("fileName", fileName);

				System.out.println("OBJECT PUT");
			    response.setContentType("application/json");
			    System.out.println("GETINFPPPOOOOOsending as a response this: "+ obj);
			    out.write(obj.toString());
			    out.close();
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
	}

}
