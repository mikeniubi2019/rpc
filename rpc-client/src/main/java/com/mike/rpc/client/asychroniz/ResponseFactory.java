package com.mike.rpc.client.asychroniz;

import com.mike.rpc.api.asynchronize.ReceiveObjectFactory;
import com.mike.rpc.client.core.pojo.RpcResponseHandlerContext;


public class ResponseFactory implements ReceiveObjectFactory {
    @Override
    public Object getObject() {
        return new RpcResponseHandlerContext(null,null);
    }
}
