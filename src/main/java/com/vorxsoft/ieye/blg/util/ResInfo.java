package com.vorxsoft.ieye.blg.util;

public class ResInfo {
  private int res_id;
  private String res_uid;
  private String dev_no;
  private String ip_intranet;
  private int port_manage;
  private int svr_id;
  private String res_no;
  private String res_name;
  private String res_type;
  private int chn_type;
  private int dev_id;
  private String svr_no;
  private int sio_mode;
  private int online_state;

  public ResInfo(int res_id, String res_uid, String dev_no, String ip_intranet, int port_manage,
                 int svr_id, String res_no, String res_name, String res_type, int chn_type, int dev_id, String svr_no) {
    super();
    this.res_id = res_id;
    this.res_uid = res_uid;
    this.dev_no = dev_no;
    this.ip_intranet = ip_intranet;
    this.port_manage = port_manage;
    this.svr_no = svr_no;
    this.svr_id = svr_id;
    this.res_no = res_no;
    this.res_name = res_name;
    this.res_type = res_type;
    this.chn_type = chn_type;
    this.dev_id = dev_id;
  }


  public ResInfo() {
  }

  public ResInfo(int res_id, String res_uid, String dev_no, String ip_intranet, int port_manage, int svr_id) {
    super();
    this.res_id = res_id;
    this.res_uid = res_uid;
    this.dev_no = dev_no;
    this.ip_intranet = ip_intranet;
    this.port_manage = port_manage;
    this.svr_id = svr_id;
  }

  private ResInfo(Builder builder) {
    setRes_id(builder.res_id);
    setRes_uid(builder.res_uid);
    setDev_no(builder.dev_no);
    setIp_intranet(builder.ip_intranet);
    setPort_manage(builder.port_manage);
    setSvr_id(builder.svr_id);
    setRes_no(builder.res_no);
    setRes_name(builder.res_name);
    setRes_type(builder.res_type);
    setChn_type(builder.chn_type);
    setDev_id(builder.dev_id);
    setSvr_no(builder.svr_no);
    setSio_mode(builder.sio_mode);
    setOnline_state(builder.online_state);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public String toString() {
    return "ResInfo{" +
            "res_id=" + res_id +
            ", res_uid='" + res_uid + '\'' +
            ", dev_no='" + dev_no + '\'' +
            ", ip_intranet='" + ip_intranet + '\'' +
            ", port_manage=" + port_manage +
            ", svr_id=" + svr_id +
            ", res_no='" + res_no + '\'' +
            ", res_name='" + res_name + '\'' +
            ", res_type='" + res_type + '\'' +
            ", chn_type=" + chn_type +
            ", dev_id=" + dev_id +
            ", svr_no='" + svr_no + '\'' +
            ", sio_mode=" + sio_mode +
            ", online_state=" + online_state +
            '}';
  }

  public int getRes_id() {
    return res_id;
  }

  public void setRes_id(int res_id) {
    this.res_id = res_id;
  }

  public String getRes_uid() {
    return res_uid;
  }

  public void setRes_uid(String res_uid) {
    this.res_uid = res_uid;
  }

  public String getDev_no() {
    return dev_no;
  }

  public void setDev_no(String dev_no) {
    this.dev_no = dev_no;
  }

  public String getIp_intranet() {
    return ip_intranet;
  }

  public void setIp_intranet(String ip_intranet) {
    this.ip_intranet = ip_intranet;
  }

  public int getPort_manage() {
    return port_manage;
  }

  public void setPort_manage(int port_manage) {
    this.port_manage = port_manage;
  }

  public int getSvr_id() {
    return svr_id;
  }

  public void setSvr_id(int svr_id) {
    this.svr_id = svr_id;
  }

  public String getRes_no() {
    return res_no;
  }

  public void setRes_no(String res_no) {
    this.res_no = res_no;
  }

  public String getRes_name() {
    return res_name;
  }

  public void setRes_name(String res_name) {
    this.res_name = res_name;
  }

  public String getRes_type() {
    return res_type;
  }

  public void setRes_type(String res_type) {
    this.res_type = res_type;
  }

  public int getChn_type() {
    return chn_type;
  }

  public void setChn_type(int chn_type) {
    this.chn_type = chn_type;
  }

  public int getDev_id() {
    return dev_id;
  }

  public void setDev_id(int dev_id) {
    this.dev_id = dev_id;
  }

  public String getSvr_no() {
    return svr_no;
  }

  public void setSvr_no(String svr_no) {
    this.svr_no = svr_no;
  }

  public int getSio_mode() {
    return sio_mode;
  }

  public void setSio_mode(int sio_mode) {
    this.sio_mode = sio_mode;
  }

  public int getOnline_state() {
    return online_state;
  }

  public void setOnline_state(int online_state) {
    this.online_state = online_state;
  }

  public static final class Builder {
    private int res_id;
    private String res_uid;
    private String dev_no;
    private String ip_intranet;
    private int port_manage;
    private int svr_id;
    private String res_no;
    private String res_name;
    private String res_type;
    private int chn_type;
    private int dev_id;
    private String svr_no;
    private int sio_mode;
    private int online_state;

    private Builder() {
    }

    public Builder res_id(int val) {
      res_id = val;
      return this;
    }

    public Builder res_uid(String val) {
      res_uid = val;
      return this;
    }

    public Builder dev_no(String val) {
      dev_no = val;
      return this;
    }

    public Builder ip_intranet(String val) {
      ip_intranet = val;
      return this;
    }

    public Builder port_manage(int val) {
      port_manage = val;
      return this;
    }

    public Builder svr_id(int val) {
      svr_id = val;
      return this;
    }

    public Builder res_no(String val) {
      res_no = val;
      return this;
    }

    public Builder res_name(String val) {
      res_name = val;
      return this;
    }

    public Builder res_type(String val) {
      res_type = val;
      return this;
    }

    public Builder chn_type(int val) {
      chn_type = val;
      return this;
    }

    public Builder dev_id(int val) {
      dev_id = val;
      return this;
    }

    public Builder svr_no(String val) {
      svr_no = val;
      return this;
    }

    public Builder sio_mode(int val) {
      sio_mode = val;
      return this;
    }

    public Builder online_state(int val) {
      online_state = val;
      return this;
    }

    public ResInfo build() {
      return new ResInfo(this);
    }
  }
}
