package roads.main;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mailer{  
	private Mailer() {}
	public static final Logger LOG = LoggerFactory.getLogger(Mailer.class);
	public static void mail(String msg, String to, String header) {
		Mailer.send(PropertiesManager.getPropStringError("email"),PropertiesManager.getPropStringError("emailPass"), to, header, msg);
	}
	public static void send(String from,String password,String to,String sub,String msg){  
		//Get properties object    
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.office365.com");    
		props.put("mail.smtp.socketFactory.port", "587");   
		props.put("mail.smtp.socketFactory.class",    
				"javax.net.SocketFactory");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		props.put("mail.smtp.starttls.required", "true");

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", "587");

		//get Session   

		Session session = Session.getDefaultInstance(props,    
				new Authenticator() {    
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {    
				return new PasswordAuthentication(from,password);  
			}    
		});    
		//compose message    
		try {    
			MimeMessage message = new MimeMessage(session);    
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));    
			message.setSubject(sub);
			message.setText(msg);
			session.getTransport("smtp");
			//send message  
			Transport.send(message);
		} catch (MessagingException e) {LOG.error("An error occured while sending mail.", e);}   

	} }