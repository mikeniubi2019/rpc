package com.mike.rpc.client.reflect.handler;

import com.mike.rpc.api.net.pojo.RpcRequest;
import com.mike.rpc.client.annotation.Service;
import com.mike.rpc.client.contex.AbstractContex;
import com.mike.rpc.client.core.cache.Cache;
import com.mike.rpc.client.core.pojo.FallbackService;
import com.mike.rpc.client.net.holder.ResponseHolder;
import com.mike.rpc.client.net.holder.ResponseHolderUtils;
import com.mike.rpc.client.net.pojo.IpChannelHolder;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class DoSendHandler implements InvocationHandler {
    private String serviceName;
    private String version;
    private AbstractContex contex;
    private ThreadLocalRandom random =ThreadLocalRandom.current();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Cache cache =null;
        //TODO查找缓存
        Service service = contex.getServices(serviceName+version);
        if (service!=null&&service.isChache()){
            cache = contex.getCachePool().get(serviceName+version);
            if (cache!=null&&cache.getResult()!=null){
                return cache.getResult();
            }
        }
        //生成request
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethondName(method.getName());
        rpcRequest.setParams(args);
        rpcRequest.setServiceName(serviceName);
        rpcRequest.setVersion(version);
        ResponseHolder responseHolder = new ResponseHolder();
        Object result = null;
        IpChannelHolder ipChannelHolder = null;
        while (!responseHolder.isSuccess() && responseHolder.getTryCount()<3){
            rpcRequest.setId(random.nextLong());
            //根据服务名称获取chennle列表
            ipChannelHolder = contex.getChannlesByServiceName(serviceName+version);
            //查找是否回退
            if (ipChannelHolder.isFallback()){
                service = contex.getServices(serviceName+version);
                FallbackService fallbackService = service.getFallbackService();
                if (fallbackService==null){
                    throw new Exception(serviceName+version+":发生熔断！并且没有回退方法");
                }
                System.out.println("执行回退方法");
                return fallbackService.getMethod().invoke(fallbackService.getInstant());
            }
            //负载均衡
            Channel channel = ipChannelHolder.getChannel();

            //发起请求
            channel.writeAndFlush(rpcRequest);
            //管理接收对象
            responseHolder.setId(rpcRequest.getId());
            ResponseHolderUtils.putHolder(responseHolder);

            result = responseHolder.get();
            ResponseHolderUtils.removeHolder(rpcRequest.getId());
            responseHolder.setTryCount((byte)( responseHolder.getTryCount()+(byte)1));
            responseHolder.setTimeOut(responseHolder.getTimeOut()+3000);
        }
        if (!responseHolder.isSuccess()){
            if (ipChannelHolder!=null){
                ipChannelHolder.setFallback(true);
            }
            throw new Exception("请求失败，重试"+responseHolder.getTryCount()+"次");
        }
        //设置缓存
        if (result!=null){
            if (cache==null) {
                cache = new Cache(result);
            }else {
                cache.setResult(result);
            }
            contex.getCachePool().put(serviceName+version,cache);
        }
        return result;
    }

    public DoSendHandler(String serviceName, String version,AbstractContex contex) {
        this.serviceName = serviceName;
        this.version = version;
        this.contex = contex;
    }
}
