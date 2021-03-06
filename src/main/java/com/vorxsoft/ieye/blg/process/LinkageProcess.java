package com.vorxsoft.ieye.blg.process;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.vorxsoft.ieye.blg.grpc.LogServiceClient;
import com.vorxsoft.ieye.blg.grpc.VsIeyeClient;
import com.vorxsoft.ieye.blg.util.*;
import com.vorxsoft.ieye.proto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.vorxsoft.ieye.blg.util.Constant.*;

public class LinkageProcess implements Runnable {
  private String name;
  //private Jedis jedis;
  //private RedisUtil redisUtil;
  private ConcurrentLinkedQueue<String> cq ;
  private Connection conn;
  VsIeyeClient cmsIeyeClient;
  HashMap<String, LinkageItem> linkageItemHashMap;
  private ResUtil resUtil;
  private SmsUtil smsUtil;
  private EmailUtil emailUtil;
  private static Logger logger = LogManager.getLogger(LinkageProcess.class.getName());
  private LogServiceClient logServiceClient;

  public ConcurrentLinkedQueue<String> getCq() {
    return cq;
  }

  public void setCq(ConcurrentLinkedQueue<String> cq) {
    this.cq = cq;
  }

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

  public HashMap<String, LinkageItem> getLinkageItemHashMap() {
    return linkageItemHashMap;
  }

  public void setLinkageItemHashMap(HashMap<String, LinkageItem> linkageItemHashMap) {
    this.linkageItemHashMap = linkageItemHashMap;
  }

  public static Logger getLogger() {
    return logger;
  }

  public static void setLogger(Logger logger) {
    LinkageProcess.logger = logger;
  }

  public LogServiceClient getLogServiceClient() {
    return logServiceClient;
  }

  public void setLogServiceClient(LogServiceClient logServiceClient) {
    this.logServiceClient = logServiceClient;
  }

//HashMap<String,VsIeyeClient>
  //Publisher publisher;

  public LinkageProcess() {
    linkageItemHashMap = new HashMap<>();
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
    resUtil = new ResUtilImpl();
    resUtil.init(conn);
  }

  @Override
  public void run() {
    for (int i = 0; ; i++) {
      //System.out.println(name + "运行  :  " + i);
      try {
        processLinkages();
        Thread.sleep(100);
        //Thread.sleep((int) Math.random() * 1000);
        processLinkageItemHashMap();
        Thread.sleep(100);
        //Thread.sleep((int) Math.random() * 1000);
        if (i % 20 == 0) {
          System.out.println("process :" + getName() + "is running");
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
        getLogger().error(e.getMessage(),e);
      } catch (Exception e) {
        e.printStackTrace();
        getLogger().error(e.getMessage(),e);
      }
    }
  }

  private boolean hasInsertDB = false;
  private boolean needRelinkage = false;
  private boolean hasReLinkage = false;


  public void processLinkageItemHashMap() throws SQLException {
    Iterator iter = getLinkageItemHashMap().entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      Object key = entry.getKey();
      Object val = entry.getValue();
      LinkageItem linkageItem = (LinkageItem) val;
      if (linkageItem.isHasInsertDB() != true) {
        if (linkageItem.linkageItem2DB(getConn())) {
          linkageItem.setHasInsertDB(true);
        }
      }
      if (linkageItem.isNeedRelinkage()) {
        if (!linkageItem.isHasReLinkage()) {
          // re send linkage
          reProcessLinkage(linkageItem,linkageItem.getEventId());
        }
      }
      if(linkageItem.isHasInsertDB() && !linkageItem.isNeedRelinkage()){
        getLinkageItemHashMap().remove(linkageItem);
      }
      if(linkageItem.isHasInsertDB()  && linkageItem.isNeedRelinkage() && linkageItem.isHasReLinkage()){
        getLinkageItemHashMap().remove(linkageItem);
      }

    }
  }

  public boolean isOverTime(String tArg, long diffTime) {
    int duratime = 60;
    try {
      duratime = Integer.parseInt(tArg);
    } catch (NumberFormatException e) {
      System.out.println(e);
      duratime = 60;
    }
    if (diffTime >= duratime)
      return true;
    else
      return false;
  }

