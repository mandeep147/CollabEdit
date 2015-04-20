package com.CollabEdit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;
 
/** 
 * @ServerEndpoint gives the relative name for the end point
 * This will be accessed via ws://localhost:12345/EchoChamber/echo
 * Where localhost is the address of the host
 */
@ServerEndpoint(value="/main", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class}) 
public class OTServerr {
    // This might not look right as wordpress hates angle brackets.
    //private static final Set sessions = Collections.synchronizedSet(new HashSet());
	
	private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
	private static HashMap<String,Session> userSession = new HashMap<String,Session>();
	private static HashMap<Session,String> sessionFile = new HashMap<Session,String>();
    /**
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was 
     * successful.
     */
    @OnOpen
    public void onOpen(Session session){
    	System.out.println(session.getId() + " has opened a connection");    
        System.out.println("CALLLED");
/*        Message message = new Message(Json.createObjectBuilder()
            .add("type", "text")
            .add("data", "User has connected")
            .build());
*/       
        //sendMessageToAll(message);
 
        try {
            Message connectedMessage = new Message(Json.createObjectBuilder()
            .add("type", "text")
            .add("data", "User has connected")
            .build());
            sessions.add(session);
            session.getBasicRemote().sendObject(connectedMessage);
        } catch (IOException | EncodeException ex) {
            ex.printStackTrace();
        }
    
         
    }
 
    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     */
/*    public boolean saveData(String userId, String Filename, String FileData)
    {
    	String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=CollabEditDB";
    	String user = "sa";
    	String Password = "manpreet";
    	String classloading = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

		//PrintWriter out;
	    Connection con;
	    Statement stmt;
	    
	    System.out.println("in the SAVE DATAAAAAAAAAAAAAAA");
	    String sql;
	    try
	    {
			Class.forName(classloading);
			con = DriverManager.getConnection(dbURL, user, Password );
			sql = "select data from dbo.DataTable where UserId = '"+userId+"' and FileName = '"+Filename+"'";
			
			stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery(sql);
		
			System.out.println("Query1: "+sql);
			String temp = rs.getString("data");
			
			sql = "Update dbo.DataTable set data = '"+FileData+"' where UserId = '"+userId+"' and FileName = '"+Filename+"'";
			
			stmt.executeUpdate(sql);
	    }
		catch(Exception e)
		{
			System.out.println("Exception occured while Saving Data:"+ e);
			try
			{
				Class.forName(classloading);
				con = DriverManager.getConnection(dbURL, user, Password );

				sql = "Insert into dbo.DataTable(UserId, FileName, data) values ('"+userId+"','"+Filename+"','"+FileData+"')";
				
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
*/
    
    
    @OnMessage
    public void onMessage(Message message, Session session)
    {
   
    	int val=-1;JsonObject fg = null;

    	if(containsValue(message, "total",false))
    	{
    		val = message.getJson().getInt("total");
    	}
    	if(containsValue(message, "CodeFromEditor",true))
    	{
    		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    		String ppl = message.getJson().getString("from");
    		ppl = ppl +", "+message.getJson().getString("email1");
    		Boolean check = Authentication.getInstance().saveData(ppl,message.getJson().getString("file"), message.getJson().getString("CodeFromEditor"));
    		System.out.println("KAAAAAAAAAAAAAAAAAAAAAAAAAM hooooooooooooooo gyaaaaaaaaaaaaaa "+check);
    		
    		sendMessageToAll(message,userSession.get(message.getJson().getString("from")));
    	}
    	else
    	{
    		System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE "+val);
        	if(val!=-1)
        	{
        		System.out.println("VVVVVVVVVVVVAAAAAAAAAAAAAALLLLLLLLLLLLLLLLLLLLLLLLLL");
        		if(containsValue(message, "response",true))
        		{
        			System.out.println("iiiiiiiiiiiiiiiiiiiiiI M @ 157 lineeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        		}
        		else
        		{
        			System.out.println("in the ELSEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
        			String userFile = sessionFile.get(userSession.get(message.getJson().getString("from")));
        			
        			int total = message.getJson().getInt("total");
        			System.out.println("value of total: "+total);
        			while(total>0)
        			{
        				System.out.println("in the total WHILE ");
        				String email = message.getJson().getString("email"+total); //email:simran@gmail.com
        				System.out.println("send mesaage to : "+email);
        				
        				Session ses = userSession.get(email); //getting the session of that person TO WHOM we want to send the message
        				System.out.println("send mesaage to session : "+ses);

        				String tempFile = sessionFile.get(ses); //file
        				System.out.println("send mesaage to FILEEE : "+tempFile);
        				
        				System.out.println("ses: "+ses+"  file: "+tempFile);
//        				System.out.println("sending data to "+email+ " session id is : "+ses.getId()+"daata: "+message.getJson());
        				if(tempFile.equals(userFile))
        				{
        					System.out.println("in the tempFile IF Condition");
        					sendMessageToAll(message,ses);
        				}
        				System.out.println("OUTSIDE IFFfFFFFFFFF");
        				total--;
        			}
        		}
        	}
        	else
        	{
        		
        		String f = message.getJson().getString("from");
        		String file = message.getJson().getString("file");
        		
        		//adding data to both the HashMaps
        		userSession.put(f,session);
        		sessionFile.put(session, file);
        		System.out.println("USer: "+f+" session: "+session+" file: "+file);
        		System.out.println("Message from " + session.getId() + ": " + message.toString());
        		System.out.println("*********************************************************");
        	}

    	}
    }
 
    
    private Boolean containsValue(Message json, String value,boolean string)
    {
    	boolean ans = false;
    	try
    	{
    		if(string)
    		{
    			String temp = json.getJson().getString(value); 
    		}
    		else
    		{
    			int temp =  json.getJson().getInt(value);
    		}
    		ans = true;
    	}
    	catch(Exception e)
    	{
//    		System.out.println(e);
    		System.out.println("in the Cactch");
    		ans = false;
    	}
    	finally{
    		return ans;
    	}
    }
    /**
     * The user closes the connection.
     * 
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session){
        sessions.remove(session);
        System.out.println("Session " +session.getId()+" has ended");
        Message message = new Message(Json.createObjectBuilder()
            .add("type", "text")
            .add("data", "User has disconnected")
            .build());
        sendMessageToAll(message,session);
        System.out.println("Done callig send message to all");
    }
 
    private void sendMessageToAll(Message message,Session session){
            try {
                session.getBasicRemote().sendObject(message);
                System.out.println("MESSAGE SENT");
            } catch (IOException | EncodeException ex) {
            	System.out.println("Message not sent");
                ex.printStackTrace();
            }
        }
    }
