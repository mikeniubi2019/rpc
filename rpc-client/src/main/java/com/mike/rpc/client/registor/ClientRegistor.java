package com.mike.rpc.client.registor;

import com.mike.rpc.client.annotation.Service;
import com.mike.rpc.client.contex.AbstractContex;
import com.mike.rpc.client.net.pojo.IpChannelHolder;
import com.mike.rpc.client.net.starter.ClientBootstrat;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ClientRegistor implements Registor{
    private String registIp;
    private String registPort;
    private CuratorFramework curatorFramework;
    private AbstractContex abstractContex;
    private Map<String, Service> serviceMap;


    @Override
    public void innitCache() {
        String basePackage = "/service";
        try {
            if (curatorFramework.checkExists().forPath(basePackage)==null){
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(basePackage);
            }
            for (String serviceName : serviceMap.keySet()){
                String servicePackage= basePackage + "/" + serviceName;
                List<String> pathList=null;
                if (curatorFramework.checkExists().forPath(servicePackage)==null){
                    curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(servicePackage);
                    pathList = new ArrayList();
                }else {
                    pathList = curatorFramework.getChildren().forPath(servicePackage);
                }
                creatPathCache(servicePackage,serviceName);
                //把holder放入context里
                abstractContex.getServiceChannelMap().put(serviceName,pathList.stream().filter(StringUtils::isNotEmpty).map(this::transferPathToIpChannelHolder).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void creatPathCache(String servicePackage,String serviceName) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(this.curatorFramework,servicePackage,false);
        pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        pathChildrenCache.getListenable().addListener(new ServiceChangeListener(this,serviceName));
    }

    public IpChannelHolder transferPathToIpChannelHolder(String path){
        String[] strings = path.split(":");
        IpChannelHolder ipChannelHolder = abstractContex.getIpStrMappingChannelHolder().get(strings[0]+":"+strings[1]);
        if (ipChannelHolder!=null){
            return ipChannelHolder;
        }
        ipChannelHolder = new IpChannelHolder();
        ipChannelHolder.setAddr(strings[0]);
        ipChannelHolder.setPort(Integer.valueOf(strings[1]));
        ipChannelHolder.setWeight(Integer.valueOf(strings[2]));
        abstractContex.getIpStrMappingChannelHolder().put(strings[0]+":"+strings[1],ipChannelHolder);
        return ipChannelHolder;
    }

    @Override
    public void initRegistor() {
        this.serviceMap = abstractContex.getServiceMap();
        this.registIp = abstractContex.getProperty("registIp")==null?"localhost":abstractContex.getProperty("registIp");
        this.registPort = abstractContex.getProperty("registPort")==null?"2181":abstractContex.getProperty("registPort");
        this.curatorFramework = creatCuratorFramework();
        curatorFramework.start();
    }

    private CuratorFramework creatCuratorFramework() {
          return CuratorFrameworkFactory.builder()
                .connectString(registIp+":"+registPort)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(100,3))
                .namespace("MIKERPC")
                .build();
    }

    public ClientRegistor(AbstractContex abstractContex) {
        this.abstractContex = abstractContex;
    }

    public AbstractContex getAbstractContex() {
        return abstractContex;
    }

    public void setAbstractContex(AbstractContex abstractContex) {
        this.abstractContex = abstractContex;
    }
}
