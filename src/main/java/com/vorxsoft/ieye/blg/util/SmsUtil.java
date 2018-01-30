package com.vorxsoft.ieye.blg.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @Author boundlesswu
 * @Description
 *  * 短信API产品的DEMO程序,工程中包含了一个SmsDemo类，直接通过
 * 执行main函数即可体验短信产品API功能(只需要将AK替换成开通了云通信-短信产品功能的AK即可)
 * 工程依赖了2个jar包(存放在工程的libs目录下)
 * 1:aliyun-java-sdk-core.jar
 * 2:aliyun-java-sdk-dysmsapi.jar
 * <p>
 * 备注:Demo工程编码采用UTF-8
 * 国际短信发送请勿参照此DEMO
 * @Date 2018-01-25 17:38
 **/
public class SmsUtil {
  //产品名称:云通信短信API产品,开发者无需替换
  private String product = "Dysmsapi";
  //产品域名,开发者无需替换
  private String domain = "dysmsapi.aliyuncs.com";
  // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
  private String accessKeyId = "LTAIEctvEq6hfNqb";
  private String accessKeySecret = "o49v0PVHSIt5ZMCqGHPKQ1dDMJZnfS";
  private String  signName = "蛙视";
  private String templateCode = "SMS_123735642";

  public String getProduct() {
    return product;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getAccessKeyId() {
    return accessKeyId;
  }

  public void setAccessKeyId(String accessKeyId) {
    this.accessKeyId = accessKeyId;
  }

  public String getAccessKeySecret() {
    return accessKeySecret;
  }

  public void setAccessKeySecret(String accessKeySecret) {
    this.accessKeySecret = accessKeySecret;
  }

  public String getSignName() {
    return signName;
  }

  public void setSignName(String signName) {
    this.signName = signName;
  }

  public String getTemplateCode() {
    return templateCode;
  }

  public void setTemplateCode(String templateCode) {
    this.templateCode = templateCode;
  }

  public SmsUtil() {
  }
  public SmsUtil(  String product , String domain,
                 String accessKeyId, String accessKeySecret,
                 String  signName , String templateCode) {
    setProduct(product);
    setDomain(domain);
    setAccessKeyId(accessKeyId);
    setAccessKeySecret(accessKeySecret);
    setSignName(signName);
    setTemplateCode(templateCode);
  }

  public SendSmsResponse sendSms(String phoneNum, String happentime, String name, String desc) throws ClientException {
    //可自助调整超时时间
    System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
    System.setProperty("sun.net.client.defaultReadTimeout", "10000");
    //初始化acsClient,暂不支持region化
    IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
    IAcsClient acsClient = new DefaultAcsClient(profile);

    //组装请求对象-具体描述见控制台-文档部分内容
    SendSmsRequest request = new SendSmsRequest();
    //必填:待发送手机号
    request.setPhoneNumbers(phoneNum);
    //必填:短信签名-可在短信控制台中找到
    request.setSignName(signName);
    //必填:短信模板-可在短信控制台中找到
    request.setTemplateCode(templateCode);

    String stime;
    String sname;
    String sdesc;

    if(happentime.length() > 20){
      stime = happentime.substring(0,20);
    }else{
      stime = happentime;
    }
    if(name.length() > 20){
      sname = name.substring(0,20);
    }else{
      sname = name;
    }
    if(desc.length() > 20){
      sdesc = desc.substring(0,20);
    }else{
      sdesc = desc;
    }

    //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
    String smsContent = "{\"happenTime\":"+ "\"" + stime +"\"" +"," +
                          "\"name\":"     + "\"" + sname + "\"" +","+
                          "\"desc\":"     + "\"" + sdesc + "\"" +"}";
//    String smsContent = "{\"happenTime\":"+ "\"" + "1" +"\"" +"," +
//            "\"name\":"     + "\"" + name      + "\"" +","+
//            "\"desc\":"     + "\"" + desc      + "\"" +"}";
    request.setTemplateParam(smsContent);

    //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
    //request.setSmsUpExtendCode("90997");

    //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
    //request.setOutId("yourOutId");

    //hint 此处可能会抛出异常，注意catch
    SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

    return sendSmsResponse;
  }

  public SendSmsResponse sendSms() throws ClientException {

    //可自助调整超时时间
    System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
    System.setProperty("sun.net.client.defaultReadTimeout", "10000");

    //初始化acsClient,暂不支持region化
    IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
    IAcsClient acsClient = new DefaultAcsClient(profile);

    //组装请求对象-具体描述见控制台-文档部分内容
    SendSmsRequest request = new SendSmsRequest();
    //必填:待发送手机号
    request.setPhoneNumbers("13811545478");
    //必填:短信签名-可在短信控制台中找到
    request.setSignName("蛙视");
    //必填:短信模板-可在短信控制台中找到
    request.setTemplateCode("SMS_103285006");
    //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
    request.setTemplateParam("{\"faultno\":\"Tom\", \"state\":\"123\",\"time\":\"2345\"}");

    //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
    //request.setSmsUpExtendCode("90997");

    //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
    request.setOutId("yourOutId");

    //hint 此处可能会抛出异常，注意catch
    SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

    return sendSmsResponse;
  }


  public  QuerySendDetailsResponse querySendDetails(String bizId) throws ClientException {

    //可自助调整超时时间
    System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
    System.setProperty("sun.net.client.defaultReadTimeout", "10000");

    //初始化acsClient,暂不支持region化
    IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
    IAcsClient acsClient = new DefaultAcsClient(profile);

    //组装请求对象
    QuerySendDetailsRequest request = new QuerySendDetailsRequest();
    //必填-号码
    request.setPhoneNumber("13811545478");
    //可选-流水号
    request.setBizId(bizId);
    //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
    SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
    request.setSendDate(ft.format(new Date()));
    //必填-页大小
    request.setPageSize(10L);
    //必填-当前页码从1开始计数
    request.setCurrentPage(1L);

    //hint 此处可能会抛出异常，注意catch
    QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);

    return querySendDetailsResponse;
  }

