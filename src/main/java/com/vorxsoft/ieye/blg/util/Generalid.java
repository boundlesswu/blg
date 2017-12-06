package com.vorxsoft.ieye.blg.util;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Generalid {
  private static Object lock =new Object();
  private static int businessNum=0;// =new Object();
  private static int streamid=0;// =new Object();

  public static String GetBusinessID() {
    synchronized (lock) {
      businessNum=businessNum+1;
    }
    if(businessNum>999){
      businessNum=456;
    }
    String date = dateFormat(new Date(), "yyyyMMddHHmmssSSS");
    String businessID = String.format("%s%03d",date,businessNum );
    return businessID;
  }
  public static int GetStreamID() {
    synchronized (lock) {
      streamid=streamid+1;
    }
    return streamid;
  }
  public static String dateFormat(Date date, String pattern) {
    if (date == null) {
      return null;
    }
    SimpleDateFormat dateFm = new SimpleDateFormat(pattern); // 格式化当前系统日期
    return dateFm.format(date);
  }
}
