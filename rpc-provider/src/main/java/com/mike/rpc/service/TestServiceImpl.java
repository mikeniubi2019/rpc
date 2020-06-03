package com.mike.rpc.service;

import com.mike.rpc.serve.annotation.ServiceProvicer;
import com.mike.rpc.test.service.TestService;

@ServiceProvicer(beanName = "TestService")
public class TestServiceImpl implements TestService {

    public void sayHi() {
        System.out.println("sayHello v1.0");
    }

    public String getMessgae(String message) {
        System.out.println("测试getMessgae:"+message);
        return message+":serve:return:1.0";
    }
}