  public static void main(String[] args) throws ClientException, InterruptedException {

    //发短信
    //SendSmsResponse response = sendSms();
    //String phoneNum = "13641095392,13811545478";
    String phoneNum = "13811545478,18611813097";
    String happentime = "2017-01-30";
    String name="人士";
    String desc ="test";
    SmsUtil smsUtil = new SmsUtil();
    SendSmsResponse response = smsUtil.sendSms(phoneNum,happentime,name,desc);
    System.out.println("短信接口返回的数据----------------");
    System.out.println("Code=" + response.getCode());
    System.out.println("Message=" + response.getMessage());
    System.out.println("RequestId=" + response.getRequestId());
    System.out.println("BizId=" + response.getBizId());

    Thread.sleep(3000L);

    //查明细
    if (response.getCode() != null && response.getCode().equals("OK")) {
      QuerySendDetailsResponse querySendDetailsResponse = smsUtil.querySendDetails(response.getBizId());
      System.out.println("短信明细查询接口返回数据----------------");
      System.out.println("Code=" + querySendDetailsResponse.getCode());
      System.out.println("Message=" + querySendDetailsResponse.getMessage());
      int i = 0;
      for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse.getSmsSendDetailDTOs()) {
        System.out.println("SmsSendDetailDTO[" + i + "]:");
        System.out.println("Content=" + smsSendDetailDTO.getContent());
        System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
        System.out.println("OutId=" + smsSendDetailDTO.getOutId());
        System.out.println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
        System.out.println("ReceiveDate=" + smsSendDetailDTO.getReceiveDate());
        System.out.println("SendDate=" + smsSendDetailDTO.getSendDate());
        System.out.println("SendStatus=" + smsSendDetailDTO.getSendStatus());
        System.out.println("Template=" + smsSendDetailDTO.getTemplateCode());
      }
      System.out.println("TotalCount=" + querySendDetailsResponse.getTotalCount());
      System.out.println("RequestId=" + querySendDetailsResponse.getRequestId());
    }

  }
}
