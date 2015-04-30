package com.CollabEdit;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
 * Servlet implementation class Mail
 */
@WebServlet("/MailServlet")
public class MailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MailServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * This will generate a file in the Temp folder of the system
	 * and send it as an attachment 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String userId = request.getParameter("data");
		HttpSession session = request.getSession(true);
		JSONObject res = new JSONObject();
		try
		{
			String originalUser = session.getAttribute("LoggedInUserEmail").toString();
			String fileName =  session.getAttribute("CurrentFile").toString();
			
			JSONObject obj = DatabaseClass.getInstance().getFileData(originalUser, fileName,originalUser);
			
			String data = (String) obj.get("data");
			String type = (String) obj.get("type");
			String UserName = (String) obj.get("from");
			if(data.equals("null")||type.equals("none")||UserName.equals("null"))
			{
				res.put("result", "fail");
				throw new NullPointerException();
			}
			else
			{
				
				String loc =createFile(data,type,fileName); 
				if(loc!=null)
				{
					try{
						if(MailClass.sendEmailWithAttachments(userId, UserName, loc))
						{
							res.put("result", "success");
						}
						else
						{
							res.put("result", "fail");
						}
					}
					catch(Exception e)
					{
						res.put("result", "fail");
						System.out.println(e);
					}
				}
				else
				{
					res.put("result", "fail");
				}
			}
		}
		catch(Exception e)
		{
			try {
				res.put("result", "fail");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		PrintWriter out = response.getWriter();
		 
		response.setContentType("application/json");
	    out.write(res.toString());
	    out.close();
	}
	
	String createFile(String data,String ext,String fileName)
	{
		try {
			String homeString = System.getProperty("user.home");
			String home = homeString.replace('\\', '/');
			String location = home+"/AppData/Local/Temp/CollabEdit/";
			
			File dir = new File(location);
			if(!dir.exists())
			{
				dir.mkdir();
			}
			location = location+""+fileName+ext;
			File file = new File(location);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.close();
 
			return location;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
