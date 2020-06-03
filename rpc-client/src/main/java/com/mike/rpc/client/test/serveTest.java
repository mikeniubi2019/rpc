package com.mike.rpc.client.test;

import com.mike.rpc.client.contex.AnnotationContex;
import com.mike.rpc.client.annotation.Service;


public class serveTest {
    public static void main(String[] args) throws Exception {
        AnnotationContex annotationContex = new AnnotationContex("com.mike.rpc.serve");
        Service service = annotationContex.getServices("ServiceTestv1.0");

    }
}
