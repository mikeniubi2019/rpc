package com.mike.rpc.api.net.channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelHolder {
    private long groupId;
    private Map<Long,ChannelHandle> channelMap= new ConcurrentHashMap<>();

    public ChannelHandle getChannelHandle(long id){
        return channelMap.get(id);
    }

    public void addChannelHandle(long id,ChannelHandle channelHandle){
        channelMap.put(id,channelHandle);
    }

    public void removeChannel(long id){
        channelMap.remove(id);
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public Map<Long, ChannelHandle> getChannelMap() {
        return channelMap;
    }

    public void setChannelMap(Map<Long, ChannelHandle> channelMap) {
        this.channelMap = channelMap;
    }

}
