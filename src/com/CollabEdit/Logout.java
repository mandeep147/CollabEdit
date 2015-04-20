package com.CollabEdit;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Logout
 */
@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String value = request.getParameter("logout");
		HttpSession session = request.getSession(false);
		if(value.equals("logout"))
		{
			
			System.out.println("&&&&&&&&IN SIDE LOGOUT&&&&&&&&&&&&&");
			if(session!=null)
				session.invalidate();
			 return; // <--- Here.
		}
	}

}
