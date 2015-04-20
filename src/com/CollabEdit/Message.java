package com.CollabEdit;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.StringWriter;
import javax.json.Json;
import javax.json.JsonObject;
 
public class Message {
    private JsonObject json;
 
    public Message(JsonObject json) {
        this.json = json;
    }
 
    public JsonObject getJson() {
        return json;
    }
 
    public void setJson(JsonObject json) {
        this.json = json;
    }
 
    @Override
    public String toString(){
        StringWriter writer = new StringWriter();
 
        Json.createWriter(writer).write(json);
 
        return writer.toString();
    }
 
}