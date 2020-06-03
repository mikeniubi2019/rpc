package com.mike.rpc.client.core.cache;

public class Cache {
    private long outTime;
    private Object result;

    public Cache(long outTime, Object result) {
        this.outTime = outTime;
        this.result = result;
    }

    public Cache(Object result) {
        this.result = result;
        this.outTime=System.currentTimeMillis()+10000;
    }

    public long getOutTime() {
        return outTime;
    }

    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }

    public Object getResult() {
        return outTime>System.currentTimeMillis()?result:null;
    }

    public void setResult(Object result) {
        this.result = result;
        this.outTime=System.currentTimeMillis()+10000;
    }
}
