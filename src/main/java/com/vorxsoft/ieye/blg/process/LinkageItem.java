package com.vorxsoft.ieye.blg.process;

import com.vorxsoft.ieye.blg.util.Generalid;
import com.vorxsoft.ieye.proto.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.vorxsoft.ieye.blg.util.Constant.*;
import static com.vorxsoft.ieye.blg.util.Constant.sLinkageEmail;
import static com.vorxsoft.ieye.blg.util.Constant.sLinkageSnapshot;

public class LinkageItem {
  private int eventId;
  private Linkage linkage;
  private String  businessID;
  private long linkageTime;
  private boolean hasInsertDB = false;
  private boolean needRelinkage = false;
  private boolean hasReLinkage  = false;
  private int execResult = 0;

  public int getExecResult() {
    return execResult;
  }

  public void setExecResult(int execResult) {
    this.execResult = execResult;
  }

  public boolean linkageItem2DB(Connection conn) throws SQLException {
    String type = getLinkage().getSLinkageType();
    String linkageDesc;
    boolean ret = false;
    if (type.equals(sLinkageClient)) {
      linkageDesc = "客户端联动"+" 摄像机资源编号:"+getLinkage().getSArgs(0)+
                   " 告警声音"+((Integer.valueOf(getLinkage().getSArgs(1)) == 0)?"关闭":"打开")+
                   " 客户端弹出:" + getLinkage().getSArgs(2) +
                   " 字符叠加" + getLinkage().getSArgs(3) +
                    " 手动" + ((Integer.valueOf(getLinkage().getSArgs(4)) == 0)?"不":"要"+ "干预")+
                     " 报警联络人" + getLinkage().getSArgs(5);
    } else if (type.equals(sLinkageWall)) {
//      arg1	res_no	摄像机资源编号
//      arg2	res_screen_no	屏幕资源编号
//      arg3	window	窗口序号
      linkageDesc = "切换上墙"+"摄像机资源编号"+getLinkage().getSArgs(0)+
                    " 屏幕资源编号"+getLinkage().getSArgs(1)+
                     " 窗口序号"+getLinkage().getSArgs(2);
    } else if (type.equals(sLinkagePreset)) {
//      云台联动-调用预置点	linkage_preset			可设置多条
//      arg1	res_no	摄像机资源编号
//      arg2	preset_no	预置点编号（第一次调用该预置点）
//      arg3	preset_no	事件结束返回预置点编号（第二次调用该预置点）
//      arg4	秒数	持续时间，默认60秒。超过该值调用arg3返回预置点。
//      rpc PTZPreset (PTZPresetRequest) returns (PTZPresetReply)预置位设置
      linkageDesc = " 云台联动-调用预置点" + " 摄像机资源编号" + getLinkage().getSArgs(0)+
                    " 第一次调用预置点编号" + getLinkage().getSArgs(1)+
                    " 第二次调用预置点编号" + getLinkage().getSArgs(2)+
                     " 持续时间:"+getLinkage().getSArgs(3)+ "秒";
    } else if (type.equals(sLinkageCruise)) {
//      云台联动-调用巡航	linkage_cruise			可设置多条
//      arg1	res_no	摄像机资源编号
//      arg2	cruise_no	巡航编号
//      arg3	秒数	默认60秒。超过该值停止巡航。
//      rpc PTZCruise (PTZCruiseRequest) returns (PTZCruiseReply)//巡航设置*************/
//      rpc PTZCruiseResult (PTZCruiseReply) returns (DefaultReply)  结果
      linkageDesc = " 云台联动-调用巡航" + " 摄像机资源编号" + getLinkage().getSArgs(0)+
                     " 巡航编号:" +  getLinkage().getSArgs(1)+
                     " 持续时间:" + getLinkage().getSArgs(2)+ "秒";
    } else if (type.equals(sLinkageSio)) {
//      报警输出	linkage_sio			可设置多条
//      arg1	res_no	开关量输出通道资源编号
//      arg2	0/1	开关量输出通道闭/开
//      arg3	秒数	默认60秒。超过该值停止输出通道联动
//      rpc AlarmControl (AlarmControlRequest) returns (DefaultReply)
      linkageDesc = "开关量报警输出" + " 开关量输出通道资源编号:" + getLinkage().getSArgs(0)+
                     " 开关量输出通道" + ((Integer.valueOf(getLinkage().getSArgs(1)) == 0)?"闭":"开")+
                      " 默认时间" + getLinkage().getSArgs(2) + "秒";
    } else if (type.equals(sLinkageRecord)) {
//      联动录像	linkage_record			可设置多条
//      arg1	res_no	摄像机资源编号
      linkageDesc = "联动录像" + "  摄像机资源编号:" + getLinkage().getSArgs(0);
    } else if (type.equals(sLinkageSms)) {
//      短信联动	linkage_sms			每个事件规则仅1条
//      arg1	文字信息	短信内容
//      arg2	手机号码	要发送的手机号码，逗号隔开
      linkageDesc = "短信联动" + " 短信内容"+ getLinkage().getSArgs(0)+
                    " 手机号码" + getLinkage().getSArgs(1);
    } else if (type.equals(sLinkageSnapshot)) {
//      告警抓图	linkage_snapshot			每个事件规则仅1条
//      arg1	res_no	摄像机资源编号，多个用逗号隔开。
      linkageDesc = "告警抓图" + " 摄像机资源编号:" + getLinkage().getSArgs(0);
    } else if (type.equals(sLinkageEmail)) {
//      发送邮件	linkage_email			每个事件规则仅1条
//      arg1	文字信息	邮件主题
//      arg2	文字信息	邮件正文
//      arg3	邮件地址	接收人邮箱，多条用逗号隔开
      linkageDesc =  " 发送邮件" +  " 邮件主题"+getLinkage().getSArgs(0)+
                     " 邮件正文" + getLinkage().getSArgs(1)+
                      " 邮件地址" + getLinkage().getSArgs(2);
    }else{
      return ret;
    }
//    event_log_id	int	4			√	√		事件日志id
//    linkage_type	varchar	50				√		联动动作类型
//    linkage_desc	varchar	255						联动动作详细描述
//    exec_result	int	4	0			√		联动结果
//    0-成功;非0记录错误码
    String sql = "INSERT INTO tl_linkage(event_log_id,linkage_type,linkage_desc,exec_result) VALUES (?,?,?,?)";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1, getEventId());
    pstmt.setString(2, getLinkage().getSLinkageType());
    pstmt.setString(3, linkageDesc);
    pstmt.setInt(4 , getExecResult());

