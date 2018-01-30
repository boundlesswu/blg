package com.vorxsoft.ieye.blg.util;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @Author boundlesswu
 * @Description
 * @Date 2018-01-29 14:06
 **/
public class EmailUtil {
  private static  String protocol = "SMTP";
  private static String domain = "163.com";
  private static String server ="smtp.163.com";
  private static String port = "25";
  private static String userName = "18611813097";
  private static String password = "vorx123";

  public static String getProtocol() {
    return protocol;
  }

  public static void setProtocol(String protocol) {
    EmailUtil.protocol = protocol;
  }

  public static String getDomain() {
    return domain;
  }

  public static void setDomain(String domain) {
    EmailUtil.domain = domain;
  }

  public static String getServer() {
    return server;
  }

  public static void setServer(String server) {
    EmailUtil.server = server;
  }

  public static String getPort() {
    return port;
  }

  public static void setPort(String port) {
    EmailUtil.port = port;
  }

  public static String getUserName() {
    return userName;
  }

  public static void setUserName(String userName) {
    EmailUtil.userName = userName;
  }

  public static String getPassword() {
    return password;
  }

  public static void setPassword(String password) {
    EmailUtil.password = password;
  }

  public EmailUtil() {
  }

  public EmailUtil(String protocol , String domain ,
                   String server , String port ,
                   String userName, String password) {
    setProtocol(protocol);
    setDomain(domain);
    setServer(server);
    setPort(port);
    setUserName(userName);
    setPassword(password);
  }

  public Address[] converAddress(String sendto){
    Address[] tos = null;
    if(sendto!=null){
      String[] a = sendto.split(",");
      tos = new InternetAddress[a.length ];
      for (int i = 0; i < a.length; i++) {
        try {
          tos[i] = new InternetAddress(a[i]);
        } catch (AddressException e) {
          e.printStackTrace();
        }
      }
    }
    return tos;
  }

  public void sendMail(String sendto, String subject, String content){
    // 1.创建一个程序与邮件服务器会话对象 Session
    Properties props = new Properties();
    props.setProperty("mail.transport.protocol", protocol);
    props.setProperty("mail.smtp.host", server);
    props.setProperty("mail.smtp.port", port);
    // 指定验证为true
    props.setProperty("mail.smtp.auth", "true");
    props.setProperty("mail.smtp.timeout","1000");
    // 验证账号及密码，密码需要是第三方授权码
    Authenticator auth = new Authenticator() {
      public PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(userName + "@"+domain, password);
      }
    };
    Session session = Session.getInstance(props, auth);

// 2.创建一个Message，它相当于是邮件内容
    Message message = new MimeMessage(session);
// 设置发送者
    try {
      message.setFrom(new InternetAddress(userName + "@"+domain));
    } catch (MessagingException e) {
      e.printStackTrace();
    }

// 设置发送方式与接收者
    try {
      message.setRecipients(MimeMessage.RecipientType.TO, converAddress(sendto));
      //message.setRecipients(Message.RecipientType.TO, converAddress(sendto));
    } catch (MessagingException e) {
      e.printStackTrace();
    }
// 设置主题
    try {
      message.setSubject(subject);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
// 设置内容
    try {
      message.setContent(content, "text/html;charset=utf-8");
    } catch (MessagingException e) {
      e.printStackTrace();
    }

// 3.创建 Transport用于将邮件发送
    try {
      Transport.send(message);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args){
    String sendto = "16500622@qq.com,boundlesswu@163.com";
    //String sendto = "16500622@qq.com";
    String subject = "邮件发送测试"+System.currentTimeMillis();
    String content = "This is a test mail!!!";
    EmailUtil emailUtil = new EmailUtil();
    emailUtil.sendMail(sendto,subject,content);
  }

}
