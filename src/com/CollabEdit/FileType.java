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
 * Servlet implementation class FileType
 */
@WebServlet("/FileType")
public class FileType extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileType() {
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
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		JSONObject mainJson = null;
		String file =null;
		String json=null;
		try
		{
			String username = session.getAttribute("LoggedInUserEmail").toString();
			file =  session.getAttribute("CurrentFile").toString();
			mainJson.put("file", getFileType(file));
//			mainJson.put("file", getFileType(file));
			json = mainJson.toString();
		}catch(Exception e)
		{
			System.out.println("Exception in FileType servlet: "+e);
		}
		finally
		{

			System.out.println("FileType: json: "+json);
			out.write(json);
		    out.close();
		}

	}

	

    //getting the type of the file
    String getFileType(String file)
    {
    	int typeInt = Integer.parseInt(file.substring(file.length()-1,file.length()));
		
    	String type ="";
		switch(typeInt)
		{
			case 1: type = ".c";break;
			case 2: type =".cpp";break;
			case 3:type = ".java";break;
			case 4:type =".js";break;
			case 5:type =".html";break;
			case 6:type =".jsp";break;
			case 7:type =".css";break;
			case 8:type =".rb";break;
			case 9:type =".vb";break;
			case 10:type =".asp";break;
			case 11:type =".pl";break;
			case 12:type =".php";break;
		}
		return type;
    }

}
