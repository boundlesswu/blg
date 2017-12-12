package com.vorxsoft.ieye.blg.util;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResUtilImpl implements ResUtil {
  private Connection conn;

  @Override
  public void init(Connection conn) {
    setConn(conn);
  }

  @Override
  public ResInfo getResInfo(int id) {
    return getResinfo(id, "", 1);
  }

  @Override
  public ResInfo getResInfo(String no) {
    return getResinfo(0, no, 2);
  }

  @Override
  public String getResName(int id) {
    ResInfo resInfo = getResinfo(id);
    if (resInfo == null)
      return "";
    return resInfo.getRes_name();
  }

  @Override
  public String getResName(String no) {
    ResInfo resInfo = getResinfo(no);
    if (resInfo == null)
      return "";
    return resInfo.getRes_name();
  }

  @Override
  public String getResNo(int id) {
    ResInfo resInfo = getResinfo(id);
    if (resInfo == null)
      return "";
    return resInfo.getRes_no();
  }

  @Override
  public int getResId(String no) {
    ResInfo resInfo = getResinfo(no);
    if (resInfo == null)
      return 0;
    return resInfo.getRes_id();
  }

  @Override
  public SvrInfo getSvrInfo(int id) {
    return getSvrInfo(id,"",1);
  }

  @Override
  public SvrInfo getSvrInfo(String no) {
    return getSvrInfo(0,no,2);
  }

  @Override
  public String getSvrName(int id) {
    SvrInfo svrInfo = getSvrInfo(id,"",1);
    if(svrInfo == null)
      return "";
    return svrInfo.getSvr_name();
  }

  @Override
  public String getSvrName(String no) {
    SvrInfo svrInfo = getSvrInfo(0,no,2);
    if(svrInfo == null)
      return "";
    return svrInfo.getSvr_name();
  }

  @Override
  public String getMachineName(int id) {
    return null;
  }

  @Override
  public String getMachineName(String no) {
    return null;
  }

  @Override
  public String getDevName(int id) {
    return null;
  }

  @Override
  public String getDevName(String no) {
    return null;
  }

  public String SvrID2SvrName(int id) {
    return "name";
  }

  public String MachineID2MachineName(int id) {
    return "name";
  }

  public String DevID2DevName(int id) {
    return "name";
  }

  private SvrInfo getSvrInfo(int id, String no, int flag) {
		PreparedStatement st = null;
		ResultSet rs = null;
        SvrInfo svrInfo = null;
        String cmd ="";
        if(flag == 1) {
          cmd = "SELECT a.svr_id,a.svr_no,a.svr_name,a.svr_type,a.machine_id,a.enable_state,b.ip_extranet,b.ip_intranet,b.port_extranet,b.port_intranet,A.remark FROM "
                  + "ti_server a  LEFT JOIN ti_server_main_ex b on a.svr_id=b.svr_id WHERE a.svr_id =? ";
        }else if( flag == 2) {
          cmd = "SELECT a.svr_id,a.svr_no,a.svr_name,a.svr_type,a.machine_id,a.enable_state,b.ip_extranet,b.ip_intranet,b.port_extranet,b.port_intranet,a.remark FROM "
                  + "ti_server a  LEFT JOIN ti_server_main_ex b on a.svr_id=b.svr_id WHERE a.svr_no =? ";
        }else{
          return null;
        }
		try {
			st = getConn().prepareStatement(cmd);
			if(flag == 1 ){
			  st.setInt(1, id);
            }else if(flag == 2){
              st.setString(1, no);
            }
			rs = st.executeQuery();
			if (rs.next()) {
				svrInfo = SvrInfo.newBuilder().svr_id(rs.getInt(1)).
                        svr_no(rs.getString(2)).
                        svr_name(rs.getString(3)).
                        svr_type(rs.getString(4)).
                        machine_id(rs.getInt(5)).
                        enable_state(rs.getInt(6)).
                        ip_extranet(rs.getString(7)).
                        ip_intranet(rs.getString(8)).
                        port_extranet(rs.getInt(9)).
                        port_intranet(rs.getInt(10)).
                        remark(rs.getString(11)).build();
			}
		} catch (SQLException e) {
			 e.printStackTrace();
			System.out.println("DBPlatformServer error!"+ e);
		} finally {
				if (st != null) {
                  try {
                    st.close();
                  } catch (SQLException e) {
                    e.printStackTrace();
                  }
                  st = null;
				}
				if (rs != null) {
                  try {
                    rs.close();
                  } catch (SQLException e) {
                    e.printStackTrace();
                  }
                  rs = null;
				}
		}
		return svrInfo;
  }

  private ResInfo getResinfo(int id, String no, int flag) {
    PreparedStatement st = null;
    ResultSet rs = null;
    ResInfo resInfo = null;
    String cmd = "";
    //by id
    if (flag == 1) {
      cmd = "SELECT a.res_id,a.res_no,a.res_uid,a.res_name,a.res_type,b.dev_id,b.dev_no,c.ip_intranet,c.port_manage,c.svr_id, d.svr_no,a.chn_type FROM ti_resource a left join ti_device b "
              + "on a.dev_id=b.dev_id LEFT JOIN ti_server_media_ex c on b.svr_id=c.svr_id LEFT JOIN ti_server d on d.svr_id=c.svr_id WHERE a.res_id =?";
    } else if (flag == 2) {
      cmd = "SELECT a.res_id,a.res_no,a.res_uid,a.res_name,a.res_type,b.dev_id,b.dev_no,c.ip_intranet,c.port_manage,c.svr_id, d.svr_no,a.chn_type FROM ti_resource a left join ti_device b "
              + "on a.dev_id=b.dev_id LEFT JOIN ti_server_media_ex c on b.svr_id=c.svr_id LEFT JOIN ti_server d on d.svr_id=c.svr_id WHERE a.res_no =?";

    } else {
      return null;
    }
    try {
      //conn = connPool.getConnection();
      st = conn.prepareStatement(cmd);
      if (flag == 1) {
        st.setInt(1, id);
      } else if (flag == 2) {
        st.setString(1, no);
      }
      rs = st.executeQuery();
      if (rs.next()) {
        resInfo = ResInfo.newBuilder().res_id(rs.getInt(1)).
                res_no(rs.getString(2)).
                res_uid(rs.getString(3)).
                res_name(rs.getString(4)).
                res_type(rs.getString(5)).
                dev_id(rs.getInt(6)).
                dev_no(rs.getString(7)).
                ip_intranet(rs.getString(8)).
                port_manage(rs.getInt(9)).
                svr_id(rs.getInt(10)).
                svr_no(rs.getString(11)).
                chn_type(rs.getInt(12)).build();
      }
    } catch (SQLException e) {
      //e.printStackTrace();
      System.out.println("DBPlatformServer error!" + e);
    } finally {
      //connPool.returnConnection(conn);
      try {
        if (st != null) {
          st.close();
          st = null;
        }
        if (rs != null) {
          rs.close();
          rs = null;
        }
      } catch (SQLException ignore) {
      }
    }
    return resInfo;
  }

  public ResInfo getResinfo(int id) {
    return getResinfo(id, "", 1);
  }

  public ResInfo getResinfo(String no) {
    return getResinfo(0, no, 2);
  }

  public Connection getConn() {
    return conn;
  }

  public void setConn(Connection conn) {
    this.conn = conn;
  }
}
