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

@WebServlet("/Registration")
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/*
	 * Registration Servlet
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");

		String uname = request.getParameter("usernamesignup");
		String email = request.getParameter("emailsignup");
		String pass = request.getParameter("passwordsignup");
		
		JSONObject json = new JSONObject();
		try
		{
			String returnedValue =DatabaseClass.getInstance().createUserCredentials(uname, email, pass); 
			if(returnedValue.equals("success"))
			{
				json.put("response" , "success");
				json.put("password", "success");
				
			}
			else if(returnedValue.equals("Password"))
			{
				json.put("password", "fail");
				json.put("response" , "fail");			
			}
			else
			{
				json.put("password", "ok");
				json.put("response" , "fail");
				json.put("email", "fail");
			}
		}
		catch(Exception e)
		{
			try 
			{
				json.put("response" , "fail");
			} 
			catch (JSONException e1) 
			{
				e1.printStackTrace();
			}						
		}
		finally
		{
			out.write(json.toString());
			out.close();
		}
	}

}
