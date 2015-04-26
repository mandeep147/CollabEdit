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
 * Servlet implementation class getPrevData
 */
@WebServlet("/getPrevData")
public class getPrevData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public getPrevData() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * This will get All the files which are linked with the LoggedInUser
	 * Called during: create_file.jsp
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		try
		{
			String username = session.getAttribute("LoggedInUserEmail").toString();
			if(username!=null)
			{
				String json = DatabaseClass.getInstance().getAlreadyFileLinks(username);
				PrintWriter out = response.getWriter();
			    response.setContentType("application/json");
			    System.out.println("sending as a response this: "+json);
			    out.write(json);
			    out.close();
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

}
