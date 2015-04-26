package com.CollabEdit;

import java.io.IOException;
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
 
/** 
 * @ServerEndpoint gives the relative name for the end point
 * This will be accessed via ws://192.168.1.103:8080/CollabEdit/main
 * Where localhost is the address of the host
 */
@ServerEndpoint(value="/main", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class}) 
public class OTServerr {
	
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
    
    @OnMessage
    public void onMessage(Message message, Session session)
    {
   
    	int val=-1;JsonObject tempJson = null;

    	// if Message Json consist of "total", means it has to be sent to other user
    	if(containsValue(message, "total",false))
    	{
    		val = message.getJson().getInt("total");
    	}
    	//If it consists of "codeFromEditor" that means it has to be saved to DB
    	if(containsValue(message, "CodeFromEditor",true))
    	{
    		String userMailID = message.getJson().getString("from");
    		userMailID = userMailID +", "+message.getJson().getString("email1");
    		DatabaseClass.getInstance().saveData(userMailID,message.getJson().getString("file"), message.getJson().getString("CodeFromEditor"));
    		//sending a response
    		sendMessageToAll(message,userSession.get(message.getJson().getString("from")));
    	}
    	else
    	{
        	if(val!=-1)
        	{
        		if(containsValue(message, "response",true))
        		{
        			/*
        			 * This message is a response from the client, that it 
        			 * has received the prev. message 
        			 */
        		}
        		else
        		{
        			String userFile = sessionFile.get(userSession.get(message.getJson().getString("from")));
        			int total = message.getJson().getInt("total");
        			while(total>0)
        			{
        				String email = message.getJson().getString("email"+total);

        				Session ses = userSession.get(email); //getting the session of that person TO WHOM we want to send the message

        				String tempFile = sessionFile.get(ses); //file        				
        				
        				if(tempFile.equals(userFile))
        				{
        					sendMessageToAll(message,ses);
        				}
        				total--;
        			}
        		}
        	}
        	else
        	{
        		
        		String fromMailID = message.getJson().getString("from");
        		String file = message.getJson().getString("file");
        		
        		//adding data to both the HashMaps
        		userSession.put(fromMailID,session);
        		sessionFile.put(session, file);
        	}
    	}
    }
 
    
    @SuppressWarnings("finally")
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
