package com.mike.rpc.client.registor;

import com.mike.rpc.client.net.pojo.IpChannelHolder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.util.List;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED;
import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_REMOVED;

public class ServiceChangeListener implements PathChildrenCacheListener {
    private ClientRegistor clientRegistor;
    private String serviceName;
    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
        //TODO 这里可以放入channel信息，比如连接数等
        if (pathChildrenCacheEvent.getType().equals(CHILD_ADDED)){
            String longPath = pathChildrenCacheEvent.getData().getPath();
            List<IpChannelHolder> ipChannelHolderList = clientRegistor.getAbstractContex().getServiceChannelMap().get(serviceName);
            IpChannelHolder ipChannelHolder = clientRegistor.transferPathToIpChannelHolder(longPath);
            if (!ipChannelHolderList.contains(ipChannelHolder)){
                ipChannelHolderList.add(ipChannelHolder);
                clientRegistor.getAbstractContex().fireChannelEvent();
            }

        }
        if (pathChildrenCacheEvent.getType().equals(CHILD_REMOVED)){
            String longPath = pathChildrenCacheEvent.getData().getPath();
            List<IpChannelHolder> ipChannelHolderList = clientRegistor.getAbstractContex().getServiceChannelMap().get(serviceName);
            synchronized (clientRegistor){
                for (IpChannelHolder ipChannelHolder : ipChannelHolderList){
                    if (longPath.equals(ipChannelHolder.getAddr()+":"+ipChannelHolder.getPort()+":"+ipChannelHolder.getWeight())){
                        ipChannelHolderList.remove(ipChannelHolder);
                        clientRegistor.getAbstractContex().fireChannelEvent();
                    }
                }
            }
        }

    }

    public ServiceChangeListener(ClientRegistor clientRegistor,String serviceName) {
        this.clientRegistor = clientRegistor;
        this.serviceName=serviceName;
    }
}
