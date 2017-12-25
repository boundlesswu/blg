package com.vorxsoft.ieye.blg.grpc;

import com.vorxsoft.ieye.blg.util.TimeUtil;
import com.vorxsoft.ieye.proto.VSLogLevel;
import com.vorxsoft.ieye.proto.VSLogRequest;
import com.vorxsoft.ieye.proto.VSLogResponse;
import com.vorxsoft.ieye.proto.VSLogServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.lang.management.ManagementFactory;

import static com.vorxsoft.ieye.proto.VSLogType.VSLogTypeOther;

public class LogServiceClient extends GrpcClient{
  private VSLogServiceGrpc.VSLogServiceStub stub;
  private String  hostNameIp ;
  private int  pId;
  private String pName;

  public int obtainPid(){
    String name = ManagementFactory.getRuntimeMXBean().getName();
    int  pid = Integer.parseInt(name.split("@")[0]);
    return pid;
  }

  public String getHostNameIp() {
    return hostNameIp;
  }

  public void setHostNameIp(String hostNameIp) {
    this.hostNameIp = hostNameIp;
  }

  public int getpId() {
    return pId;
  }

  public void setpId(int pId) {
    this.pId = pId;
  }

  public String getpName() {
    return pName;
  }

  public void setpName(String pName) {
    this.pName = pName;
  }

  public VSLogServiceGrpc.VSLogServiceStub getStub() {
    return stub;
  }

  public void setStub(VSLogServiceGrpc.VSLogServiceStub stub) {
    this.stub = stub;
  }

  public LogServiceClient(String name, String IP, int PORT) {
    super(name, IP, PORT);
    if(getStub() == null){
      stub =  VSLogServiceGrpc.newStub(getManagedChannel());
    }
    this.pId = obtainPid();
  }

  public LogServiceClient(String name, String address) {
    super(name, address);
    if(getStub() == null){
      stub =  VSLogServiceGrpc.newStub(getManagedChannel());
    }
    this.pId = obtainPid();
  }
  public LogServiceClient(String IP, int PORT) {
    super(IP, PORT);
    this.pId = obtainPid();
  }


  public LogServiceClient() {
    this.pId = obtainPid();
  }

  public void init(){
    createChannel();
    if(getManagedChannel().isTerminated()){
      stub =  VSLogServiceGrpc.newStub(getManagedChannel());
    }
  }

  public void sentVSLog(VSLogRequest request){
    StreamObserver<VSLogResponse> responseObserver = new StreamObserver<VSLogResponse>(){

      @Override
      public void onNext(VSLogResponse vsLogResponse) {
        if (vsLogResponse.getResult() == 0) {
          return;
        }
      }

      @Override
      public void onError(Throwable throwable) {
        System.out.println("send log cmd  onError"+throwable);
      }

      @Override
      public void onCompleted() {
        // Won't be called, since the server in this example always
        // fails.
      }
    };
    getStub().sentVSLog(request,responseObserver);
  }


  public void sentVSLog(String  logContent,VSLogLevel level){
    StreamObserver<VSLogResponse> responseObserver = new StreamObserver<VSLogResponse>(){

      @Override
      public void onNext(VSLogResponse vsLogResponse) {
        if (vsLogResponse.getResult() == 0) {
          return;
        }
      }

      @Override
      public void onError(Throwable throwable) {
        System.out.println("send log cmd  onError"+throwable);
      }

      @Override
      public void onCompleted() {
        // Won't be called, since the server in this example always
        // fails.
      }
    };
    VSLogRequest request = VSLogRequest.newBuilder().
                           setDateTimeMs(TimeUtil.getDateTimeNs()).
                           setBusinessId("0").
                           setHostNameIp(getHostNameIp()).
                           setPId(getpId()).
                           setPName(getpName()).
                           setLogType(VSLogTypeOther).
                           setLogLevel(level).
                           setLogContent(logContent).build();
    getStub().sentVSLog(request,responseObserver);
  }
}
