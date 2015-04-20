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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");

		System.out.println("in hereeeeeeeeeeeeeeee");
		JSONObject json = new JSONObject();

		//		response.setContentType("text/html");
		String email = request.getParameter("username");
		String pass = request.getParameter("password");
		if(Authentication.checkUserCredentials(email, pass))
		{
			HttpSession session = request.getSession();
			session.setAttribute("LoggedInUserEmail", email);
			session.setMaxInactiveInterval(30*60);
			try{
				json.put("response", "success");
				//System.out.println("sending as a response this: "+json);
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
		System.out.println("respose back is: "+json.toString());
		out.write(json.toString());
		out.close();
	}

}
