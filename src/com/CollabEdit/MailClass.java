package com.CollabEdit;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

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

public class MailClass {

	/**
	 * @param args
	 */
    //Send Email "to" with attachment
	static boolean sendEmailWithAttachments(String toAddress, String fromUser,String location)
            throws AddressException, MessagingException {
		
        String host = "smtp.gmail.com";
        String port = "587";
        final String userName = "info.collabedit@gmail.com";
        final String password = "gtbit2015";
        String subject = "File Shared Via CollabEdit";
        String message = fromUser+", has shared this file, with you.";

        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.user", userName);
        properties.put("mail.password", password);
 
        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);
 
        // creates a new e-mail message
        Message msg = new MimeMessage(session);
 
        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
 
        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");
 
        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
 
        // adds attachments
        MimeBodyPart attachPart = new MimeBodyPart();
        try 
        {
        	attachPart.attachFile(new File(location));
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
        	return false;
        }
        multipart.addBodyPart(attachPart);
         
        // sets the multi-part as e-mail's content
        msg.setContent(multipart);
 
        // sends the e-mail
        Transport.send(msg);
        return true;
    }

	static Boolean sendEmail(String toAddress, String randomString)
	{
        String host = "smtp.gmail.com";
        String port = "587";
        final String userName = "info.collabedit@gmail.com";
        final String password = "gtbit2015";
        String subject = "CollabEdit Password Recovery";
        String message = "Your pass-code for CollabEdit account is: "+randomString;
        
        System.out.println("1");
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.user", userName);
        properties.put("mail.password", password);
        System.out.println("@");
        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        System.out.println("3");
        Session session = Session.getInstance(properties, auth);
 
        // creates a new e-mail message
        Message msg = new MimeMessage(session);
 
        try
        {
            System.out.println("4");
			msg.setFrom(new InternetAddress(userName));
	        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
	        msg.setRecipients(Message.RecipientType.TO, toAddresses);
	        msg.setSubject(subject);
	        msg.setSentDate(new Date());
	        System.out.println("5");
	        // creates message part
	        MimeBodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setContent(message, "text/html");
	 
	        // creates multi-part
	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(messageBodyPart);
	        System.out.println("6");
	        msg.setContent(multipart);
	        
	        System.out.println("7");
	        // sends the e-mail
	        Transport.send(msg);
	        
	        System.out.println("MAil Sent..!!");
		} 
        catch (Exception e) 
        {
			e.printStackTrace();
        	return false;
		}
        return true;
	}
}
