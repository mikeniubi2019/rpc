package com.mike.rpc.service;

import com.mike.rpc.client.annotation.Fallback;
import com.mike.rpc.client.annotation.ServiceProvicer;
import com.mike.rpc.client.annotation.ServiceProx;
import com.mike.rpc.test.service.TestService;

@ServiceProvicer()
public class TestServiceProx {

    @ServiceProx(beanName = "TestService")
    public TestService testService;

    public void sayHi(){
        testService.sayHi();
    }

    public String getMessgae(String message){
        return testService.getMessgae(message);
    }

    @Fallback
    public void back(){
        System.out.println("回退方法执行了");
    }
}
