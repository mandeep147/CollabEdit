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

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/*
	 * This executes when a user Login into the System
	 * credentials are checked
	 * Session attributes are set 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");

		JSONObject json = new JSONObject();

		String email = request.getParameter("username");
		String pass = request.getParameter("password");
		if(DatabaseClass.getInstance().checkUserCredentials(email, pass))
		{
			HttpSession session = request.getSession();
			session.setAttribute("LoggedInUserEmail", email);
			session.setMaxInactiveInterval(30*60);
			try
			{
				json.put("response", "success");
			}
			catch(Exception e)
			{

			}
		}
		else
		{
			try{
				 json.put("response", "fail");
			 }
			 catch(Exception e)
			 {

			 }
		}
		out.write(json.toString());
		out.close();
	}
}
