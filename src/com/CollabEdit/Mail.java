package com.CollabEdit;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * Servlet implementation class Mail
 */
@WebServlet("/Mail")
public class Mail extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Mail() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userId = request.getParameter("data");
		HttpSession session = request.getSession(true);
		try
		{
			System.out.println("in the TRYYYYYYYYYYYYYY");
			String originalUser = session.getAttribute("LoggedInUserEmail").toString();
			String fileName =  session.getAttribute("CurrentFile").toString();
			
			System.out.println("originalUser: "+originalUser+"  fileName: "+fileName+" userID: "+userId);
			System.out.println("Creating json object now");
			
			JSONObject obj = Authentication.getInstance().getFileData(originalUser, fileName,originalUser);
			
			System.out.println("Created json object now");
			String data = (String) obj.get("data");
			String type = (String) obj.get("type");
			String UserName = (String) obj.get("from");
			System.out.println("getting data from json object now");
			if(data.equals("null")||type.equals("none")||UserName.equals("null"))
			{
				System.out.println("no data in here");
				throw new NullPointerException();
			}
			else
			{
				
				System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
/*				System.out.println("type: "+type);
				System.out.println("data: "+data);
*/				System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
				
				String loc =createFile(data,type,fileName); 
				if(loc!=null)
				{
					try{
						System.out.println("userId: "+userId+" UserName: "+UserName+" loc: "+loc);
						System.out.println("ideal case: manpreet22thesingh@gmail.com,gtbit,C:/Users/waheguru/AppData/Local/Temp/CollabEdit/colla2.cpp");
						if(Authentication.getInstance().sendEmailWithAttachments(userId, UserName, loc))
						{
							System.out.println("VOILLAAAAAAAAAA");
						}
						else
						{
							System.out.println("can not send mail some panga");
						}
					}
					catch(Exception e)
					{
						System.out.println("Exception while sending email: ");
						System.out.println(e);
					}
				}
				else
				{
					System.out.println("NOOOOOOOOOOOOO File write krti h check kr");
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("exception in the MAIL.JAVA "+e);
		}
	}
	
	String createFile(String data,String ext,String fileName)
	{
		try {
			String homeString = System.getProperty("user.home");
			String home = homeString.replace('\\', '/');
			String location = home+"/AppData/Local/Temp/CollabEdit/";
			System.out.println("Location of the file: "+location);
			
			File dir = new File(location);
			if(!dir.exists())
			{
				dir.mkdir();
			}
			location = location+""+fileName+ext;
			System.out.println("new location: "+location);
			File file = new File(location);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				System.out.println("file created");
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			System.out.println("writing: "+data);
			bw.write(data);
			bw.close();
 
			return location;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
