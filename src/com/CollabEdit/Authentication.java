package com.CollabEdit;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.json.JSONException;
import org.json.JSONObject;

public class Authentication
{
	
	private static final long serialVersionUID = 1L;
	private static Authentication authentication=null;
	private static String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=CollabEditDB";
	private static String user = "sa";
	private static String Password = "manpreet";
	private static String classloading = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	public static Authentication getInstance()
	{
		if(authentication==null)
			authentication = new Authentication();
		return authentication;
	}
	String createUserCredentials(String uname, String email, String password) throws Exception
	{
        PrintWriter out;
        Connection con;
        PreparedStatement stmt;
        boolean returnbool;
        String sql;
		String checkEmailRegex = "^.+@.+\\.[a-z]{2,4}$";
		String checkPasswordRegex = "^.+?(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[#$%^&*@]).{8,}$";
		if(email.matches(checkEmailRegex))
		{
			
			if(password.matches(checkPasswordRegex))
			{
				System.out.println("Password MATCHED");
				try
				{
					Class.forName(classloading);
					con = DriverManager.getConnection(dbURL, user, Password );
					sql = "Insert into ExistingUsers(Username, UserId, UserPassword ) values (?, ?, ?)";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, uname);
					stmt.setString(2, email);
					stmt.setString(3, password);
				   int result = stmt.executeUpdate();
				   System.out.println(result);
				   if(result > 0)
				   {
					   return "success";
				   }
				   else 
				   {
					   return "EmailInDataBase";
				   }
					
				}
				catch(Exception e)
				{
					System.out.println("Exception in createUserCredentials " + e);
					return "EmailInDataBase";
				}
			}
			else 
			{
			
				return "Password";
			}
			
		}
		else
		{
			return null;
		}
	}
	static boolean checkUserCredentials(String email, String password)
    {
        PrintWriter out;
        Connection con;
        PreparedStatement stmt;
        boolean returnbool;
        String sql;
        try
        {
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );
			sql = "select * from ExistingUsers where UserId = ? and userpassword = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, email);
			stmt.setString(2, password);
		   ResultSet rs = stmt.executeQuery();
		   returnbool = rs.next();
		   System.out.println("returnbool is     " + returnbool);
		   System.out.println("query: "+stmt);
		   System.out.println("username: "+email+"  paswwrod: "+password);
		   try
		   {
		   if(returnbool)
		   {
			 sql=  "Insert into dbo.LoggedInUsers(EmailId) values (?)"; 
		    stmt =   con.prepareStatement(sql);
		    stmt.setString(1, email);
		    stmt.executeUpdate();
		      
		   }
		   } catch(Exception e)
		   {
		   System.out.println(e);
		   }
		   
		   System.out.println(returnbool);
		 return returnbool;
        }
        catch(SQLException sqe)
        {
       returnbool = false;
        	
        }
        catch(Exception e)
        {
        	returnbool = false;
        }
		return returnbool;
	
      
    }
    
    static ArrayList getOnlineModerators(String CreatedByEmailId)
    {
        PrintWriter out;
        Connection con;
        ArrayList<String> emails = new ArrayList<String>();
        PreparedStatement stmt;
    	int NumberOfOnlineModerators;
    	String ArrayEmails[];
		try
		{
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );
    	String sql = "select fm.EmailId As ModeratorEmails from dbo.FileCreated fc join dbo.FileModerators fm on fc.FileLink = fm.FileLink join dbo.LoggedInUsers lu on fm.EmailId = lu.EmailId";
      stmt =  con.prepareStatement(sql);
    	ResultSet rs = stmt.executeQuery();
        int columnCount = rs.getMetaData().getColumnCount();
    	
        while (rs.next())
        {
            String email = rs.getString("ModeratorEmails");
            
           emails.add(email);
        }
		}
		catch(Exception e)
		{
	System.out.println(e);
		}
		
		
    	return emails;
    }


    static String getFileLink(String createdByEmailid,  String FileName) throws Exception
    {
        PrintWriter out;
        Connection con;
        ArrayList<String> emails = new ArrayList<String>();
        Statement stmt;
    	String link = " ";
    	String FinalLink= " ";
    	String sql = "select FileLink as GeneratedLink from dbo.FileCreated fc where fc.Email = '" + createdByEmailid + "'and fc.FileName ='" +  FileName+ "'" ;
		try
		{
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
	         while(rs.next())
	         { link = rs.getString("GeneratedLink"); }
			FinalLink = FileName  +  link;
		}
		catch(Exception e)
		{
			System.out.println("ERROR in Auth: "+e);
			throw e;
		}
		return FinalLink;
    }
    
    static String getAlreadyFileLinks(String username)
     {
        Connection con;
        //String link ="";
        Statement stmt;
        String sql ="Select FileName, FileLink from [CollabEditDB].[dbo].[FileCreated] where  ShareWith LIKE '%"+username+"%'";
        JSONObject object = new JSONObject();
        try
        {
        	Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );
			stmt = con.createStatement();
		    ResultSet result = stmt.executeQuery(sql);
		    System.out.println("QUERY: "+sql);
	        int i=1;
//	        result.next();
		    while(result.next())
		    {
		    	String name = result.getString("FileName");
		    	String link = result.getString("FileLink");
		    	//Generating JSON now
		    	object.put("file"+i, name+link);
		    	i++;
		    }
	        System.out.println("end JSON: "+object.toString());
        }
        catch(Exception e)
        {
                System.out.println("Exception in GetPrev. Files");
        }
        return object.toString();
    }
    
    static String CreateFile (String creatorEmail, String fileName, String shareWith) throws Exception
    {
        Connection con;
        String link ="";
        PreparedStatement stmt;
        String sql = "Insert into dbo.FileCreated(Email, FileName, ShareWith) values (?, ?, ?)";
        String sw = creatorEmail+","+shareWith; 
        try
        {
        	Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );
			stmt = con.prepareStatement(sql);
		    stmt.setString(1, creatorEmail);
		    stmt.setString(2, fileName);
		    stmt.setString(3, sw);
		    try
            {
			    int result = stmt.executeUpdate();
			    link = Authentication.getFileLink(creatorEmail, fileName);
            }
		    catch(Exception e )
		    {
		    	System.out.println(e);
		    	throw e;

		    }
        }
        catch(Exception e)
        {
            System.out.println(e);
        		throw e;

        }
        return link;
   
    }
    static String getSharedUsingUID(String username, String fileName)
    {
        Connection con;
        String link ="";
        Statement stmt = null;
        String sql = "Select ShareWith from [CollabEditDB].[dbo].[FileCreated] where ShareWith LIKE '%"+username+"%' and FileName = '"+fileName+"'";
        System.out.println("saaadi query: "+sql);

        JSONObject object = new JSONObject();
        try
        {
        	Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );
			stmt = con.createStatement();
			
			
			ResultSet result = stmt.executeQuery(sql);
		    result.next();
		    
		    String id = result.getString("ShareWith");
		    //Generating JSON now
	        StringTokenizer st = new StringTokenizer(id,",");
	        object.put("total", st.countTokens()-1);
	        int i=1;
	        System.out.println("********AUTH*********************");
	        while(st.hasMoreTokens())
	        {

	        	String temp = st.nextToken();
	        	
	        	System.out.println(" temp: "+temp+" username: "+username);
	        	
	        	if(!temp.equals(username))
	        	{
	        		System.out.println("adding to json");
	        		object.put("email"+i, temp);
		        	i++;
	        	}

	        }
        }
        catch(Exception e)
        {
                System.out.println("Exception in AUTH "+e);
        }
        return object.toString();
    }
    
    static String getSharedWithE(String fileName)
    {
        Connection con;
        String link ="";
        Statement stmt;
        String sql = "Select ShareWith from dbo.FileCreated where FileLink = '"+fileName+"'";
        JSONObject object = new JSONObject();
        System.out.println("---------outside AUTH-----------");
        try
        {
        	 System.out.println("1---------outside AUTH-----------");
        	Class.forName(classloading);
       	 System.out.println("2---------outside AUTH-----------");
			con = DriverManager.getConnection(dbURL, user, Password );
       	 System.out.println("3---------outside AUTH-----------");
			stmt = con.createStatement();
       	 System.out.println("4---------outside AUTH-----------");
		    ResultSet result = stmt.executeQuery(sql);
       	 System.out.println("5---------outside AUTH-----------");
		    System.out.println(result);
	       	 System.out.println("6---------outside AUTH-----------");
	       	 System.out.println("QUERY: "+sql);
		    result.next();
	       	 System.out.println("7---------outside AUTH-----------");
		    String id = result.getString("ShareWith");
	       	 System.out.println("8---------outside AUTH-----------");
		    System.out.println("--in Authentication---- data: "+id);
		    //Generating JSON now
	        StringTokenizer st = new StringTokenizer(id,",");
	        
	        object.put("total", st.countTokens());
	        int i=1;
	        while(st.hasMoreTokens())
	        {
	        	object.put("email"+i, st.nextToken());
	        	i++;
	        }

	        System.out.println("end JSON: "+object.toString());
        }
        catch(Exception e)
        {
                System.out.println("Exception in AUTH");
        }
        return object.toString();
    }
    
    static boolean saveData(String userId, String Filename, String FileData)
    {
		//PrintWriter out;
	    Connection con;
	    Statement stmt;
	    
	    System.out.println("in the SAVE DATAAAAAAAAAAAAAAA");
	    String sql;
	    try
	    {
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );
			sql = "select data from [CollabEditDB].[dbo].[DataTable] where UserId = '"+userId+"' and FileName = '"+Filename+"'";
			
			stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			System.out.println("Query1: "+sql);
			String temp = rs.getString("data");
			
			sql = "Update [CollabEditDB].[dbo].[DataTable] set data = '"+FileData+"' where UserId = '"+userId+"' and FileName = '"+Filename+"'";
			
			stmt.executeUpdate(sql);
			System.out.println("Updating Query: "+sql);
	    }
		catch(Exception e)
		{
			System.out.println("Exception occured while Saving Data:"+ e);
			try
			{
				Class.forName(classloading);
				con = DriverManager.getConnection(dbURL, user, Password );

				sql = "Insert into [CollabEditDB].[dbo].[DataTable](UserId, FileName, data) values ('"+userId+"','"+Filename+"','"+FileData+"')";
				
				System.out.println("Query2: "+sql);
				stmt = con.createStatement();

				stmt.executeUpdate(sql);
				
			}
			catch(Exception e1)
			{
				System.out.println("Exception while Inserting: "+e1);
				return false;
			}
			return true;
		}
		return true;
    }
    
    static String getCollabData(String username, String file) throws Exception
    {
	    Connection con;
	    Statement stmt;
	    String sql;
	    try
	    {
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );

			sql = "select data from [CollabEditDB].[dbo].[DataTable] where UserId LIKE '%"+username+"%' and FileName = '"+file+"'";
			
			stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery(sql);
			
			rs.next();
			System.out.println("Query1: "+sql);
			String temp = rs.getString("data");
			
			JSONObject json = new JSONObject();
			json.put("data", temp);
			return json.toString();
	    }
	    catch(Exception e)
	    {
	    	System.out.println("exception in getCollabData "+e);
	    	throw e;
	    }
    }
    
    //This method will get the name of All the users, and create a JSOn and den send it.
    public static String getUsers()
    {
	    Connection con;
	    Statement stmt;
	    String sql;
		JSONObject json = new JSONObject();
	    try
	    {
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );

			sql = "select UserId  FROM [CollabEditDB].[dbo].[ExistingUsers]";
			
			stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery(sql);

			int i=1;
			while(rs.next())
			{
				json.put("email"+i, rs.getString("UserId"));
				i++;
			}
			
	    }
	    catch(Exception e)
	    {
	    	System.out.println("Exception in getUsers() "+e);
	    }
	    return json.toString();
    }
    
    //Getting the Data from the DB which will be sent via Mail
    static JSONObject getFileData(String userId, String file,String fromUser)
    {
	    Connection con;
	    Statement stmt;
	    String sql;
		JSONObject json = new JSONObject();
	    try
	    {
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );
			sql = "select data   FROM [CollabEditDB].[dbo].[DataTable] where UserId Like '%"+userId+"%' and FileName = '"+file+"'";
			stmt = con.createStatement();			
			
			ResultSet rs = stmt.executeQuery(sql);
			
			rs.next();
			
			System.out.println("Data Qury: "+sql);
			
			json.put("data", rs.getString("data"));
			
			System.out.println("second");
			
			int typeInt = Integer.parseInt(file.substring(file.length()-1,file.length()));
			
			String type = "null";
			String userName = "null";
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
			json.put("type",type);
			
			
			sql = "select Username FROM [CollabEditDB].[dbo].[ExistingUsers] where UserId='"+fromUser+"'";
			System.out.println("query2: "+sql);
			//stmt = con.createStatement();			
			rs = stmt.executeQuery(sql);
			rs.next();
			userName = rs.getString("Username");
			
			json.put("from", userName);
	    }
	    catch(Exception e)
	    {
	    	System.out.println(e);
	    	System.out.println("Exception: "+e);
	    	try {
				json.put("data","null");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    }
	    System.out.println("data from the Auth is: "+(json.toString()));
	    return json;
    }
    
	
	//Checking whether a particular mail ID exist in the database or not
	Boolean checkEmail(String email)
	{
	    Connection con;
	    Statement stmt;
	    String sql;
		JSONObject json = new JSONObject();
	    try
	    {
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );
			sql = "  select Username FROM [CollabEditDB].[dbo].[ExistingUsers] where UserId = '"+email+"'";
			stmt = con.createStatement();			
			
			ResultSet rs = stmt.executeQuery(sql);
			
			rs.next();
			
			String val = rs.getString("Username");
			return true;
	    }
	    catch(Exception e)
	    {
	    	return false;
	    }
	}
	
	//Updating the password field of the userId
	Boolean updatePassword(String userEmail, String password)
	{
	    Connection con;
	    PreparedStatement stmt;
	    String sql;
		JSONObject json = new JSONObject();
	    try
	    {
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );

			sql = "Update dbo.ExistingUsers set UserPassword = ? where UserId = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, password);
			stmt.setString(2, userEmail);
			int result = stmt.executeUpdate();
			System.out.println(result);
			if(result > 0)
				return true;
			else 	
				return false;
        }
	    catch(Exception e)
	    {
	    	System.out.println("Updation passwrd problem");
	    	System.out.println(e);
	    	return false;
	    }
	}
}