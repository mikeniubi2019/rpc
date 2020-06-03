package com.mike.rpc.client.utils;

import com.mike.rpc.client.contex.Contex;

public class ContexUtils {
    private static Contex contex;
    public synchronized void setContex(Contex contex){
        this.contex=contex;
    }
    public static Contex getCurrentContex(){
        return contex;
    }
}
