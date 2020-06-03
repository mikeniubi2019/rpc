package com.mike.rpc.api.asynchronize;

public class ExceptionHandler implements com.lmax.disruptor.ExceptionHandler {
    @Override
    public void handleEventException(Throwable throwable, long l, Object o) {
        System.out.println(throwable.getMessage());
    }

    @Override
    public void handleOnStartException(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }

    @Override
    public void handleOnShutdownException(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }
}
