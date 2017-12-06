//package com.vorxsoft.ieye.blg.mq;
//
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.apache.activemq.command.ActiveMQTopic;
//
//import javax.jms.*;
//
//
//public class Publisher {
//  private String host = "localhost";
//  private int port=5672;
//  private String user="admin";
//  private String password="password";
//  private String topicName = "topic://event";
//  private MessageProducer producer;
//  private ActiveMQConnectionFactory factory;
//  private Connection connection;
//  private Session session;
//  private Destination destination;
//
//  public Publisher(String host, int port) {
//    this.host = host;
//    this.port = port;
//  }
//
//  public Publisher() {
//  }
//
//  public void init() throws JMSException {
//    //String connectionURI = "amqp://" + host + ":" + port;
//    String connectionURI = "tcp://" + host + ":" + port;
//    System.out.println(connectionURI);
//    factory = new ActiveMQConnectionFactory(connectionURI);
//    connection = factory.createConnection(user, password);
//    connection.start();
//    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//    //destination = session.createTopic(topicName);
//    destination = new ActiveMQTopic(topicName);
//    producer = session.createProducer(destination);
//    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
//    return;
//
//  }
//
//  public void publishMsg(String msg) throws JMSException {
//    init();
//    TextMessage tmsg = session.createTextMessage(msg);
//    producer.send(tmsg);
//    connection.close();
//    return;
//  }
////  public static void main(String[] args) throws JMSException, InvalidProtocolBufferException {
////    Publisher publisher= new Publisher("192.168.20.222",61616);
////    //Publisher publisher= new Publisher();
////    //publisher.init();
////    for (int i = 0; ; i++) {
////      DefaultReply req = DefaultReply.newBuilder().setResult(i).setSBusinessID(SimpleEventClientStart.randomString()).build();
//////      byte[] result=req.toByteArray() ;
//////      DefaultReply req2= DefaultReply.parseFrom(result);
//////      System.out.println(req2);
//////      String jsonFormat =JsonFormat.printer().print(req2.toBuilder());
//////      System.out.println(jsonFormat);
////      String a = JsonFormat.printer().print(req.toBuilder());
////      System.out.println(a);
////      publisher.publishMsg(a);
////    }
////  }
//}
