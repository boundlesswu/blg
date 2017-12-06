package com.vorxsoft.ieye.blg.grpc;

import com.vorxsoft.ieye.proto.*;
import io.grpc.stub.StreamObserver;

public class VsIeyeClient extends GrpcClient {
  private VsIeyeProtoGrpc.VsIeyeProtoStub stub;

  public VsIeyeClient(String IP, int PORT) {
    super(IP, PORT);
  }

  public VsIeyeClient() {
  }

  public VsIeyeClient(String name, String IP, int PORT) {
    super(name, IP, PORT);
  }

  public VsIeyeClient(String name, String address) {
    super(name, address);
  }

  public void init() {
    createChannel();
    stub = VsIeyeProtoGrpc.newStub(getManagedChannel());
  }

  public void reportEvent(ReportEventRequest request) {
    StreamObserver<DefaultReply> responseObserver = new StreamObserver<DefaultReply>() {
      @Override
      public void onNext(DefaultReply value) {
        if (value.getResult() != 0) {
          return;
        }
      }

      @Override
      public void onError(Throwable t) {
        System.out.println("report event onError" + t);
      }

      @Override
      public void onCompleted() {
        // Won't be called, since the server in this example always
        // fails.
      }
    };
    stub.reportEvent(request, responseObserver);
  }

  public void reportLinkage(ReportLinkageRequest request) {
    StreamObserver<DefaultReply> responseObserver = new StreamObserver<DefaultReply>() {
      @Override
      public void onNext(DefaultReply value) {
        if (value.getResult() != 0) {
          return;
        }
      }

      @Override
      public void onError(Throwable t) {
        System.out.println("report linkage onError" + t);
      }

      @Override
      public void onCompleted() {
        // Won't be called, since the server in this example always
        // fails.
      }
    };
    stub.reportLinkage(request, responseObserver);
  }

  //切换上墙	linkage_wall
  public void PlayLiveScreen(LiveScreenRequest request) {
    StreamObserver<LiveScreenReply> responseObserver = new StreamObserver<LiveScreenReply>() {
      @Override
      public void onNext(LiveScreenReply value) {
        if (value.getResult() != 0) {
          return;
        }
      }

      @Override
      public void onError(Throwable t) {
        System.out.println("playLiveScreenResult onError" + t);
      }

      @Override
      public void onCompleted() {
        // Won't be called, since the server in this example always
        // fails.
      }
    };
    stub.playLiveScreen(request, responseObserver);
  }

  //云台联动-调用预置点	linkage_preset
  //PTZPreset (PTZPresetRequest) returns (PTZPresetReply)预置位设置
  public void pTZPreset(PTZPresetRequest request){
    StreamObserver<PTZPresetReply> responseObserver = new StreamObserver<PTZPresetReply>() {
      @Override
      public void onNext(PTZPresetReply ptzPresetReply) {
        if (ptzPresetReply.getResult() != 0) {
          return;
        }
      }

      @Override
      public void onError(Throwable throwable) {
        System.out.println("PTZPreset onError" + throwable);
      }

      @Override
      public void onCompleted() {

      }
    };
    stub.pTZPreset(request,responseObserver);
  }
  //云台联动-调用巡航	linkage_cruise
  // PTZCruise (PTZCruiseRequest) returns (PTZCruiseReply)
  public void pTZCruise(PTZCruiseRequest request){
    StreamObserver<PTZCruiseReply> streamObserver = new StreamObserver<PTZCruiseReply>() {
      @Override
      public void onNext(PTZCruiseReply ptzCruiseReply) {
        if (ptzCruiseReply.getResult() != 0) {
          return;
        }
      }

      @Override
      public void onError(Throwable throwable) {
        System.out.println("PTZCruise onError" + throwable);
      }

      @Override
      public void onCompleted() {

      }
    };
    stub.pTZCruise(request,streamObserver);
  }

  //报警输出	linkage_sio			可设置多条
  //AlarmControl (AlarmControlRequest) returns (DefaultReply)
  public void alarmControl(AlarmControlRequest request){
    StreamObserver<DefaultReply> streamObserver = new StreamObserver<DefaultReply>() {
      @Override
      public void onNext(DefaultReply defaultReply) {
        if (defaultReply.getResult() != 0) {
          return;
        }
      }

      @Override
      public void onError(Throwable throwable) {
        System.out.println("alarmControl onError" + throwable);
      }

      @Override
      public void onCompleted() {

      }
    };
    stub.alarmControl(request,streamObserver);
  }

}
