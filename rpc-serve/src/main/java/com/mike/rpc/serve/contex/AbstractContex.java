package com.mike.rpc.serve.contex;

import com.mike.rpc.api.asynchronize.AsychronizeHandler;
import com.mike.rpc.serve.annotation.Service;
import com.mike.rpc.serve.annotation.ServiceProvicer;
import com.mike.rpc.serve.asychroniz.RequestFactory;
import com.mike.rpc.serve.asychroniz.ServeAsychronizeHandlerContex;
import com.mike.rpc.serve.core.serviceHandler.DoServiceHandler;
import com.mike.rpc.serve.net.starter.ServeBootstrat;
import com.mike.rpc.serve.registor.Registor;
import com.mike.rpc.serve.registor.ServiceRegistor;
import com.mike.rpc.serve.utils.StringUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;


import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractContex implements Contex{
    private String basePackage;
    private Map<String,Service> serviceMap = new ConcurrentHashMap();
    private List<String> serviceClassNames = new ArrayList<>();
    private List<Class> serviceClasss = new ArrayList<>();
    private List<ChannelHandler> channelHandlers = new ArrayList<>();
    private ChannelInitializer channelInitializer;
    private ServeBootstrat serveBootstrat;
    private Map<String,String> propertys = new ConcurrentHashMap<>();
    private AsychronizeHandler asychronizeHandler;

    public String getProperty(String key){
        return propertys.get(key);
    }

    public List<ChannelHandler> getChannelHandlers() {
        return channelHandlers;
    }

    public void setChannelHandlers(List<ChannelHandler> channelHandlers) {
        this.channelHandlers = channelHandlers;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
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

        //注册服务到注册中心
        Registor registor = new ServiceRegistor(this);
        registor.initRegistor();
        registor.publishServices();

        //生成disruptor线程池
        this.asychronizeHandler = new ServeAsychronizeHandlerContex(new RequestFactory());
        //添加实际处理service的类到disruptor
        addServiceConsumers();
        //开启disruptor
        asychronizeHandler.start();
        //生成channelHandler
        doRegistChannelHandler();
        //开启netty服务器
        openBootstrap();
        //
        //System.out.println(this);
        //serveBootstrat.shutdown();
//TODO
    }

    private void addServiceConsumers() {
        for (int i=0;i<Runtime.getRuntime().availableProcessors();i++){
            this.asychronizeHandler.regeistInvokeHandler(new DoServiceHandler());
        }
    }

    private void openBootstrap() {
        if (serveBootstrat==null) {
            checkBootstratIsPresent();
        }
        serveBootstrat.setChannelInitializer(channelInitializer);
        serveBootstrat.start();
    }

    private void checkBootstratIsPresent() {
        String add = "localhost";
        String port = "8079";
        if (propertys.containsKey("address")) {
            add = propertys.get("address");
        }else {
            propertys.put("address","localhost");
        }
        if (propertys.containsKey("port")) {
            port = propertys.get("port");
        }else {
            propertys.put("port","8079");
        }
        serveBootstrat=new ServeBootstrat(add,Integer.parseInt(port));
    }

    private void putContexIfAbsentAnnotation() {
        if (serviceClasss.isEmpty()) return;
        for (Class clazz : serviceClasss){
            Service service = null;
            try {
                service = classTransferService(clazz);
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

    private void doPutContex(Service service) {
        String version = service.getVersion();
        String[] serviceNames = service.getServiceClassName();
        Arrays.stream(serviceNames).forEach(name->{
            serviceMap.put(name+version,service);
        });
    }

    private Service classTransferService(Class clazz) throws IllegalAccessException, InstantiationException {
        Service service = new Service();
        Method[] methods = clazz.getDeclaredMethods();
        ServiceProvicer serviceProvicer = (ServiceProvicer) clazz.getAnnotation(ServiceProvicer.class);
        String[] serviceNames = serviceProvicer.beanName();
        Class[] interfaces = clazz.getInterfaces();
        String[] types=null;
        if (interfaces.length>0){
            types = new String[interfaces.length+serviceNames.length];
            for (int index=0;index<interfaces.length;index++){
                types[index]=interfaces[index].getName();
            }
            System.arraycopy(serviceNames,0,types,interfaces.length,serviceNames.length);
        }
        String version = serviceProvicer.version();
        service.setMethods(methods);
        service.setServiceClass(clazz);
        service.setVersion(version);
        if (types==null){
            service.setServiceClassName(serviceNames);
        }else {
            service.setServiceClassName(types);
        }

        service.setInstant(clazz.newInstance());
        serviceClassNames.addAll(Arrays.asList(serviceNames));
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

    public AsychronizeHandler getAsychronizeHandler() {
        return asychronizeHandler;
    }

    public void setAsychronizeHandler(AsychronizeHandler asychronizeHandler) {
        this.asychronizeHandler = asychronizeHandler;
    }
}
