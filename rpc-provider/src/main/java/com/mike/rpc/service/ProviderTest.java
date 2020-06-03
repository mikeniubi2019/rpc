package com.mike.rpc.service;

import com.mike.rpc.serve.contex.AnnotationContex;

public class ProviderTest {
    public static void main(String[] args) throws Exception {
        AnnotationContex annotationContex = new AnnotationContex("application.properties");
        System.out.println(annotationContex);
    }
}
