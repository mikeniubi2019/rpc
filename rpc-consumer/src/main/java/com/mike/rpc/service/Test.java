package com.mike.rpc.service;

import com.mike.rpc.client.annotation.Service;
import com.mike.rpc.client.contex.AnnotationContex;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

    public static void main(String[] args) throws Exception {
        AnnotationContex annotationContex = new AnnotationContex("application.properties");
        Service service = annotationContex.getServices("TestServicev1.0");
        TestServiceProx testService = (TestServiceProx)service.getInstant();
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i=0;i<=10000000;i++){
            int finalI = i;
            if (i%1000000==0) Thread.sleep(5000);
            executorService.submit(()->{
                testService.sayHi();
                testService.getMessgae("1111");
                //System.out.println("最外层返回参数为"+finalI);
                if (finalI%100000 ==0){
                    System.out.println(finalI+"-------------------------");
                    System.out.println(System.currentTimeMillis()-startTime);
                }
            });
        }
    }
}
