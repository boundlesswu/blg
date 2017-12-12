package com.vorxsoft.ieye.blg.util;

public class SvrInfo {
	private int svr_id;
	private String svr_no;
	private String svr_name;
	private String svr_type;
	private int machine_id;
	private int enable_state;
	private String remark;
	private String ip_extranet;
	private String ip_intranet;

	public int getPort_extranet() {
		return port_extranet;
	}

	public void setPort_extranet(int port_extranet) {
		this.port_extranet = port_extranet;
	}

	public int getPort_intranet() {
		return port_intranet;
	}

	public void setPort_intranet(int port_intranet) {
		this.port_intranet = port_intranet;
	}

	private int port_extranet;
	private int port_intranet;

	public SvrInfo(int svr_id, String svr_no, String svr_name, String svr_type, int machine_id, int enable_state,
                   String remark, String ip_extranet, String ip_intranet, int port_extranet, int port_intranet) {
		super();
		this.svr_id = svr_id;
		this.svr_no = svr_no;
		this.svr_name = svr_name;
		this.svr_type = svr_type;
		this.machine_id = machine_id;
		this.enable_state = enable_state;
		this.remark = remark;
		this.ip_extranet = ip_extranet;
		this.ip_intranet = ip_intranet;
		this.port_extranet = port_extranet;
		this.port_intranet = port_intranet;
	}

	public SvrInfo() {
		// TODO Auto-generated constructor stub
	}

	private SvrInfo(Builder builder) {
		setSvr_id(builder.svr_id);
		setSvr_no(builder.svr_no);
		setSvr_name(builder.svr_name);
		setSvr_type(builder.svr_type);
		setMachine_id(builder.machine_id);
		setEnable_state(builder.enable_state);
		setRemark(builder.remark);
		setIp_extranet(builder.ip_extranet);
		setIp_intranet(builder.ip_intranet);
		setPort_extranet(builder.port_extranet);
		setPort_intranet(builder.port_intranet);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public int getSvr_id() {
		return svr_id;
	}
	public void setSvr_id(int svr_id) {
		this.svr_id = svr_id;
	}
	public String getSvr_no() {
		return svr_no;
	}
	public void setSvr_no(String svr_no) {
		this.svr_no = svr_no;
	}
	public String getSvr_name() {
		return svr_name;
	}
	public void setSvr_name(String svr_name) {
		this.svr_name = svr_name;
	}
	public String getSvr_type() {
		return svr_type;
	}
	public void setSvr_type(String svr_type) {
		this.svr_type = svr_type;
	}
	public int getMachine_id() {
		return machine_id;
	}
	public void setMachine_id(int machine_id) {
		this.machine_id = machine_id;
	}
	public int getEnable_state() {
		return enable_state;
	}
	public void setEnable_state(int enable_state) {
		this.enable_state = enable_state;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getIp_extranet() {
		return ip_extranet;
	}

	public void setIp_extranet(String ip_extranet) {
		this.ip_extranet = ip_extranet;
	}

	public String getIp_intranet() {
		if(ip_extranet!=null){
			return ip_extranet;
		}
		return ip_intranet;
	}

	public void setIp_intranet(String ip_intranet) {
		this.ip_intranet = ip_intranet;
	}


	public static final class Builder {
		private int svr_id;
		private String svr_no;
		private String svr_name;
		private String svr_type;
		private int machine_id;
		private int enable_state;
		private String remark;
		private String ip_extranet;
		private String ip_intranet;
		private int port_extranet;
		private int port_intranet;
		private int port_manage;

		private Builder() {
		}

		public Builder svr_id(int val) {
			svr_id = val;
			return this;
		}

		public Builder svr_no(String val) {
			svr_no = val;
			return this;
		}

		public Builder svr_name(String val) {
			svr_name = val;
			return this;
		}

		public Builder svr_type(String val) {
			svr_type = val;
			return this;
		}

		public Builder machine_id(int val) {
			machine_id = val;
			return this;
		}

		public Builder enable_state(int val) {
			enable_state = val;
			return this;
		}

		public Builder remark(String val) {
			remark = val;
			return this;
		}

		public Builder ip_extranet(String val) {
			ip_extranet = val;
			return this;
		}

		public Builder ip_intranet(String val) {
			ip_intranet = val;
			return this;
		}

		public Builder port_extranet(int val) {
			port_extranet = val;
			return this;
		}

		public Builder port_intranet(int val) {
			port_intranet = val;
			return this;
		}

		public Builder port_manage(int val) {
			port_manage = val;
			return this;
		}

		public SvrInfo build() {
			return new SvrInfo(this);
		}
	}
}
