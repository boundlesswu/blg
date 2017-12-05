package com.vorxsoft.ieye.blg.action;//package com.vorxsoft.ieye.blg.action;
//
//import java.security.Security;
//import java.util.Date;
//import java.util.Properties;
//
//import javax.mail.Authenticator;
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.AddressException;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//
//
//
//public class SendEmail {
//
//  public static void main(String[] args) throws AddressException,
//          MessagingException {
//    String SEND_USER = "2569000943@qq.com";
//    String SEND_UNAME = "2569000943";
//    String SEND_PWD = "********";
//    String VALUE_SMTP = "smtp.qq.com";
//    Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
//    //final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
//    // Get a Properties object
//    Properties props = System.getProperties();
//    // props.setProperty("mail.smtp.host", "smtp.gmail.com");
//    props.setProperty("mail.smtp.host", "smtp.qq.com");
//    //props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
//    //props.setProperty("mail.smtp.socketFactory.fallback", "false");
//    props.setProperty("mail.smtp.port", "25");
//    //props.setProperty("mail.smtp.port", "587");
//    //props.setProperty("mail.smtp.socketFactory.port", "25");
//    //props.setProperty("mail.smtp.socketFactory.port", "587");
//    props.put("mail.smtp.auth", "true");
//    final String username = "2569000943";
//    final String password = "chHorse123";
//    Session session = Session.getDefaultInstance(props,
//            new Authenticator() {
//              protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password);
//              }
//            });
//
//    // -- Create a new message --
//    session.setDebug(true);
//    Message msg = new MimeMessage(session);
//
//    // -- Set the FROM and TO fields --
//    msg.setFrom(new InternetAddress(username + "@qq.com"));
//    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(
//            "798210413@qq.com", false));
//    msg.setSubject("Hello---");
//    msg.setText("How are you");
//    msg.setSentDate(new Date());
//    Transport transport = session.getTransport("smtp");
//    // smtp验证，就是你用来发邮件的邮箱用户名密码
//    transport.connect(VALUE_SMTP, SEND_UNAME, SEND_PWD);
//    // 发送
//    transport.sendMessage(msg, msg.getAllRecipients());
//    Transport.send(msg);
//    transport.close();
//
//    System.out.println("Message sent.");
//  }
//
//}
