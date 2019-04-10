/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazon_ses;

/**
 *
 * @author Dell
 */
import java.util.Properties;
import java.lang.Boolean;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.sql.*;
import java.util.Scanner;

public class AmazonSESSample {

    public static Boolean mail(String FROM, String FROMNAME, String TO) throws Exception {
        // Replace smtp_username with your Amazon SES SMTP user name.
        String SMTP_USERNAME = "AKIAZRBPQZAU74A4Q4FV";
    
        // Replace smtp_password with your Amazon SES SMTP password.
        String SMTP_PASSWORD = "BMkrLgqM55SYcL9OW/n2Wly74nTl3jGh1gi7bFl1C6qb";
    
        // Amazon SES SMTP host name. This example uses the US West (Oregon) region.
        // See https://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html#region-endpoints
        // for more information.
        String HOST = "email-smtp.us-west-2.amazonaws.com";
    
        // The port you will connect to on the Amazon SES SMTP endpoint. 
        int PORT = 587;
    
        String SUBJECT = "Amazon SES test mail(SMTP interface accessed using Java)";
    
        String BODY = String.join(
    	    System.getProperty("line.separator"),
    	    "<h1>Amazon SES SMTP Email Test</h1>",
    	    "<p>This email was sent with Amazon SES using the ", 
    	    "<a href='https://github.com/javaee/javamail'>Javamail Package and MySQL</a>",
    	    " for <a href='https://www.java.com'>Java</a>."
    	);
        
        // Create a Properties object to contain connection configuration information.
    	Properties props = System.getProperties();
    	props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtp.port", PORT); 
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.auth", "true");

        // Create a Session object to represent a mail session with the specified properties. 
    	Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information. 
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM,FROMNAME));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));
        msg.setSubject(SUBJECT);
        msg.setContent(BODY,"text/html");
            
        // Create a transport.
        Transport transport = session.getTransport();
        int flag = 0;
        // Send the message.
        try
        {
            System.out.println("Sending...");
            
            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
        	
            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
            flag = 1;
        }
        catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();
            if (flag == 1)
                return true;
            else
                return false;
        }
    }
    
    public static void main(String[] args) throws Exception {

        String url;
        url = "jdbc:mysql://localhost:3306/lit2016004";
        String username = "root";
        String password = "aws432c";
        String query1 = "SELECT * FROM email";
        
        Class.forName("com.mysql.jdbc.Driver");
        //DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        Connection c = DriverManager.getConnection(url, username, password);
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery(query1);
        
        // Replace sender@example.com with your "From" address.
        // This address must be verified.
        String FROM = "lit2016004@iiitl.ac.in";
        String FROMNAME = "Vikash IIITL";
       
        while (r.next()){
            String TO = r.getString("email_id");
            Boolean sent = r.getBoolean("sent");
            
            if (sent == false){
                Boolean mail_sent = mail(FROM, FROMNAME, TO);
                
                if (mail_sent == true){
                    String query2 = "UPDATE email SET sent = ?";
                    PreparedStatement p = c.prepareStatement(query2);
                    p.setBoolean(1, true);
                    int count = p.executeUpdate();
                }
            }
        }
        
        s.close();
        c.close();
    }
}