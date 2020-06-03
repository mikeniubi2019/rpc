package com.mike.rpc.serve.asychroniz;

import com.lmax.disruptor.RingBuffer;
import com.mike.rpc.api.asynchronize.AsychronizeHandlerContext;
import com.mike.rpc.api.asynchronize.ReceiveObjectFactory;
import com.mike.rpc.serve.core.pojo.RpcRequestHandlerContext;
import net.sf.cglib.beans.BeanCopier;

public class ServeAsychronizeHandlerContex extends AsychronizeHandlerContext {
    private BeanCopier beanCopier;

    public ServeAsychronizeHandlerContex(ReceiveObjectFactory receiveObjectFactory) {
        super(receiveObjectFactory);
        this.beanCopier = BeanCopier.create(RpcRequestHandlerContext.class,RpcRequestHandlerContext.class,false);
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
