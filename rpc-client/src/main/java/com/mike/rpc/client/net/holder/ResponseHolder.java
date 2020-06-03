package com.mike.rpc.client.net.holder;

import com.mike.rpc.api.net.pojo.RpcResponse;

public class ResponseHolder extends RpcResponse implements Holder{
    private boolean success=false;
    private byte[] lock = new byte[1];
    private byte tryCount=0;
    private long timeOut;

    public ResponseHolder() {
        this.timeOut=System.currentTimeMillis()+3000;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public Object get() {
            long nowTime = System.currentTimeMillis();
            while (!isSuccess() && this.timeOut>nowTime){
                synchronized (lock){
                if (isSuccess()) {
                    lock.notifyAll();
                    return getResult();
                }
                    try {
                        lock.wait(this.timeOut-nowTime);
                    } catch (InterruptedException e) {
                        lock.notifyAll();
                        e.printStackTrace();
                        return null;
                    }
                    lock.notifyAll();
                    nowTime = System.currentTimeMillis();
            }
        }
        return getResult();
    }

    @Override
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public void setMessage(String message){
        super.setMessage(message);
        synchronized (lock){
            this.timeOut=this.timeOut-3000;
            lock.notifyAll();
        }
    }

    public void setResult(Object o){
        synchronized (lock){
            super.setResult(o);
            setSuccess(true);
            lock.notifyAll();
        }
    }

    public byte getTryCount() {
        return tryCount;
    }

    public void setTryCount(byte tryCount) {
        this.tryCount = tryCount;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
}
