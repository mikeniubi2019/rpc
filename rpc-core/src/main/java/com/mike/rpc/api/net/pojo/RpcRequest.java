package com.mike.rpc.api.net.pojo;

import java.io.Serializable;

public class RpcRequest implements Serializable,Cloneable{
    private String id;
    private Object[] params;
    private String methondName;
    private String serviceName;
    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getMethondName() {
        return methondName;
    }

    public void setMethondName(String methondName) {
        this.methondName = methondName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