  public void reProcessLinkage(LinkageItem linkageItem, int eventId) {
    Linkage linkage = linkageItem.getLinkage();
    String type = linkage.getSLinkageType();
    long currentTime = System.currentTimeMillis();
    long diffTime = (currentTime - linkageItem.getLinkageTime()) / 1000;
    if (type.equals(sLinkagePreset)) {
      //      云台联动-调用预置点	linkage_preset			可设置多条
//      arg1	res_no	摄像机资源编号
//      arg2	preset_no	预置点编号（第一次调用该预置点）
//      arg3	preset_no	事件结束返回预置点编号（第二次调用该预置点）
//      arg4	秒数	持续时间，默认60秒。超过该值调用arg3返回预置点。
//      rpc PTZPreset (PTZPresetRequest) returns (PTZPresetReply)预置位设置
      String resNo = linkage.getSArgs(0);
      String presetNo1 = linkage.getSArgs(1);
      String presetNo2 = linkage.getSArgs(2);
      String bussinessId = Generalid.GetBusinessID();
      ///第二次调用arg3
      if (isOverTime(linkage.getSArgs(3), diffTime)) {
        PTZPresetRequest request = PTZPresetRequest.newBuilder().
                setSResNo(resNo).
                setNResid(resUtil.getResId(resNo)).
                setSBusinessID(bussinessId).
                setEmAct(PTZ_ACT.PTZ_CALL_PRESET).
                setSPresetNo(presetNo2).build();
        getCmsIeyeClient().pTZPreset(request);
        linkageItem.setHasReLinkage(true);
        //addLinkageItemHashMap(eventId, linkage, bussinessId, true, false, true);
      }
    } else if (type.equals(sLinkageCruise)) {
      //      云台联动-调用巡航	linkage_cruise			可设置多条
//      arg1	res_no	摄像机资源编号
//      arg2	cruise_no	巡航编号
//      arg3	秒数	默认60秒。超过该值停止巡航。
//      rpc PTZCruise (PTZCruiseRequest) returns (PTZCruiseReply)//巡航设置*************/
//      rpc PTZCruiseResult (PTZCruiseReply) returns (DefaultReply)  结果
      String resNo = linkage.getSArgs(0);
      String cruiseNo = linkage.getSArgs(1);
      String bussinessId = Generalid.GetBusinessID();
      if (isOverTime(linkage.getSArgs(2), diffTime)) {
        PTZCruiseRequest request = PTZCruiseRequest.newBuilder().setSResNo(resNo).setEmAct(OPT_ACT.OA_OFF).
                setCruiseNo(cruiseNo).setSBusinessID(bussinessId).build();
        getCmsIeyeClient().pTZCruise(request);
        //addLinkageItemHashMap(eventId, linkage, bussinessId, true, false, true);
        linkageItem.setHasReLinkage(true);
      }
    } else if (type.equals(sLinkageSio)) {
//      报警输出	linkage_sio			可设置多条
//      arg1	res_no	开关量输出通道资源编号
//      arg2	0/1	开关量输出通道闭/开
//      arg3	秒数	默认60秒。超过该值停止输出通道联动
//      rpc AlarmControl (AlarmControlRequest) returns (DefaultReply)
      String resNo = linkage.getSArgs(0);
      String bussinessId = Generalid.GetBusinessID();
      int nCmd = Integer.parseInt(linkage.getSArgs(2));
      if(isOverTime(linkage.getSArgs(2), diffTime)) {
        AlarmControlRequest request = AlarmControlRequest.newBuilder().setSResNo(resNo).
                setSBusinessID(bussinessId).setNCmd(0).build();
        getCmsIeyeClient().alarmControl(request);
        //addLinkageItemHashMap(eventId, linkage, bussinessId, true, false, true);
        linkageItem.setHasReLinkage(true);
      }
    }else{

    }
  }

