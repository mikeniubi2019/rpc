package com.mike.rpc.api.asynchronize;

import com.lmax.disruptor.WorkHandler;

import java.util.List;

public interface AsychronizeHandler<T> {
    void regeistInvokeHandler(WorkHandler<T> workHandler);
    List<WorkHandler> getWorkHandler();
    void onData(T t);
    void start();
}
