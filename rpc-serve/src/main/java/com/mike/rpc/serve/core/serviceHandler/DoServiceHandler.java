package com.mike.rpc.serve.core.serviceHandler;

import com.lmax.disruptor.WorkHandler;
import com.mike.rpc.api.net.pojo.RpcRequest;
import com.mike.rpc.api.net.pojo.RpcResponse;
import com.mike.rpc.serve.annotation.Service;
import com.mike.rpc.serve.core.pojo.RpcRequestHandlerContext;
import com.mike.rpc.serve.utils.ContexUtils;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DoServiceHandler implements WorkHandler<RpcRequestHandlerContext> {

    @Override
    public void onEvent(RpcRequestHandlerContext rpcRequestHandlerContext) throws Exception {
        ChannelHandlerContext channelHandlerContext = rpcRequestHandlerContext.getChannelHandlerContext();
        RpcRequest rpcRequest = rpcRequestHandlerContext.getT();

        Object result = doInvoke(rpcRequest);
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setId(rpcRequest.getId());
        rpcResponse.setSuccess(true);
        rpcResponse.setResult(result);
        channelHandlerContext.writeAndFlush(rpcResponse);
    }

    private Object doInvoke(RpcRequest rpcRequest) {
        String methondName = rpcRequest.getMethondName();
        String serviceName = rpcRequest.getServiceName();
        String version = rpcRequest.getVersion();
        Object[] params = rpcRequest.getParams();
        Service service = ContexUtils.getCurrentContex().getServices(serviceName+version);
        for (Method method : service.getMethods()){
            if (method.getName().equals(methondName)){
                try {
                    if (params==null||params.length==0){
                        return method.invoke(service.getInstant());
                    } else {
                        return method.invoke(service.getInstant(),params);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
