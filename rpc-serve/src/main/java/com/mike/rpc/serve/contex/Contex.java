package com.mike.rpc.serve.contex;

import com.mike.rpc.serve.annotation.Service;

public interface Contex {
    Service getServices(String serviceName);
    Service getServices(Class type);

    void setLocation(String path);

    void reflesh() throws Exception;

    void registService(Service service);

    void scan();

    void doRegistChannelHandler();

    void PutProperties() throws Exception;
}
