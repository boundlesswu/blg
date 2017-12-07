package com.vorxsoft.ieye.blg;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.vorxsoft.ieye.blg.process.LinkageItem;
import com.vorxsoft.ieye.proto.*;
import io.grpc.stub.StreamObserver;
import redis.clients.jedis.Jedis;

import java.util.HashMap;

public class BLGServer extends VsIeyeProtoGrpc.VsIeyeProtoImplBase {
  private Jedis jedis;
  HashMap<String, LinkageItem> linkageItemHashMap;

  BLGServer(String ip, int port) {
    jedis = new Jedis(ip, port);
  }

  public Jedis getJedis() {
    return jedis;
  }

  public void setJedis(Jedis jedis) {
    this.jedis = jedis;
  }

  public HashMap<String, LinkageItem> getLinkageItemHashMap() {
    return linkageItemHashMap;
  }

  public void setLinkageItemHashMap(HashMap<String, LinkageItem> linkageItemHashMap) {
    this.linkageItemHashMap = linkageItemHashMap;
  }

  public void updateExecResult(LiveScreenReply req) {
    LinkageItem linkageItem = getLinkageItemHashMap().get(req.getSBusinessID());
    if (linkageItem != null) {
      linkageItem.setExecResult(req.getResult());
    }
  }

  public void updateExecResult(PTZPresetReply req) {
    LinkageItem linkageItem = getLinkageItemHashMap().get(req.getSBusinessID());
    if (linkageItem != null) {
      linkageItem.setExecResult(req.getResult());
    }
  }

  public void updateExecResult(PTZCruiseReply req) {
    LinkageItem linkageItem = getLinkageItemHashMap().get(req.getSBusinessID());
    if (linkageItem != null) {
      linkageItem.setExecResult(req.getResult());
    }
  }

  //rpc PlayLiveScreenResult (LiveScreenReply) returns (DefaultReply)
  @Override
  public void playLiveScreenResult(LiveScreenReply req, StreamObserver<DefaultReply> reply) {
    System.out.println("receiver" + req);
    updateExecResult(req);
    DefaultReply defaultReply = DefaultReply.newBuilder().setResult(1).setSBusinessID(req.getSBusinessID()).build();
    reply.onNext(defaultReply);
    reply.onCompleted();
  }

  //rpc PTZPresetResult (PTZPresetReply) returns (DefaultReply)
  @Override
  public void pTZPresetResult(PTZPresetReply req, StreamObserver<DefaultReply> reply) {
    System.out.println("receiver" + req);
    updateExecResult(req);
    DefaultReply defaultReply = DefaultReply.newBuilder().setResult(1).setSBusinessID(req.getSBusinessID()).build();
    reply.onNext(defaultReply);
    reply.onCompleted();
  }

  //rpc PTZCruiseResult (PTZCruiseReply) returns (DefaultReply)
  @Override
  public void pTZCruiseResult(PTZCruiseReply req, StreamObserver<DefaultReply> reply) {
    System.out.println("receiver" + req);
    updateExecResult(req);
    DefaultReply defaultReply = DefaultReply.newBuilder().setResult(1).setSBusinessID(req.getSBusinessID()).build();
    reply.onNext(defaultReply);
    reply.onCompleted();
  }

  //rpc ReportLinkage (ReportLinkageRequest) returns (DefaultReply)
  @Override
  public void reportLinkage(ReportLinkageRequest req, StreamObserver<DefaultReply> reply) {
    System.out.println("receiver" + req);
    if (req.getEventWithLinkagesCount() > 0) {
      String s = null;
      try {
        s = JsonFormat.printer().print(req.toBuilder());
        //s = JsonFormat.printToString(req);
      } catch (InvalidProtocolBufferException e) {
        e.printStackTrace();
      }
      //System.out.println("s");
      jedis.hset("eventWithLinkage_" + String.valueOf(System.currentTimeMillis()), "req", s);
    }
//    for (int i = 0; i < req.getEventWithLinkagesCount(); i++) {
//      EventWithLinkage eventWithLinkage = req.getEventWithLinkages(i);
//      Events event = eventWithLinkage.getEvent();
//      for (int j = 0; j < eventWithLinkage.getLinkagesCount(); j++) {
//        Linkage linkage = eventWithLinkage.getLinkages(i);
//
//      }
//    }
    DefaultReply defaultReply = DefaultReply.newBuilder().setResult(1).setSBusinessID("1212121").build();
    reply.onNext(defaultReply);
    reply.onCompleted();

  }
}
