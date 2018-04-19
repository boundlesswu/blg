package com.vorxsoft.ieye.blg;

import com.coreos.jetcd.Watch;
import com.coreos.jetcd.watch.WatchEvent;
import com.coreos.jetcd.watch.WatchResponse;
import com.vorxsoft.ieye.blg.grpc.LogServiceClient;
import com.vorxsoft.ieye.blg.grpc.VsIeyeClient;
import com.vorxsoft.ieye.blg.process.LinkageProcess;
import com.vorxsoft.ieye.blg.util.ConfigReadUtils;
import com.vorxsoft.ieye.blg.util.EmailUtil;
import com.vorxsoft.ieye.blg.util.SmsUtil;
import com.vorxsoft.ieye.microservice.MicroService;
import com.vorxsoft.ieye.microservice.MicroServiceImpl;
import com.vorxsoft.ieye.microservice.WatchCallerInterface;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static com.vorxsoft.ieye.proto.VSLogLevel.VSLogLevelInfo;


public class BLGServerStart implements WatchCallerInterface {
  private static String serviceName = "server_blg";
  private static String hostip;
  private static int ttl = 60;
  private String registerCenterName;
  private String activemqName;
  private String activemqIp;
  private int activemqPort;
  private String redisName;
  private static Logger logger = LogManager.getLogger(BLGServerStart.class.getName());
  private LogServiceClient logServiceClient;
  private String emailProtocol;
  private String emailServer;
  private String emailDomain;
  private String emailPort;
  private String emailUserName;
  private String emailPassword;
  private String aliyunSmsProduct;
  private String aliyunSmsDomain;
  private String aliyunSmsAccessKeyId;
  private String aliyunSmsAccessKeySecret;
  private String aliyunSmsTemplateCode;
  private String aliyunSmsSignName;
  //private RedisUtil redisUtil;
  private ConcurrentLinkedQueue<String> cq ;

  public ConcurrentLinkedQueue<String> getCq() {
    return cq;
  }

  public void setCq(ConcurrentLinkedQueue<String> cq) {
    this.cq = cq;
  }

  public LogServiceClient getLogServiceClient() {
    return logServiceClient;
  }

  public void setLogServiceClient(LogServiceClient logServiceClient) {
    this.logServiceClient = logServiceClient;
  }

  public static Logger getLogger() {
    return logger;
  }

  public static void setLogger(Logger logger) {
    BLGServerStart.logger = logger;
  }

