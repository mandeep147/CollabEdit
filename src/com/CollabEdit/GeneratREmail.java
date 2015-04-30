package com.CollabEdit;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class GeneratREmail
 */
@WebServlet("/GeneratREmail")
public class GeneratREmail extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GeneratREmail() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * Generation of the Random Pass Code
	 * Mailing this, to the Email id
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String mailId = request.getParameter("userEmail");
		String randomPassword = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
		
		JSONObject json = new JSONObject();
		HttpSession session = request.getSession(true);
		System.out.println("sending mail");
		if(MailClass.sendEmail(mailId, randomPassword))
		{
			System.out.println("done");
			session.setAttribute("passCode", randomPassword);
			try
			{
				json.put("result", "success");
			} 
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try 
			{
				json.put("result", "fail");
			} 
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PrintWriter out = response.getWriter();
	    response.setContentType("application/json");
	    System.out.println("sending as a response this: "+json);
	    out.write(json.toString());
	    out.close(); 
	}
	
	
}
