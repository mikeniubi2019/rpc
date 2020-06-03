package com.mike.rpc.serve.test;

import com.mike.rpc.serve.annotation.Service;
import com.mike.rpc.serve.contex.AnnotationContex;


public class serveTest {
    public static void main(String[] args) throws Exception {
        AnnotationContex annotationContex = new AnnotationContex("com.mike.rpc.serve");
        Service service = annotationContex.getServices("ServiceTestv1.0");

    }
}
