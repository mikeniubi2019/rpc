package com.mike.rpc.client.core.pojo;

import java.lang.reflect.Method;

public class FallbackService {
    private Class clazz;
    private Object instant;
    private Method method;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Object getInstant() {
        return instant;
    }

    public void setInstant(Object instant) {
        this.instant = instant;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
