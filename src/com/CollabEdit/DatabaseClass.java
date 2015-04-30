package com.CollabEdit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseClass
{
	private static DatabaseClass authentication=null;
	private static String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=CollabEditDB";
	private static String user = "sa";
	private static String Password = "manpreet";
	private static String classloading = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	public static DatabaseClass getInstance()
	{
		if(authentication==null)
			authentication = new DatabaseClass();
		return authentication;
	}
	
	/*
	 * Validate the password as per constraints and then add details to DB
	 * Executes during Registration of a new User
	 */
	String createUserCredentials(String uname, String email, String password) throws Exception
	{
        Connection con;
        PreparedStatement stmt;
        String sql;
		String checkEmailRegex = "^.+@.+\\.[a-z]{2,4}$";
		String checkPasswordRegex = "^.+?(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[#$%^&*@]).{8,}$";
		if(email.matches(checkEmailRegex))
		{
			
			if(password.matches(checkPasswordRegex))
			{
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
	
	/*
	 * Checks the Email + password combination from DB  
	 *	Executes during login
	 */
	Boolean checkUserCredentials(String email, String password)
    {
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
		   
		   }
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
    
	/*
	 * This method will return all the file names which 
	 * have been linked with the username
	 * */
	
    String getAlreadyFileLinks(String username)
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
	        
        }
        catch(Exception e)
        {
                
        }
        return object.toString();
    }
    
    Boolean checkPrevFiles(String user1, String fileName)
    {
        Connection con;
        String link ="";
        Statement stmt;
        String sql = "Select * from [CollabEditDB].[dbo].[FileCreated] where ShareWith LIKE '%"+user1+"%' and FileName = '"+fileName+"'";
//        String sw = creatorEmail+","+shareWith; 
        try
        {
        	Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );
			stmt = con.createStatement();//Statement(sql);
		    ResultSet rs = stmt.executeQuery(sql);
		    rs.next();
		    System.out.println("qyer1: "+sql);
		    try
            {
		    	String u = rs.getString("Email");
			    return true;
            }
		    catch(Exception e )
		    {
		    	return false;
		    }
        }
        catch(Exception e)
        {
        	return false;
        } 
    }
    
    
    
    /*
     * When a new file is created then this method will insert the data to the DB
     */
    Boolean CreateFile (String creatorEmail, String fileName, String shareWith) throws Exception
    {
        Connection con;
        String link ="";
        PreparedStatement stmt;
        
        System.out.println("For: creatorEmail: "+creatorEmail);
        
        String sql = "Insert into dbo.FileCreated(Email, FileName, ShareWith) values (?, ?, ?)";
        String sw = creatorEmail+","+shareWith;
        //sysout
        if(!checkPrevFiles(creatorEmail, fileName)&&!checkPrevFiles(shareWith, fileName))
        	
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
			    stmt.executeUpdate();
			    return true;
            }
		    catch(Exception e )
		    {
		    	throw e;

		    }
        }
        catch(Exception e)
        {
    		throw e;
        }
		return false; 
    }
    
    /*
     * This method will return the userEmailId of the person with
     * whom this File has been shared 
     */

    String getSharedUsingUID(String username, String fileName)
    {
        Connection con;
        Statement stmt = null;
        String sql = "Select ShareWith from [CollabEditDB].[dbo].[FileCreated] where ShareWith LIKE '%"+username+"%' and FileName = '"+fileName+"'";

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
	        
	        while(st.hasMoreTokens())
	        {
	        	String temp = st.nextToken();
	        	if(!temp.equals(username))
	        	{
	        		object.put("email"+i, temp);
		        	i++;
	        	}
	        }
        }
        catch(Exception e)
        {
        	
        }
        return object.toString();
    }
    

    //This saves the data from the editor to the db
    boolean saveData(String userId, String Filename, String FileData)
    {
		//PrintWriter out;
	    Connection con;
	    Statement stmt;
	    
	    
	    String sql;
	    try
	    {
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );
			sql = "select data from [CollabEditDB].[dbo].[DataTable] where UserId = '"+userId+"' and FileName = '"+Filename+"'";
			
			stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			
			String temp = rs.getString("data");
			
			sql = "Update [CollabEditDB].[dbo].[DataTable] set data = '"+FileData+"' where UserId = '"+userId+"' and FileName = '"+Filename+"'";
			
			stmt.executeUpdate(sql);
			
	    }
		catch(Exception e)
		{
			
			try
			{
				Class.forName(classloading);
				con = DriverManager.getConnection(dbURL, user, Password );

				sql = "Insert into [CollabEditDB].[dbo].[DataTable](UserId, FileName, data) values ('"+userId+"','"+Filename+"','"+FileData+"')";
				
				
				stmt = con.createStatement();

				stmt.executeUpdate(sql);
				
			}
			catch(Exception e1)
			{
				
				return false;
			}
			return true;
		}
		return true;
    }
    
    /*
     * This will get the data of a particular file form the DB
     * Called by: CollabData.java 
     */
    
    JSONObject getCollabData(String username, String file) throws Exception
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
			
			String temp = rs.getString("data");
			
			JSONObject json = new JSONObject();
			json.put("data", temp);
			return json;
	    }
	    catch(Exception e)
	    {
	    	
	    	throw e;
	    }
    }
    
    //This method will get the name of All the users, and create a JSOn and den send it.
    String getUsers(String userID)
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
				String temp = rs.getString("UserId");
				
				if(!temp.equals(userID))
				{
					json.put("email"+i, temp);
					i++;
				}

			}
			
	    }
	    catch(Exception e)
	    {
	    	
	    }
	    return json.toString();
    }
    
    /*
     * Getting the Data of the file from the DB which will be sent via Mail
     */
    JSONObject getFileData(String userId, String file,String fromUser)
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
			
			
			
			json.put("data", rs.getString("data"));
			
			
			
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
			
			//stmt = con.createStatement();			
			rs = stmt.executeQuery(sql);
			rs.next();
			userName = rs.getString("Username");
			
			json.put("from", userName);
	    }
	    catch(Exception e)
	    {
	    	
	    	
	    	try {
				json.put("data","null");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    }
	    
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
	    try
	    {
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );

			sql = "Update dbo.ExistingUsers set UserPassword = ? where UserId = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, password);
			stmt.setString(2, userEmail);
			int result = stmt.executeUpdate();
			
			if(result > 0)
				return true;
			else 	
				return false;
        }
	    catch(Exception e)
	    {
	    	
	    	
	    	return false;
	    }
	}
	
	String getCreator(String file, String shareWith)
	{
	    Connection con;
	    Statement stmt;
	    String sql;
	    JSONObject json = new JSONObject();
	    try
	    {
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );

			sql = "select Email FROM [CollabEditDB].[dbo].[FileCreated] where ShareWith LIKE '%"+shareWith+"%' and FileName = '"+file+"'";
			stmt = con.createStatement();
			System.out.println("Sql Query: "+sql);
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			String s = rs.getString("Email");
			System.out.println("Output from GetCreator: "+s);
			json.put("creator", s);
			System.out.println("done");
	    }
	    catch(Exception e)
	    {
	    	System.out.println(e);
	    	try{
	    		json.put("null","null");
	    	}
	    	catch(Exception e1)
	    	{
	    		System.out.println(e1);
	    	}
	    }
	    return json.toString();
	}
}