package com.vorxsoft.ieye.blg.grpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;


public abstract class  GrpcClient {
  private ManagedChannel managedChannel;
  String name;
  private String IP;
  private int PORT = 0;

  public GrpcClient(String name, String IP, int PORT) {
    this.name = name;
    this.IP = IP;
    this.PORT = PORT;
  }
  public GrpcClient(String name, String address) {
    this.name = name;
    String[] a = address.split(":");
    this.IP = a[0];
    this.PORT = Integer.parseInt(a[1]);
  }
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  public GrpcClient(String IP, int PORT) {
    this.IP = IP;
    this.PORT = PORT;
    if(getManagedChannel().isTerminated())
      createChannel();
  }

  public GrpcClient() {
  }

  public void createChannel(){
    managedChannel = NettyChannelBuilder.forAddress(IP,PORT).usePlaintext(true).build();
  }

  public void close(){
    managedChannel.shutdown();
  }
  public ManagedChannel getManagedChannel() {
    return managedChannel;
  }

  public void setManagedChannel(ManagedChannel managedChannel) {
    this.managedChannel = managedChannel;
  }

  public String getIP() {
    return IP;
  }

  public void setIP(String IP) {
    this.IP = IP;
  }

  public int getPORT() {
    return PORT;
  }

  public void setPORT(int PORT) {
    this.PORT = PORT;
  }


}
