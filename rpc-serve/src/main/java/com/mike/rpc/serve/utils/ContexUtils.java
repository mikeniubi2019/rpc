package com.mike.rpc.serve.utils;

import com.mike.rpc.serve.contex.Contex;

public class ContexUtils {
    private static Contex contex;
    public synchronized void setContex(Contex contex){
        this.contex=contex;
    }
    public static Contex getCurrentContex(){
        return contex;
    }
}
