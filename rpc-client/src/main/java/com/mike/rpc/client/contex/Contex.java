package com.mike.rpc.client.contex;

import com.mike.rpc.client.annotation.Service;
import io.netty.channel.ChannelInitializer;

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
