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
 * Servlet implementation class DisplaySEmails
 */
@WebServlet("/DisplaySEmails")
public class DisplaySEmails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DisplaySEmails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * Servlet will get the email-id of that person with whom the Current file 
	 * of loggedInUser is being shared
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//Using Sessions in order to get USerID and FileName in order to get the SharedWith ppl
		
		HttpSession session = request.getSession(true);
		Object username = session.getAttribute("LoggedInUserEmail");
		Object filename = session.getAttribute("CurrentFile");
		
		if(username!=null&&filename!=null)
		{
			String json = DatabaseClass.getInstance().getSharedUsingUID(username.toString(),filename.toString());
			PrintWriter out = response.getWriter();
		 
			response.setContentType("application/json");
		    out.write(json);
		    out.close();
		}

	}

}
