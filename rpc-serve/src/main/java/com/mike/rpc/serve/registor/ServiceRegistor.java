package com.mike.rpc.serve.registor;

import com.mike.rpc.serve.annotation.Service;
import com.mike.rpc.serve.contex.AbstractContex;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.Map;

public class ServiceRegistor implements Registor{
    private String registIp;
    private String registPort;
    private String localIp;
    private String localPort;

    private String weight;
    private CuratorFramework curatorFramework;

    private AbstractContex abstractContex;

    private Map<String,Service> serviceMap;

    @Override
    public void publishServices() {
        String basePackage = "/service";
        String ipPortStr = "/"+localIp+":"+localPort+":"+weight;
        try {
            if (curatorFramework.checkExists().forPath(basePackage)==null){
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(basePackage);
            }
            for (String serviceName : serviceMap.keySet()){

                String servicePackage= basePackage + "/" + serviceName;
                if (curatorFramework.checkExists().forPath(servicePackage)==null){
                    curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(servicePackage);
                }

                curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(servicePackage+ipPortStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initRegistor() {
        this.serviceMap = abstractContex.getServiceMap();
        this.registIp = abstractContex.getProperty("registIp")==null?"localhost":abstractContex.getProperty("registIp");
        this.registPort = abstractContex.getProperty("registPort")==null?"2181":abstractContex.getProperty("registPort");
        this.localIp = abstractContex.getProperty("address")==null?"localhost":abstractContex.getProperty("address");
        this.localPort = abstractContex.getProperty("port")==null?"8079":abstractContex.getProperty("port");
        this.weight = abstractContex.getProperty("weight")==null?"1":abstractContex.getProperty("weight");
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

    public ServiceRegistor(AbstractContex abstractContex) {
        this.abstractContex = abstractContex;
    }
}