  public void addLinkageItemHashMap(int eventId, int eventLogId,Linkage linkage,
                                    String bussinessId,
                                    boolean hasInsertDB, boolean needRelinkage, boolean hasReLinkage) {
    LinkageItem linkageItem = LinkageItem.newBuilder().eventLogId(eventLogId).
            eventId(eventId).linkage(linkage).
            linkageTime(System.currentTimeMillis()).
            businessID(bussinessId).
            hasInsertDB(false).needRelinkage(needRelinkage).hasReLinkage(hasInsertDB).
            build();

    if (getLinkageItemHashMap() == null)
      setLinkageItemHashMap(new HashMap<>());
    getLinkageItemHashMap().put(bussinessId, linkageItem);
  }
  public void processLinkage(Linkage linkage, Events event){
    int eventId = event.getNEventID();
    int eventLogId = event.getNEventlogID();
    String eventName = event.getSEventName();
    String happenTime = event.getSHappentime();
    processLinkage(linkage,eventId,eventLogId,eventName,happenTime);
  }
  public void processLinkage(Linkage linkage, int eventId,int eventLogId, String eventName, String happenTime) {
    String type = linkage.getSLinkageType();
    if (type.equals(sLinkageClient)) {
      getLogger().debug("sLinkageClient " + linkage);
    } else if (type.equals(sLinkageWall)) {
//      arg1	res_no	摄像机资源编号
//      arg2	res_screen_no	屏幕资源编号
//      arg3	window	窗口序号
      getLogger().debug("sLinkageWall"+linkage);
      String resNo = linkage.getSArgs(0);
      String resScreenNo = linkage.getSArgs(1);
      int window = Integer.parseInt(linkage.getSArgs(2));
      String bussinessId = Generalid.GetBusinessID();
      LiveScreenRequest request = LiveScreenRequest.newBuilder().setSResNo(resNo).
              setSScreenResNo(resScreenNo).setNWindow(window).
              setSBusinessID(bussinessId).build();
      getCmsIeyeClient().PlayLiveScreen(request);
      addLinkageItemHashMap(eventId,eventLogId, linkage, bussinessId, false, false, false);
    } else if (type.equals(sLinkagePreset)) {
//      云台联动-调用预置点	linkage_preset			可设置多条
//      arg1	res_no	摄像机资源编号
//      arg2	preset_no	预置点编号（第一次调用该预置点）
//      arg3	preset_no	事件结束返回预置点编号（第二次调用该预置点）
//      arg4	秒数	持续时间，默认60秒。超过该值调用arg3返回预置点。
//      rpc PTZPreset (PTZPresetRequest) returns (PTZPresetReply)预置位设置
      getLogger().debug("sLinkagePreset"+linkage);
      String resNo = linkage.getSArgs(0);
      String presetNo1 = linkage.getSArgs(1);
      String presetNo2 = linkage.getSArgs(2);
      String bussinessId = Generalid.GetBusinessID();
      int duratime = Integer.parseInt(linkage.getSArgs(3));
      PTZPresetRequest request = PTZPresetRequest.newBuilder().setSResNo(resNo).
              setNResid(resUtil.getResId(resNo)).
              setSBusinessID(bussinessId).
              setEmAct(PTZ_ACT.PTZ_CALL_PRESET).
              setSPresetNo(presetNo1).build();
      getCmsIeyeClient().pTZPreset(request);
      addLinkageItemHashMap(eventId,eventLogId, linkage, bussinessId, false, true, false);
    } else if (type.equals(sLinkageCruise)) {
//      云台联动-调用巡航	linkage_cruise			可设置多条
//      arg1	res_no	摄像机资源编号
//      arg2	cruise_no	巡航编号
//      arg3	秒数	默认60秒。超过该值停止巡航。
//      rpc PTZCruise (PTZCruiseRequest) returns (PTZCruiseReply)//巡航设置*************/
//      rpc PTZCruiseResult (PTZCruiseReply) returns (DefaultReply)  结果
      getLogger().debug("sLinkageCruise"+linkage);
      String resNo = linkage.getSArgs(0);
      String cruiseNo = linkage.getSArgs(1);
      String bussinessId = Generalid.GetBusinessID();
      PTZCruiseRequest request = PTZCruiseRequest.newBuilder().setSResNo(resNo).setEmAct(OPT_ACT.OA_ON).
              setCruiseNo(cruiseNo).setSBusinessID(bussinessId).build();
      getCmsIeyeClient().pTZCruise(request);
      addLinkageItemHashMap(eventId,eventLogId, linkage, bussinessId, false, true, false);
    } else if (type.equals(sLinkageSio)) {
//      报警输出	linkage_sio			可设置多条
//      arg1	res_no	开关量输出通道资源编号
//      arg2	0/1	开关量输出通道闭/开
//      arg3	秒数	默认60秒。超过该值停止输出通道联动
//      rpc AlarmControl (AlarmControlRequest) returns (DefaultReply)
      getLogger().debug("sLinkageSio"+linkage);
      String resNo = linkage.getSArgs(0);
      String bussinessId = Generalid.GetBusinessID();
      int nCmd = Integer.parseInt(linkage.getSArgs(2));
      AlarmControlRequest request = AlarmControlRequest.newBuilder().setSResNo(resNo).
              setSBusinessID(bussinessId).setNCmd(1).build();
      getCmsIeyeClient().alarmControl(request);
      addLinkageItemHashMap(eventId,eventLogId, linkage, bussinessId, false, true, false);
    } else if (type.equals(sLinkageRecord)) {
      getLogger().debug("sLinkageRecord"+linkage);
      String bussinessId = Generalid.GetBusinessID();
      String res_no = linkage.getSArgs(0);
      RecordControlRequest request =  RecordControlRequest.newBuilder().setSBusinessID(bussinessId).
                                      setSResNo(res_no).setBStart(true).setNRecType(4).setNStreamType(1).build();
      getCmsIeyeClient().recordControl(request);
      addLinkageItemHashMap(eventId,eventLogId, linkage, bussinessId, false, true, false);
    } else if (type.equals(sLinkageSms)) {
      getLogger().debug("sLinkageSms"+linkage);
      String bussinessId = Generalid.GetBusinessID();
      String desc = linkage.getSArgs(0);
      String phoneNum = linkage.getSArgs(1);
      try {
        SendSmsResponse response = smsUtil.sendSms(phoneNum,happenTime,eventName,desc);
        if(response.getCode().equals("OK")){
          getLogger().debug("success to send sms (eventName : " + eventName + "desc : "+desc +") to "+ phoneNum);
        }else{
          getLogger().error("failed to send sms (eventName : " + eventName + "desc : "+desc +") to "+ phoneNum+",because :" + response.getCode() + "  " + response.getMessage());
        }
      } catch (ClientException e) {
        e.printStackTrace();
        getLogger().error(e.getMessage(), e);
      }finally {
        addLinkageItemHashMap(eventId,eventLogId, linkage, bussinessId, false, false, false);
      }
    } else if (type.equals(sLinkageSnapshot)) {
      getLogger().debug("sLinkageSnapshot"+linkage);
    } else if (type.equals(sLinkageEmail)) {
      getLogger().debug("sLinkageEmail"+linkage);
      String bussinessId = Generalid.GetBusinessID();
      String subject = linkage.getSArgs(0);
      String content = linkage.getSArgs(1) + "\n"+ eventName+"@"+happenTime;
      String sendto = linkage.getSArgs(2);
      emailUtil.sendMail(sendto,subject,content);
      addLinkageItemHashMap(eventId,eventLogId, linkage, bussinessId, false, false, false);
    } else {
      getLogger().debug("error linage type "+linkage);
    }
  }

