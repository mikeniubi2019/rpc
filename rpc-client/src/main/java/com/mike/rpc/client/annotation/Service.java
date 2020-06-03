package com.mike.rpc.client.annotation;



import com.mike.rpc.client.core.pojo.FallbackService;

import java.lang.reflect.Method;

public class Service {
    private String[] serviceClassNames;
    private Object instant;
    private Class serviceClass;
    private Method[] methods;
    private String version="v1.0";
    private FallbackService fallbackService;
    private boolean isChache;

    public String[] getServiceClassName() {
        return serviceClassNames;
    }

    public void setServiceClassName(String[] serviceClassNames) {
        this.serviceClassNames = serviceClassNames;
    }

    public Class getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class serviceClass) {
        this.serviceClass = serviceClass;
    }

    public Method[] getMethods() {
        return methods;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getServiceClassNames() {
        return serviceClassNames;
    }

    public void setServiceClassNames(String[] serviceClassNames) {
        this.serviceClassNames = serviceClassNames;
    }

    public Object getInstant() {
        return instant;
    }

    public void setInstant(Object instant) {
        this.instant = instant;
    }

    public FallbackService getFallbackService() {
        return fallbackService;
    }

    public void setFallbackService(FallbackService fallbackService) {
        this.fallbackService = fallbackService;
    }

    public boolean isChache() {
        return isChache;
    }

    public void setChache(boolean chache) {
        isChache = chache;
    }
}
