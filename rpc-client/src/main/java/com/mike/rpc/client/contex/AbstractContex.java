package com.mike.rpc.client.contex;

import com.mike.rpc.api.asynchronize.AsychronizeHandler;
import com.mike.rpc.client.annotation.Fallback;
import com.mike.rpc.client.annotation.Service;
import com.mike.rpc.client.annotation.ServiceProvicer;
import com.mike.rpc.client.annotation.ServiceProx;
import com.mike.rpc.client.asychroniz.ClientAsychronizeHandlerContex;
import com.mike.rpc.client.asychroniz.ResponseFactory;
import com.mike.rpc.client.core.cache.Cache;
import com.mike.rpc.client.core.loadBalance.RamdomLoadBalance;
import com.mike.rpc.client.core.loadBalance.loadBalance;
import com.mike.rpc.client.core.pojo.FallbackService;
import com.mike.rpc.client.core.serviceHandler.DoRecieveHandler;
import com.mike.rpc.client.net.pojo.IpChannelHolder;
import com.mike.rpc.client.net.starter.ClientBootstrat;
import com.mike.rpc.client.reflect.handler.DoSendHandler;
import com.mike.rpc.client.registor.ClientRegistor;
import com.mike.rpc.client.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractContex implements Contex{
    private String basePackage;
    private Map<String,Service> serviceMap = new ConcurrentHashMap();
    private List<String> serviceClassNames = new ArrayList<>();
    private List<Class> serviceClasss = new ArrayList<>();
    private List<ChannelHandler> channelHandlers = new ArrayList<>();
    private ChannelInitializer channelInitializer;
    private ClientBootstrat clientBootstrat;
    private Map<String,String> propertys = new ConcurrentHashMap<>();
    private Map<String,List<Channel>> channelMap = new ConcurrentHashMap<>();
    private List<Channel> channelList = new ArrayList<>();

    private AsychronizeHandler asychronizeHandler;
    private ClientRegistor clientRegistor;
    private Map<String,List<IpChannelHolder>> ServiceChannelMap = new ConcurrentHashMap<>();
    private volatile boolean channelChangeFlag = true;
    private Map<String,IpChannelHolder> ipStrMappingChannelHolder = new ConcurrentHashMap<>();
    private loadBalance loadBalance;
    private WeakHashMap<String, Cache> cachePool = new WeakHashMap(1024*1024);

    public String getProperty(String key){
        return propertys.get(key);
    }
    public List<ChannelHandler> getChannelHandlers() {
        return channelHandlers;
    }


    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setChannelHandlers(List<ChannelHandler> channelHandlers) {
        this.channelHandlers = channelHandlers;
    }

    @Override
    public Service getServices(String serviceName){
        try {
            return serviceMap.get(StringUtils.BeanNametranst(serviceName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Service getServices(Class type){
        return serviceMap.get(type.getSimpleName());
    }


    public void setService(String beanName,Service service){
        serviceMap.put(beanName,service);
    }

    @Override
    public void registService(Service service) {
        //TODO
    }

    @Override
    public void reflesh() throws Exception {
        //获得全局属性
        PutProperties();
        //扫描bean
        scan();
        //把扫描出来的bean，注入容器
        putContexIfAbsentAnnotation();
        //从注册中心获取服务
        this.clientRegistor = new ClientRegistor(this);
        initRgestor();
        //加载负载均衡器
        innitLoadbalance();
        //生成disruptor
        this.asychronizeHandler = new ClientAsychronizeHandlerContex(new ResponseFactory());
        addConsumerHandler();
        //开启disruptor
        asychronizeHandler.start();

        //生成channelHandler
        doRegistChannelHandler();
        //开启netty,生成服务信息
        openBootstrap();
        //关联channel和serviceName
        combinChannelToService();
//        System.out.println(this);
        //serveBootstrat.shutdown();
//TODO
    }

    private void innitLoadbalance() {
        if (this.loadBalance==null){
            this.loadBalance=new RamdomLoadBalance();
        }
    }

    private void initRgestor() {
        this.clientRegistor.initRegistor();
        this.clientRegistor.innitCache();
    }

    private void addConsumerHandler() {
        for (int i =0;i<Runtime.getRuntime().availableProcessors();i++){
            this.asychronizeHandler.regeistInvokeHandler(new DoRecieveHandler());
        }
    }

    private void combinChannelToService() {
        for (Map.Entry entry :serviceMap.entrySet() ){
            String serviceName = (String) entry.getKey();
            channelMap.put(serviceName,channelList);
        }
    }

    private Map<String,List<IpChannelHolder>> generatorServiceMapChannel(){
        return null;
    }

    private void openBootstrap() {
        this.clientBootstrat = new ClientBootstrat(this);
        this.clientBootstrat.setChannelInitializer(this.channelInitializer);
        this.clientBootstrat.innitClient();

        for (List<IpChannelHolder> ipChannelHolderList : this.getServiceChannelMap().values()){
            for (IpChannelHolder ipChannelHolder : ipChannelHolderList){
                ClientBootstrat clientBootstrat = ipChannelHolder.getBootstrat();
                if (clientBootstrat==null){
                    clientBootstrat = this.clientBootstrat;
                }
                Channel channel = ipChannelHolder.getChannel();
                if (channel==null){
                    ipChannelHolder.setChannel(clientBootstrat.connect(ipChannelHolder.getAddr(),ipChannelHolder.getPort()));
                }
                //ipStrMappingChannelHolder.put(ipChannelHolder.getAddr()+":"+ipChannelHolder.getPort(),ipChannelHolder);
            }
        }
    }

    private void putContexIfAbsentAnnotation() {
        if (serviceClasss.isEmpty()) return;
        for (Class clazz : serviceClasss){
            Service service = null;
            try {
                service = classTransferService(clazz);
                checkCache(service,clazz);
                instantIfProxAbsent(service);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            } catch (InstantiationException e) {
                e.printStackTrace();
                continue;
            }
            doPutContex(service);
        }
    }

    private void checkCache(Service service, Class clazz) {
        if (clazz.isAnnotationPresent(ServiceProvicer.class)){
            ServiceProvicer serviceProvicer = (ServiceProvicer) clazz.getDeclaredAnnotation(ServiceProvicer.class);
            service.setChache(serviceProvicer.cache());
        }
    }

    private void instantIfProxAbsent(Service service) {
        Class clazz = service.getInstant().getClass();
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length>0){
            for (Field field : fields){
                if (field.isAnnotationPresent(ServiceProx.class)){
                    ServiceProx serviceProx = field.getDeclaredAnnotation(ServiceProx.class);
                    String[] beanNames = serviceProx.beanName();
                    String beanName = field.getType().getName();
                    String[] strings = new String[beanNames.length+1];
                    System.arraycopy(beanNames,0,strings,0,beanNames.length);
                    strings[beanNames.length]=beanName;
                    String version = serviceProx.version();
                    service.setVersion(version);
                    service.setServiceClassNames(strings);
                    Object prox = creatProx(field,service.getVersion());
                    try {
                        field.set(service.getInstant(),prox);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //回退方法
        Method[] methonds = clazz.getDeclaredMethods();
        if (methonds.length>0){
            for (Method method : methonds){
                if (method.isAnnotationPresent(Fallback.class)){
                    if (method.getParameterCount()>0) {
                        System.out.println(method.getName()+"回退方法暂时不支持参数,已忽略");
                    }
                    FallbackService fallbackService = new FallbackService();
                    fallbackService.setClazz(clazz);
                    fallbackService.setInstant(service.getInstant());
                    fallbackService.setMethod(method);
                    service.setFallbackService(fallbackService);
                    break;
                }
            }
        }
    }

    private Object creatProx(Field field,String version) {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{field.getType()},
                new DoSendHandler(field.getGenericType().getTypeName(),version,this));
    }

    private void doPutContex(Service service) {
        String version = service.getVersion();
        String[] serviceNames = service.getServiceClassNames();

        Arrays.stream(serviceNames).forEach(name->{
            serviceMap.put(name+version,service);
        });

    }

    private Service classTransferService(Class clazz) throws IllegalAccessException, InstantiationException {
        Service service = new Service();
        Method[] methods = clazz.getDeclaredMethods();
        service.setMethods(methods);
        service.setServiceClass(clazz);
        service.setInstant(clazz.newInstance());
        serviceClassNames.add(clazz.getSimpleName());
        return service;
    }

    public Map<String, Service> getServiceMap() {
        return serviceMap;
    }

    public void setServiceMap(Map<String, Service> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public List<String> getServiceClassNames() {
        return serviceClassNames;
    }

    public void setServiceClassNames(List<String> serviceClassNames) {
        this.serviceClassNames = serviceClassNames;
    }

    public void putServiceBeanClass(Class c){
        serviceClasss.add(c);
    }

    public List<Class> getServiceClasss() {
        return serviceClasss;
    }

    public void setServiceClasss(List<Class> serviceClasss) {
        this.serviceClasss = serviceClasss;
    }

    public ChannelInitializer getChannelInitializer() {
        return channelInitializer;
    }

    public void setChannelInitializer(ChannelInitializer channelInitializer) {
        this.channelInitializer = channelInitializer;
    }
    public void setProperty(String key,String value){
        this.propertys.put(key,value);
    }

    public void addChannleHandler(ChannelHandler channelHandler){
        if (this.channelHandlers.contains(channelHandler)) return;
        this.channelHandlers.add(channelHandler);
    }

    //根据服务名获取，并进行负载均衡
    public IpChannelHolder getChannlesByServiceName(String serviceName) throws Exception {
        List<IpChannelHolder> ipChannelHolderList = ServiceChannelMap.get(serviceName);
        if (ipChannelHolderList==null||ipChannelHolderList.size()<1) {
            throw new Exception("channel为空");
        }
        IpChannelHolder ipChannelHolder = this.loadBalance.select(ipChannelHolderList);
        return this.ipStrMappingChannelHolder.get(ipChannelHolder.getAddr()+":"+ipChannelHolder.getPort());
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }



    public AsychronizeHandler getAsychronizeHandler() {
        return asychronizeHandler;
    }

    public void setAsychronizeHandler(AsychronizeHandler asychronizeHandler) {
        this.asychronizeHandler = asychronizeHandler;
    }

    public Map<String, List<IpChannelHolder>> getServiceChannelMap() {
        return ServiceChannelMap;
    }

    public void setServiceChannelMap(Map<String, List<IpChannelHolder>> serviceChannelMap) {
        ServiceChannelMap = serviceChannelMap;
    }


    public void fireChannelEvent() {
        //无锁更新
        if (channelChangeFlag){
            channelChangeFlag=false;
            //修改channel信息
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Set<String> set = this.getServiceChannelMap().values().stream().flatMap(Collection::stream).map(ipChannelHolder -> ipChannelHolder.getAddr()+":"+ipChannelHolder.getPort()).collect(Collectors.toSet());
            for (String ipAdd : ipStrMappingChannelHolder.keySet()){
                if (!set.contains(ipAdd)){
                    ipStrMappingChannelHolder.remove(ipAdd);
                }
            }

            for (List<IpChannelHolder> ipChannelHolderList : this.getServiceChannelMap().values()){
                for (int i=0;i<ipChannelHolderList.size();i++){
                    IpChannelHolder ipChannelHolder = ipChannelHolderList.get(i);
                    ipChannelHolder = ipStrMappingChannelHolder.get(ipChannelHolder.getAddr()+":"+ipChannelHolder.getPort());

                    if (ipChannelHolder.getBootstrat()==null){
                        ipChannelHolder.setBootstrat(this.clientBootstrat);
                    }
                    if (ipChannelHolder.getChannel()==null){
                        ipChannelHolder.setChannel(clientBootstrat.connect(ipChannelHolder.getAddr(),ipChannelHolder.getPort()));
                    }
                    ipChannelHolderList.set(i,ipChannelHolder);
                }

            }
            channelChangeFlag=true;
        }
    }

    public Map<String, IpChannelHolder> getIpStrMappingChannelHolder() {
        return ipStrMappingChannelHolder;
    }

    public void setIpStrMappingChannelHolder(Map<String, IpChannelHolder> ipStrMappingChannelHolder) {
        this.ipStrMappingChannelHolder = ipStrMappingChannelHolder;
    }

    public loadBalance getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(loadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    public WeakHashMap<String,Cache> getCachePool() {
        return cachePool;
    }

    public void setCachePool(WeakHashMap cachePool) {
        this.cachePool = cachePool;
    }
}
