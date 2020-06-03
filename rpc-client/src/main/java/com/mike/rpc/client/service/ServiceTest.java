package com.mike.rpc.client.service;

import com.mike.rpc.client.annotation.ServiceProvicer;

@ServiceProvicer()
public class ServiceTest {
    public void sayHello(String s,int i){
        System.out.println("hello world! v1.0"+s+i);
    }
}
