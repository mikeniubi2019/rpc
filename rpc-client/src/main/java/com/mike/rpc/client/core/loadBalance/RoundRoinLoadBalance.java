package com.mike.rpc.client.core.loadBalance;

import com.mike.rpc.client.net.pojo.IpChannelHolder;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class RoundRoinLoadBalance implements loadBalance{
    private AtomicLong count = new AtomicLong(0);
    @Override
    public IpChannelHolder select(List<IpChannelHolder> ipChannelHolders) {
        return ipChannelHolders.get((int) (count.incrementAndGet()%ipChannelHolders.size()));
    }
}
