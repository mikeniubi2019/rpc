package com.mike.rpc.client.core.loadBalance;

import com.mike.rpc.client.net.pojo.IpChannelHolder;

import java.util.List;

public interface loadBalance {
        IpChannelHolder select(List<IpChannelHolder> ipChannelHolders);
}
