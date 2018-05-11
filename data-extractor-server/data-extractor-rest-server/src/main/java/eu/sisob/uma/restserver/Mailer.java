/*
    Copyright (c) 2014 "(IA)2 Research Group. Universidad de Málaga"
                        http://iaia.lcc.uma.es | http://www.uma.es

    This file is part of SISOB Data Extractor.

    SISOB Data Extractor is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SISOB Data Extractor is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SISOB Data Extractor. If not, see <http://www.gnu.org/licenses/>.
*/
package eu.sisob.uma.restserver;

import eu.sisob.uma.restserver.managers.TaskFileManager;
import com.sun.mail.smtp.SMTPTransport;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.IOUtils;

public class Mailer 
{    
    private static final Logger LOG = Logger.getLogger(Mailer.class.getName());
    
    public static void sendMail(String to, String subject, String content) throws MessagingException, UnsupportedEncodingException
    {
        String server_email = TheConfig.getProperty(TheConfig.SYSTEMEMAIL_ADDRESS);
        String server_pwd = TheConfig.getProperty(TheConfig.SYSTEMEMAIL_PASSWORD);
                
        Properties props = System.getProperties();
        props.put("mail.smtps.host",TheConfig.getProperty("mail.smtps.host"));
        props.put("mail.smtps.auth","true");
        Session session = Session.getInstance(props, null);
        
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(server_email, "SISOB Notification"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(subject);
        msg.setContent(content, "text/html; charset=utf-8");
        msg.setHeader("X-Mailer", "Sisob Data Extractor System");
        msg.setSentDate(new Date());
        
        SMTPTransport t =(SMTPTransport)session.getTransport("smtps");
        t.connect(TheConfig.getProperty("mail.smtps.host"), server_email, server_pwd);
        t.sendMessage(msg, msg.getAllRecipients());
        
        LOG.log(Level.INFO, "Email Response To {0}: {1}", new Object[]{to, t.getLastServerResponse()});
        t.close(); 
    }
    
    /*
     * This notifies via email the file results of a task (this must be called in the end of a task, for example in the executeCallBackOfTask of ResearchersCrawlerTaskInRest)
     */
    /**
     *
     * @param user
     * @param task_code
     * @param email
     * @param task_kind
     * @param extra_msg
     * @return
     */
    public static boolean notifyResultsOfTask(String user, String task_code, 
                                                String email, String task_kind, 
                                                String extra_msg)
    {
        LOG.log(Level.INFO, "Notyfing task is finish ({0}, {1}, {2})", 
                new Object[]{user, task_code, task_kind});
        
        boolean success = false;
        
        try {
            
            String subject = "Your " + task_kind + " task has been finished [code=" + task_code + "]";
            
            String serverUrl = TheConfig.getInstance().getString(TheConfig.SERVER_URL);
            
            // Get the email layout
            InputStream inputStream =  Mailer.class.getClassLoader().getResourceAsStream("email/layout.html");
            String emailHtml = IOUtils.toString(inputStream);
            
            // Get html that contains the result files
            String htmlFile =   "<li>" +
                                    "#FILE_NAME# | " +
                                    "<a target='_blank' href='#HREF#'>" +
                                        "Download" +
                                    "</a>" +
                                "</li>";
            String htmlListFiles = "";
            List<String> fresults = TaskFileManager.getResultFiles(user, task_code);
            for(String iFileName : fresults){
                String iFileUrl = serverUrl + "/download-file.jsp"
                                            + "?task-code="+task_code  
                                            + "&file-name="+iFileName;
                
                String iHtmlFile = htmlFile.replaceAll("#HREF#", iFileUrl);
                iHtmlFile = iHtmlFile.replaceAll("#FILE_NAME#", iFileName);

                htmlListFiles += iHtmlFile;
            }
            
            // Set the the list files, server url, task code and extra message 
            emailHtml = emailHtml.replaceAll("#LIST_FILES#", htmlListFiles);
            emailHtml = emailHtml.replaceAll("#SERVER_URL#", serverUrl);
            emailHtml = emailHtml.replaceAll("#TASK_CODE#", task_code);
            emailHtml = emailHtml.replaceAll("#EXTRA_MSG#", extra_msg);
            
            // Send Mail
            Mailer.sendMail(email, subject, emailHtml);           
            
            success = true;
        } 
        catch (MessagingException ex){
            LOG.log(Level.SEVERE, "Error sending email to " + email, ex);            
            TaskFileManager.notifyResultError(user, task_code, "Error sending email to " + email);
        } 
        catch (Exception ex){
            LOG.log(Level.SEVERE, "Error sending email to " + email, ex);            
            TaskFileManager.notifyResultError(user, task_code, "Error sending email to " + email);
        }
        
        return success;
    }
}
