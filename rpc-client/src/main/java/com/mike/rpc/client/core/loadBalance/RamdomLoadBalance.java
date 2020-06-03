package com.mike.rpc.client.core.loadBalance;

import com.mike.rpc.client.net.pojo.IpChannelHolder;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class RamdomLoadBalance implements loadBalance{
    private ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
    @Override
    public IpChannelHolder select(List<IpChannelHolder> ipChannelHolders) {
        int i = ipChannelHolders.size()<=1?0:threadLocalRandom.nextInt(ipChannelHolders.size());
        return ipChannelHolders.get(Math.max(i, 0));
    }
}
