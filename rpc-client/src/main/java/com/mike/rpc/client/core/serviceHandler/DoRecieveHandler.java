package com.mike.rpc.client.core.serviceHandler;

import com.lmax.disruptor.WorkHandler;
import com.mike.rpc.api.net.pojo.RpcResponse;
import com.mike.rpc.client.core.pojo.RpcResponseHandlerContext;
import com.mike.rpc.client.net.holder.ResponseHolder;
import com.mike.rpc.client.net.holder.ResponseHolderUtils;

public class DoRecieveHandler implements WorkHandler<RpcResponseHandlerContext> {
    @Override
    public void onEvent(RpcResponseHandlerContext rpcResponseHandlerContext) throws Exception {
        RpcResponse rpcResponse = rpcResponseHandlerContext.getT();
        long id = rpcResponse.getId();
        ResponseHolder responseHolder = ResponseHolderUtils.getHolder(id);
        if (responseHolder==null) {return;}
        responseHolder.setSuccess(true);
        if (rpcResponse.getMessage()!=null){
            System.out.println("error:"+rpcResponse.getMessage());
            responseHolder.setMessage(rpcResponse.getMessage());
        }else {
            if (rpcResponse.getResult()!=null){
                responseHolder.setResult(rpcResponse.getResult());
            }
            else {
                responseHolder.setResult(null);
            }
        }
    }
}
