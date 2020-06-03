package com.mike.rpc.serve.annotation;



import java.lang.reflect.Method;

public class Service {
    private String[] serviceClassNames;
    private Object instant;
    private Class serviceClass;
    private Method[] methods;
    private String version="v1.0";

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
}
