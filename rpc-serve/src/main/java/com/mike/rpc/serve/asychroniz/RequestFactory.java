package com.mike.rpc.serve.asychroniz;

import com.mike.rpc.api.asynchronize.ReceiveObjectFactory;
import com.mike.rpc.serve.core.pojo.RpcRequestHandlerContext;

public class RequestFactory implements ReceiveObjectFactory {
    @Override
    public Object getObject() {
        return new RpcRequestHandlerContext(null,null);
    }
}
