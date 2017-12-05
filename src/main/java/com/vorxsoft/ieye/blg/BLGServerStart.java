package com.vorxsoft.ieye.blg;

import com.coreos.jetcd.Watch;
import com.coreos.jetcd.watch.WatchEvent;
import com.coreos.jetcd.watch.WatchResponse;
import com.vorxsoft.ieye.blg.grpc.VsIeyeClient;
import com.vorxsoft.ieye.blg.process.LinkageProcess;
import com.vorxsoft.ieye.microservice.MicroService;
import com.vorxsoft.ieye.microservice.MicroServiceImpl;
import com.vorxsoft.ieye.microservice.WatchCallerInterface;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BLGServerStart implements WatchCallerInterface {
  private static String serviceName = "server_blg";
  private static String hostip;
  private static int ttl = 60;
  private String registerCenterName;
  private String activemqName;
  private String activemqIp;
  private int activemqPort;
  private String redisName;


  @Override
  public void WatchCaller(Watch.Watcher watch) {
    WatchResponse ret = watch.listen();
    System.out.println("watcher response  " + watch.listen());
    for (int i = 0; i < ret.getEvents().size(); i++) {
      WatchEvent a = ret.getEvents().get(i);
      String key = a.getKeyValue().getKey().toString();
      String[] akey = key.split("/");
      String name = akey[1];
      String address = akey[3];
      switch (a.getEventType()) {
        case PUT:
          if (name.equals("server_cms")) {
            //update cms grpc client and  synchronize to process threads
          }
          if (name.equals("server_blg")) {
            //update blg grpc client and  synchronize to process threads
          }
          if (name.equals("server_log")) {
            //update log grpc client and  synchronize to process threads
          }
          if (name.equals("server_iaag")) {
            //update cms grpc client
          }
          break;
        case DELETE:
          if (name.equals("server_cms")) {
            //clear cms grpc client and  synchronize to process threads
          }
          if (name.equals("server_blg")) {
            //clear blg grpc client and  synchronize to process threads
          }
          if (name.equals("server_log")) {
            //clear log grpc client and  synchronize to process threads
          }
          if (name.equals("server_iaag")) {
            //clear cms grpc client
          }
          break;
        case UNRECOGNIZED:
          break;
      }
//      KeyValue keyVal = a.getKeyValue();
//      String key = a.getKeyValue().getKey().toString();
//      String value = a.getKeyValue().getKey

    }
  }

  private static String registerCenterAddress;
  private static int PORT = 66666;
  private Server server;
  private static String redisIp;
  private static int redisPort;
  private Connection conn;
  private String dbUrl;
  private static String dbname;
  private static String dbAddress;
  private static String driverClassName;
  private static String dbUser;
  private static String dbPasswd;
  private Jedis jedis;
  private VsIeyeClient cmsClient;
  private final String cfgFileName = "blg.xml";
  private InputStream cfgFile;

  public void setCmsClient(VsIeyeClient cmsClient) {
    this.cmsClient = cmsClient;
  }


  public void dbInit() throws SQLException, ClassNotFoundException {
    dbUrl = "jdbc:" + dbname + "://" + dbAddress;
    System.out.println("db url :" + dbUrl);
    Class.forName(driverClassName);
    conn = DriverManager.getConnection(dbUrl, dbUser, dbPasswd);
    //st = conn.createStatement();
  }

  public void redisInit() {
    jedis = new Jedis(redisIp, redisPort);
  }

  private void start() throws Exception {
    server = NettyServerBuilder.forPort(PORT).addService(new BLGServer(redisIp, redisPort).bindService()).build();
    server.start();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        BLGServerStart.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  public VsIeyeClient getCmsClient() {
    return cmsClient;
  }

  private void stop() {
    try {
      jedis.close();
      conn.close();
      server.awaitTermination(2, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {
    final BLGServerStart blgServerStart = new BLGServerStart();

    blgServerStart.cfgInit();
    MicroService myservice = new MicroServiceImpl();
    myservice.init(registerCenterAddress, blgServerStart);
    blgServerStart.start();
    blgServerStart.redisInit();
    blgServerStart.dbInit();

    myservice.RegisteWithHB(serviceName, hostip, PORT, ttl);
    myservice.SetWatcher("server_", true);

    String cmsAddress = myservice.Resolve("cms").toString();
    if (cmsAddress == null) {
      System.out.println("cannot resolve cms server  address");
    } else {
      System.out.println("successful resolve cms server  address:" + cmsAddress);
      blgServerStart.setCmsClient(new VsIeyeClient("cms", cmsAddress));
    }

    LinkageProcess linkageProcess = new LinkageProcess();
    linkageProcess.setCmsIeyeClient(blgServerStart.getCmsClient());
    linkageProcess.setName("linkageProcess");

    linkageProcess.dbInit(dbname, dbAddress, driverClassName, dbUser, dbPasswd);
    linkageProcess.redisInit(redisIp, redisPort);

    new Thread(linkageProcess).start();

    TimeUnit.SECONDS.sleep(3000);
  }

  public void getConfigPath() throws FileNotFoundException {
    String tmp = String.valueOf(this.getClass().getClassLoader().getResource(cfgFileName));
    System.out.println("tmp:" + tmp);
    if (tmp.startsWith("jar"))
      cfgFile = new FileInputStream(new File(System.getProperty("user.dir") + File.separator + cfgFileName));
    else
      cfgFile = this.getClass().getClassLoader().getResourceAsStream(cfgFileName);
  }

  public void cfgInit() throws FileNotFoundException {
    // 解析books.xml文件
    // 创建SAXReader的对象reader
    getConfigPath();
    SAXReader reader = new SAXReader();
    try {
      System.out.println("cfg file is:" + cfgFile);
      // 通过reader对象的read方法加载books.xml文件,获取docuemnt对象。
      //Document document = reader.read(new File(cfgFile));
      Document document = reader.read(cfgFile);
      // 通过document对象获取根节点bookstore
      Element bookStore = document.getRootElement();
      // 通过element对象的elementIterator方法获取迭代器
      Iterator it = bookStore.elementIterator();
      // 遍历迭代器，获取根节点中的信息（书籍）
      while (it.hasNext()) {
        //System.out.println("=====开始遍历某一本书=====");
        Element cfg = (Element) it.next();
        // 获取book的属性名以及 属性值
        List<Attribute> bookAttrs = cfg.attributes();
        System.out.println("cfgname :" + cfg.getName());
        for (Attribute attr : bookAttrs) {
          //System.out.println("属性名：" + attr.getName() + "--属性值：" + attr.getValue());
        }
        String tname = cfg.getName();
        //解析子节点的信息
        Iterator itt = cfg.elementIterator();
        while (itt.hasNext()) {
          Element bookChild = (Element) itt.next();
          String lname = bookChild.getName();
          String lvalue = bookChild.getStringValue();
          //System.out.println("节点名：" + bookChild.getName() + "--节点值：" + bookChild.getStringValue());
          if (tname.equals("info")) {
            if (lname.equals("hostip"))
              hostip = lvalue;
            else if (lname.equals("port"))
              PORT = Integer.parseInt(lvalue);
            else if (lname.equals("name"))
              serviceName = lvalue;
            else if (lname.equals("ttl"))
              ttl = Integer.parseInt(lvalue);
          }
          if (tname.equals("database")) {
            if (lname.equals("name"))
              dbname = lvalue;
            else if (lname.equals("address"))
              dbAddress = lvalue;
            else if (lname.equals("user"))
              dbUser = lvalue;
            else if (lname.equals("passwd"))
              dbPasswd = lvalue;
            else if (lname.equals("driverClassName"))
              driverClassName = lvalue;
          }
          if (tname.equals("registerCenter")) {
            if (lname.equals("name"))
              registerCenterName = lvalue;
            else if (lname.equals("address"))
              registerCenterAddress = lvalue;
          }
          if (tname.equals("redis")) {
            if (lname.equals("name"))
              redisName = lvalue;
            else if (lname.equals("ip"))
              redisIp = lvalue;
            else if (lname.equals("port"))
              redisPort = Integer.parseInt(lvalue);
          }
          if (tname.equals("activemq")) {
            if (lname.equals("name"))
              activemqName = lvalue;
            else if (lname.equals("ip"))
              activemqIp = lvalue;
            else if (lname.equals("port"))
              activemqPort = Integer.parseInt(lvalue);
          }
        }
        //System.out.println("=====结束遍历某一本书=====");
      }
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }

}