    if (pstmt.executeUpdate() > 0) {
      ret = true;
      ResultSet rs = pstmt.getGeneratedKeys();
      rs.close();
    }
    pstmt.close();
    return ret;
  }

  public boolean isHasInsertDB() {
    return hasInsertDB;
  }

  public void setHasInsertDB(boolean hasInsertDB) {
    this.hasInsertDB = hasInsertDB;
  }

  public boolean isNeedRelinkage() {
    return needRelinkage;
  }

  public void setNeedRelinkage(boolean needRelinkage) {
    this.needRelinkage = needRelinkage;
  }

  public boolean isHasReLinkage() {
    return hasReLinkage;
  }

  public void setHasReLinkage(boolean hasReLinkage) {
    this.hasReLinkage = hasReLinkage;
  }

  public LinkageItem() {
  }

  private LinkageItem(Builder builder) {
    setEventId(builder.eventId);
    setLinkage(builder.linkage);
    setBusinessID(builder.businessID);
    setLinkageTime(builder.linkageTime);
    setHasInsertDB(builder.hasInsertDB);
    setNeedRelinkage(builder.needRelinkage);
    setHasReLinkage(builder.hasReLinkage);
    setExecResult(builder.execResult);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public int getEventId() {
    return eventId;
  }

  public void setEventId(int eventId) {
    this.eventId = eventId;
  }

  public Linkage getLinkage() {
    return linkage;
  }

  public void setLinkage(Linkage linkage) {
    this.linkage = linkage;
  }

  public String getBusinessID() {
    return businessID;
  }

  public void setBusinessID(String businessID) {
    this.businessID = businessID;
  }

  public long getLinkageTime() {
    return linkageTime;
  }

  public void setLinkageTime(long linkageTime) {
    this.linkageTime = linkageTime;
  }

  @Override
  public String toString() {
    return "LinkageItem{" +
            "eventId=" + eventId +
            ", linkage=" + linkage +
            ", businessID='" + businessID + '\'' +
            ", linkageTime=" + linkageTime +
            '}';
  }


  public static final class Builder {
    private int eventId;
    private Linkage linkage;
    private String businessID;
    private long linkageTime;
    private boolean hasInsertDB;
    private boolean needRelinkage;
    private boolean hasReLinkage;
    private int execResult;

    private Builder() {
    }

    public Builder eventId(int val) {
      eventId = val;
      return this;
    }

    public Builder linkage(Linkage val) {
      linkage = val;
      return this;
    }

    public Builder businessID(String val) {
      businessID = val;
      return this;
    }

    public Builder linkageTime(long val) {
      linkageTime = val;
      return this;
    }

    public Builder hasInsertDB(boolean val) {
      hasInsertDB = val;
      return this;
    }

    public Builder needRelinkage(boolean val) {
      needRelinkage = val;
      return this;
    }

    public Builder hasReLinkage(boolean val) {
      hasReLinkage = val;
      return this;
    }

    public Builder execResult(int val) {
      execResult = val;
      return this;
    }

    public LinkageItem build() {
      return new LinkageItem(this);
    }
  }
}
