package com.mike.rpc.client.net.pojo;

import com.mike.rpc.client.net.starter.ClientBootstrat;
import io.netty.channel.Channel;

import java.util.Objects;

public class IpChannelHolder {
    private String addr;
    private int port;
    private Channel channel;
    private ClientBootstrat bootstrat;
    private int weight=1;
    private int connectCount=0;
    private int successCount=0;
    private int failCount=0;
    private volatile boolean isFallback=false;
    private long backTime;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getConnectCount() {
        return connectCount;
    }

    public void setConnectCount(int connectCount) {
        this.connectCount = connectCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public ClientBootstrat getBootstrat() {
        return bootstrat;
    }

    public void setBootstrat(ClientBootstrat bootstrat) {
        this.bootstrat = bootstrat;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isFallback() {
        synchronized (this){
            if (isFallback && backTime<System.currentTimeMillis()){
                isFallback = false;
            }
            notifyAll();
        }
        return isFallback;
    }

    public void setFallback(boolean fallback) {
        if (fallback){
            backTime=System.currentTimeMillis()+1000*60*5;
        }
        isFallback = fallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpChannelHolder that = (IpChannelHolder) o;
        return port == that.port &&
                Objects.equals(addr, that.addr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addr, port);
    }

    public long getBackTime() {
        return backTime;
    }

    public void setBackTime(long backTime) {
        this.backTime = backTime;
    }
}
