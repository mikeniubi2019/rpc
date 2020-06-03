package com.mike.rpc.serve.service;

import com.mike.rpc.serve.annotation.ServiceProvicer;

@ServiceProvicer(beanName = "ServiceTest",version = "v1.0")
public class ServiceTest {
    public void sayHello(String s,int i){
        System.out.println("hello world! v1.0"+s+i);
    }
}
