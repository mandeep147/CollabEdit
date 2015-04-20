package com.CollabEdit;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
 
/**
 * Converts the message class to a string.
 */
public class MessageEncoder implements Encoder.Text<Message> {
 
    @Override
    public String encode(Message message) throws EncodeException {
        return message.getJson().toString();
    }
 
    @Override
    public void init(EndpointConfig config) {
        System.out.println("Init");
    }
 
    @Override
    public void destroy() {
        System.out.println("destroy");
    }
 
}