  public void processLinkages() throws com.googlecode.protobuf.format.JsonFormat.ParseException {
    List<ReportLinkageRequest> reportLinkageRequests = getReportLinkageRequest();
    if (reportLinkageRequests == null) {
      return;
    }
    for (int i = 0; i < reportLinkageRequests.size(); i++) {
      ReportLinkageRequest req = reportLinkageRequests.get(i);
      for (int j = 0; j < req.getEventWithLinkagesCount(); j++) {
        EventWithLinkage eventWithLinkage = req.getEventWithLinkages(j);
        Events event = eventWithLinkage.getEvent();
//        int eventId = event.getNEventID();
//        int eventLogId = event.getNEventlogID();
        for (int k = 0; k < eventWithLinkage.getLinkagesCount(); k++) {
          Linkage linkage = eventWithLinkage.getLinkages(k);
          processLinkage(linkage, event);
          //if(linkage.getSLinkageType().equals())

        }
      }
    }
  }

  public List<ReportLinkageRequest> getReportLinkageRequest() throws com.googlecode.protobuf.format.JsonFormat.ParseException {
    List<ReportLinkageRequest> reportLinkageRequests = new ArrayList<>();
    int i = 0;
    while(!getCq().isEmpty() ||  i > 10) {
      String tmp = getCq().poll();
      i++;
      ReportLinkageRequest.Builder builder = ReportLinkageRequest.newBuilder();
      try {
        JsonFormat.parser().merge(tmp, builder);
        ReportLinkageRequest req = builder.build();
        reportLinkageRequests.add(req);
      } catch (InvalidProtocolBufferException e) {
        e.printStackTrace();
        getLogger().error(e.getMessage(), e);
      }
    }
    return reportLinkageRequests;
    }

//    String patterKey = "eventWithLinkage_*";
//    Set<String> set = redisUtil.keys(patterKey);
//    if (set.size() == 0) {
//      //System.out.println("patterKey :" + patterKey + "is not exist");
//      //getLogger().debug("patterKey :" + patterKey + "is not exist");
//      return null;
//    }
//    List<ReportLinkageRequest> reportLinkageRequests = new ArrayList<>();
//    Iterator<String> it = set.iterator();
//    while (it.hasNext()) {
//      String keyStr = it.next();
//      String s = redisUtil.hget(keyStr, "req");
//      if (s == null || s.length() == 0) {
//        getLogger().debug("get request is null");
//        continue;
//      }
//
//      ReportLinkageRequest.Builder builder = ReportLinkageRequest.newBuilder();
//      try {
//        JsonFormat.parser().merge(s, builder);
//        ReportLinkageRequest req = builder.build();
//        reportLinkageRequests.add(req);
//        redisUtil.del(keyStr);
//      } catch (InvalidProtocolBufferException e) {
//        e.printStackTrace();
//        getLogger().error(e.getMessage(), e);
//      }
//    }
//    return reportLinkageRequests;
//  }
}
