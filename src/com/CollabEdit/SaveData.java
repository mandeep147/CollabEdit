package com.CollabEdit;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.json.*;

/**
 * Servlet implementation class InsertFile
 */
@WebServlet("/SaveData")
public class SaveData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Authentication authentication=null;
	private static String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=CollabEditDB";
	private static String user = "sa";
	private static String Password = "minisiminoni";
	private static String classloading = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveData() {
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
		HttpSession session = request.getSession(true);
		System.out.println("session get");
		String userId = (String)session.getAttribute("LoggedInUserEmail");
		String Filename = (String)session.getAttribute("CurrentFile");
		String FileData = request.getParameter("CodeFromEditor");

		System.out.println(" in the Save Data\n DataCodeMirror: "+FileData);
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		
		if(Authentication.getInstance().saveData(userId, Filename, FileData))
		{
		    out.write("{'result': 'success' }");
		    out.close();
		}
		else
		{
		    out.write("{'result': 'fail' }");
		    out.close();
		}
	}
}