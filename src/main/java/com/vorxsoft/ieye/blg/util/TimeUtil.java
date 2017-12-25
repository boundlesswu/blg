package com.vorxsoft.ieye.blg.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
  static public String timestmap2string(Timestamp t){
    return "test";
  }
  static public Timestamp string2timestamp(String  s) {
    //String的类型必须形如： yyyy-mm-dd hh:mm:ss[.f...] 这样的格式，中括号表示可选，否则报错！！！
    //System.out.println(s);
    return Timestamp.valueOf(s);
  }
  static public long string2timestamplong(String  s) {
    //String的类型必须形如： yyyy-mm-dd hh:mm:ss[.f...] 这样的格式，中括号表示可选，否则报错！！！
    //System.out.println(s);
    return Timestamp.valueOf(s).getTime();
  }
  static public  String getDateTimeNs(){
    String msg="";
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS");
    msg += sdf.format(date);
    System.out.println(msg);
    return msg;
  }


}
