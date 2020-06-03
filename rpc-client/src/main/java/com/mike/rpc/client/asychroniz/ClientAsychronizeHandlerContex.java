package com.mike.rpc.client.asychroniz;

import com.lmax.disruptor.RingBuffer;
import com.mike.rpc.api.asynchronize.AsychronizeHandlerContext;
import com.mike.rpc.api.asynchronize.ReceiveObjectFactory;

import com.mike.rpc.client.core.pojo.RpcResponseHandlerContext;
import net.sf.cglib.beans.BeanCopier;

public class ClientAsychronizeHandlerContex extends AsychronizeHandlerContext {
    private BeanCopier beanCopier;

    public ClientAsychronizeHandlerContex(ReceiveObjectFactory receiveObjectFactory) {
        super(receiveObjectFactory);
        this.beanCopier = BeanCopier.create(RpcResponseHandlerContext.class,RpcResponseHandlerContext.class,false);
    }

    @Override
    public void onData(Object o) {
        RingBuffer ringBuffer = super.getRingBuffer();
        long seq = ringBuffer.next();
        try {
            beanCopier.copy(o,ringBuffer.get(seq),null);
        }finally {
            ringBuffer.publish(seq);
        }
    }
}
