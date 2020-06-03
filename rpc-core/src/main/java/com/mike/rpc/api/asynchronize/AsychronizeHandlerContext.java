package com.mike.rpc.api.asynchronize;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public abstract class AsychronizeHandlerContext<T> implements AsychronizeHandler{
    private List<WorkHandler> workHandlers=new ArrayList<>();
    private RingBuffer<T> ringBuffer;
    private SequenceBarrier sequenceBarrier;
    private WorkerPool workerPool;
    private ReceiveObjectFactory receiveObjectFactory;

    public AsychronizeHandlerContext(ReceiveObjectFactory receiveObjectFactory) {
        this.receiveObjectFactory = receiveObjectFactory;
    }

    public RingBuffer generatRingbuff(){
        return RingBuffer.create(
                ProducerType.MULTI,
                receiveObjectFactory::getObject,
                1024*1024,
                new SleepingWaitStrategy());
    }

    public SequenceBarrier generatSequenceBarrier(){
        return this.ringBuffer.newBarrier();
    }

    public void generatWorkPool(){
        this.workerPool = new WorkerPool(this.ringBuffer,this.sequenceBarrier,new ExceptionHandler(), this.workHandlers.toArray(new WorkHandler[this.workHandlers.size()]));
    }

    @Override
    public void regeistInvokeHandler(WorkHandler workHandler) {
        if (!workHandlers.contains(workHandler)){
            this.workHandlers.add(workHandler);
        }
    }

    @Override
    public List<WorkHandler> getWorkHandler() {
        return this.workHandlers;
    }

    public void addWorkHandler(WorkHandler workHandler){
        if (!workHandlers.contains(workHandler)){
            this.workHandlers.add(workHandler);
        }
    }

    @Override
    public void start() {

        this.ringBuffer = generatRingbuff();

        this.sequenceBarrier = generatSequenceBarrier();

        generatWorkPool();

        this.ringBuffer.addGatingSequences(this.workerPool.getWorkerSequences());

        this.workerPool.start(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

    }

    public RingBuffer<T> getRingBuffer() {
        return ringBuffer;
    }

    public void setRingBuffer(RingBuffer<T> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }
}
