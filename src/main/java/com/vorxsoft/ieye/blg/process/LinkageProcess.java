package com.vorxsoft.ieye.blg.process;

import com.google.protobuf.util.JsonFormat;
import com.vorxsoft.ieye.blg.grpc.VsIeyeClient;
import com.vorxsoft.ieye.proto.EventWithLinkage;
import com.vorxsoft.ieye.proto.Events;
import com.vorxsoft.ieye.proto.Linkage;
import com.vorxsoft.ieye.proto.ReportLinkageRequest;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.vorxsoft.ieye.blg.util.Constant.*;

public class LinkageProcess implements Runnable {
  private String name;
  private Jedis jedis;
  private Connection conn;
  VsIeyeClient cmsIeyeClient;
  //HashMap<String,VsIeyeClient>
  //Publisher publisher;

  public LinkageProcess() {
  }

  public LinkageProcess(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Jedis getJedis() {
    return jedis;
  }

  public void setJedis(Jedis jedis) {
    this.jedis = jedis;
  }

  public Connection getConn() {
    return conn;
  }

  public void setConn(Connection conn) {
    this.conn = conn;
  }

  public VsIeyeClient getCmsIeyeClient() {
    return cmsIeyeClient;
  }

  public void setCmsIeyeClient(VsIeyeClient cmsIeyeClient) {
    this.cmsIeyeClient = cmsIeyeClient;
  }


  public void dbInit(String dbname, String dbAddress, String driverClassName, String dbUser, String dbPasswd) throws SQLException, ClassNotFoundException {
    String dbUrl = "jdbc:" + dbname + "://" + dbAddress;
    System.out.println("db url :" + dbUrl);
    Class.forName(driverClassName);
    conn = DriverManager.getConnection(dbUrl, dbUser, dbPasswd);
    //st = conn.createStatement();
  }

  public void redisInit(String redisIP, int redisPort) {
    jedis = new Jedis(redisIP, redisPort);
  }

  @Override
  public void run() {
    for (int i = 0; ; i++) {
      //System.out.println(name + "运行  :  " + i);
      try {
        processLinkages();
        Thread.sleep((int) Math.random() * 10);
        if(i%2000 == 0) {
          System.out.println("process :" + getName() +  "is running");
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  public void processLinkage(Linkage linkage) {
    String type = linkage.getSLinkageType();
    if (type.equals(sLinkageClient)) {

    } else if (type.equals(sLinkageWall)) {
    } else if (type.equals(sLinkagePreset)) {
    } else if (type.equals(sLinkageCruise)) {
    } else if (type.equals(sLinkageSio)) {
    } else if (type.equals(sLinkageRecord)) {
    } else if (type.equals(sLinkageSms)) {
    } else if (type.equals(sLinkageSnapshot)) {
    } else if (type.equals(sLinkageEmail)) {

    }
  }

  public void processLinkages() {
    List<ReportLinkageRequest> reportLinkageRequests = getReportLinkageRequest();
    if (reportLinkageRequests == null) {
      return;
    }
    for (int i = 0; i < reportLinkageRequests.size(); i++) {
      ReportLinkageRequest req = reportLinkageRequests.get(i);
      for (int j = 0; j < req.getEventWithLinkagesCount(); j++) {
        EventWithLinkage eventWithLinkage = req.getEventWithLinkages(j);
        Events event = eventWithLinkage.getEvent();
        for (int k = 0; k < eventWithLinkage.getLinkagesCount(); k++) {
          Linkage linkage = eventWithLinkage.getLinkages(k);
          processLinkage(linkage);
          //if(linkage.getSLinkageType().equals())

        }
      }
    }
  }

  public List<ReportLinkageRequest> getReportLinkageRequest() {
    String patterKey = "eventWithLinkage_*";
    Set<String> set = jedis.keys(patterKey);
    if (set.size() == 0) {
      System.out.println("patterKey :" + patterKey + "is not exist");
      return null;
    }
    List<ReportLinkageRequest> reportLinkageRequests = new ArrayList<>();
    Iterator<String> it = set.iterator();
    while (it.hasNext()) {
      String keyStr = it.next();
      String s = getJedis().hget(keyStr, "req");
      if (s == null || s.length() == 0) {
        continue;
      }
      ReportLinkageRequest.Builder builder = ReportLinkageRequest.newBuilder();
      //JsonFormat.merge(s, builder);
      ReportLinkageRequest req = builder.build();
      reportLinkageRequests.add(req);
    }
    return reportLinkageRequests;
  }
}
