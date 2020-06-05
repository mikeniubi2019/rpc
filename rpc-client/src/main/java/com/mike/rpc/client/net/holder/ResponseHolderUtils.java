package com.mike.rpc.client.net.holder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ResponseHolderUtils {

    private static Map<String,ResponseHolder> map = new ConcurrentHashMap();
//    private static LinkedBlockingQueue<ResponseHolder> delayQueue = new LinkedBlockingQueue();
    private static long count = 0;
//    static {
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(()->{
//            if (delayQueue.size()>0){
//                boolean flag = true;
//                while (flag){
//                    ResponseHolder responseHolder = delayQueue.peek();
//
//                    if (responseHolder.isSuccess()){
//                        map.remove(responseHolder.getId());
//                        delayQueue.poll();
//                        continue;
//                    }
//                    //超时
//                    if (responseHolder.getTimeOut()>=System.currentTimeMillis()){
//                        delayQueue.poll();
//                        map.remove(responseHolder.getId());
//                        continue;
//                    }
//
//                    responseHolder = map.get(responseHolder.getId());
//                    if (responseHolder==null) {
//                        delayQueue.poll();
//                        continue;
//                    }
//
//                    flag=false;
//                }
//                count+=count;
//                if (count%10==0){
//                    System.gc();
//                }
//            }
//        },1,1, TimeUnit.SECONDS);
//    }

    public static ResponseHolder getHolder(String id){
        return map.get(id);
    }

    public static void putHolder(ResponseHolder responseHolder){
        map.put(responseHolder.getId(),responseHolder);
//        delayQueue.add(responseHolder);
    }

    public static void removeHolder(String id){
        map.remove(id);
    }


}
