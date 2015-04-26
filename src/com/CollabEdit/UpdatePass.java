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
 * Servlet implementation class UpdatePass
 */
@WebServlet("/UpdatePass")
public class UpdatePass extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdatePass() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * Updating Password
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		HttpSession session = request.getSession(true);
		String passCode = request.getParameter("passCode");
		String password = request.getParameter("password");
		JSONObject json = new JSONObject();
		try
		{
			String randomString = session.getAttribute("passCode").toString();
			if(passCode.equals(randomString))
			{
				String checkPasswordRegex = "^.+?(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[#$%^&*@]).{8,}$";
				if(password.matches(checkPasswordRegex))
				{
					if(DatabaseClass.getInstance().updatePassword(request.getParameter("userId"), password))
					{
						json.put("response","success");
					}
					else
					{
						json.put("response","fail");
					}
				}
				else
				{
					json.put("password","fail");
				}
			}
			else
			{
				json.put("passCode","fail");
			}
		}
		catch(Exception e)
		{
			try 
			{
				json.put("response", "fail");
			} 
			catch (JSONException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		finally
		{
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			out.write(json.toString());
			out.close();
			
		}
	}

}