  @Override
  public void WatchCaller(Watch.Watcher watch) {
    WatchResponse ret = watch.listen();
    System.out.println("watcher response  " + ret);
    getLogger().info("watcher response  " + ret);
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
  //private Jedis jedis;
  private VsIeyeClient cmsClient;
  private final String cfgFileName = "blg_service.xml";
  private InputStream cfgFile;

  private SmsUtil smsUtil;
  private EmailUtil emailUtil;

//  public RedisUtil getRedisUtil() {
//    return redisUtil;
//  }
//
//  public void setRedisUtil(RedisUtil redisUtil) {
//    this.redisUtil = redisUtil;
//  }

  public SmsUtil getSmsUtil() {
    return smsUtil;
  }

  public void setSmsUtil(SmsUtil smsUtil) {
    this.smsUtil = smsUtil;
  }

  public EmailUtil getEmailUtil() {
    return emailUtil;
  }

  public void setEmailUtil(EmailUtil emailUtil) {
    this.emailUtil = emailUtil;
  }

  public void setCmsClient(VsIeyeClient cmsClient) {
    this.cmsClient = cmsClient;
  }

  public void dbInit() throws SQLException, ClassNotFoundException {
    dbUrl = "jdbc:" + dbname + "://" + dbAddress;
    System.out.println("db url :" + dbUrl);
    Class.forName(driverClassName);
    conn = DriverManager.getConnection(dbUrl, dbUser, dbPasswd);
    getLogger().info("successful db init " + dbUrl + dbUser + dbPasswd);
    //st = conn.createStatement();
  }

//  public void redisInit() {
//    redisUtil = new RedisUtil(redisIp, redisPort);
//    //jedis = new Jedis(redisIp, redisPort);
//  }

  public void cqInit() {
    cq = new ConcurrentLinkedQueue<String>();
    //jedis = new Jedis(redisIp, redisPort);
  }

  private void start() throws Exception {
    //server = NettyServerBuilder.forPort(PORT).addService(new BLGServer(redisUtil).bindService()).build();
    server = NettyServerBuilder.forPort(PORT).addService(new BLGServer(cq).bindService()).build();
    server.start();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        getLogger().error("*** shutting down gRPC server since JVM is shutting down");
        BLGServerStart.this.stop();
        System.err.println("*** server shut down");
        getLogger().error("*** server shut down");
      }
    });
  }

  public VsIeyeClient getCmsClient() {
    return cmsClient;
  }

  private void stop() {
    try {
      //jedis.close();
      conn.close();
      server.awaitTermination(2, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
      getLogger().error(e.getMessage(), e);
    } catch (SQLException e) {
      e.printStackTrace();
      getLogger().error(e.getMessage(), e);
    }
  }

  public static void main(String[] args) throws Exception {
    final BLGServerStart blgServerStart = new BLGServerStart();

    blgServerStart.cfgInit();

    blgServerStart.setEmailUtil();
    blgServerStart.setSmsUtil();

    //blgServerStart.redisInit();
    blgServerStart.cqInit();
    blgServerStart.dbInit();

    MicroService myservice = new MicroServiceImpl();
    myservice.init(registerCenterAddress, blgServerStart);
    blgServerStart.start();

    myservice.RegisteWithHB(serviceName, hostip, PORT, ttl);
    myservice.SetWatcher("server_", true);

    String logAddress = myservice.Resolve("server_log");
    if (logAddress == null) {
      System.out.println("cannot resolve log server  address");
      blgServerStart.getLogger().warn("cannot resolve log server  address");
    } else {
      System.out.println("successful resolve log server  address:" + logAddress);
      blgServerStart.getLogger().info("successful resolve log server  address:" + logAddress);
      blgServerStart.setLogServiceClient(new LogServiceClient("log", logAddress));
      blgServerStart.getLogServiceClient().setHostNameIp(hostip);
      blgServerStart.getLogServiceClient().setpName(serviceName);
      String logContent = "successful resolve log server  address:" + logAddress;
      blgServerStart.getLogServiceClient().sentVSLog(logContent, VSLogLevelInfo);
    }

    String cmsAddress = myservice.Resolve("server_cms");
    if (cmsAddress == null) {
      System.out.println("cannot resolve cms server  address");
      getLogger().warn("cannot resolve cms server  address");
    } else {
      System.out.println("successful resolve cms server  address:" + cmsAddress);
      getLogger().info("successful resolve cms server  address:" + cmsAddress);
      blgServerStart.setCmsClient(new VsIeyeClient("cms", cmsAddress));
    }

    LinkageProcess linkageProcess = new LinkageProcess();
    linkageProcess.setCmsIeyeClient(blgServerStart.getCmsClient());
    linkageProcess.setName("linkageProcess");

    linkageProcess.dbInit(dbname, dbAddress, driverClassName, dbUser, dbPasswd);
    linkageProcess.setCq(blgServerStart.getCq());
    //linkageProcess.setRedisUtil(blgServerStart.getRedisUtil());
    linkageProcess.setEmailUtil(blgServerStart.getEmailUtil());
    linkageProcess.setSmsUtil(blgServerStart.getSmsUtil());

    new Thread(linkageProcess).start();

    TimeUnit.DAYS.sleep(3000);
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
    ConfigReadUtils configReadUtils = new ConfigReadUtils();
    configReadUtils.cfgInit();
    hostip = configReadUtils.getHostip();
    PORT = configReadUtils.getBlgPort();
    ttl = configReadUtils.getTtl();
    registerCenterAddress = configReadUtils.getRegisterCenterAddress();
    dbname = configReadUtils.getDbname();
    dbUser = configReadUtils.getDbUser();
    dbPasswd = configReadUtils.getDbPasswd();
    driverClassName = configReadUtils.getDriverClassName();
    dbAddress = configReadUtils.getDbAddress();
    redisName = configReadUtils.getRedisName();
    redisIp = configReadUtils.getRedisIP();
    redisPort = configReadUtils.getRedisPort();
    activemqName = configReadUtils.getActivemqName();
    activemqIp = configReadUtils.getActivemqIp();
    activemqPort = configReadUtils.getActivemqPort();

    emailProtocol = configReadUtils.getEmailProtocol();
    emailServer = configReadUtils.getEmailServer();
    emailDomain = configReadUtils.getEmailDomain();
    emailPort = configReadUtils.getEmailPort();
    emailUserName = configReadUtils.getEmailUserName();
    emailPassword = configReadUtils.getEmailPassword();
    aliyunSmsProduct = configReadUtils.getAliyunSmsProduct();
    aliyunSmsDomain = configReadUtils.getAliyunSmsDomain();
    aliyunSmsAccessKeyId = configReadUtils.getAliyunSmsAccessKeyId();
    aliyunSmsAccessKeySecret = configReadUtils.getAliyunSmsAccessKeySecret();
    aliyunSmsTemplateCode = configReadUtils.getAliyunSmsTemplateCode();
    aliyunSmsSignName = configReadUtils.getAliyunSmsSignName();
  }

  public void setEmailUtil() {
    setEmailUtil(new EmailUtil(emailProtocol, emailDomain, emailServer, emailPort, emailUserName, emailPassword));
  }

  public void setSmsUtil() {
    setSmsUtil(new SmsUtil(aliyunSmsProduct, aliyunSmsDomain, aliyunSmsAccessKeyId, aliyunSmsAccessKeySecret, aliyunSmsSignName, aliyunSmsTemplateCode));
  }
}
