package com.mike.rpc.serve.contex;

import com.mike.rpc.api.net.bootstrat.DefaultHandleChainBuilder;
import com.mike.rpc.serve.annotation.ServiceProvicer;
import com.mike.rpc.serve.core.channelHandler.InvokeHandler;
import com.mike.rpc.serve.utils.ContexUtils;
import io.netty.channel.ChannelHandler;

import java.io.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class AnnotationContex extends AbstractContex{
    private String contexName;
    private URL url;
    private List<String> beanClassNames=new ArrayList<>();
    private String path;
    private String classSeprator = "\\\\classes\\\\";
    private String propertyPath;
    @Override
    public void setLocation(String path){
        this.path=path;
    }

    public AnnotationContex(String path) throws Exception {
        this.path = path;
        this.contexName=this.getClass().getSimpleName();
        reflesh();
        new ContexUtils().setContex(this);
    }

    @Override
    public void scan() {
        String location = checkPath(super.getBasePackage());
        scanClassInPath(location);
        if (!beanClassNames.isEmpty()){
            beanClassNames.stream().forEach((beanName)->{
                try {
                    Class clazz = Class.forName(beanName);
                    if (clazz.isAnnotationPresent(ServiceProvicer.class)){
                        putServiceBeanClass(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void doRegistChannelHandler() {
        //TODO
        //加入rpc业务逻辑handler// 在这里手动添加，后期可以自动扫描添加
        //super.addChannleHandler(new InvokeHandler());

        super.addChannleHandler(new InvokeHandler());
        DefaultHandleChainBuilder defaultHandleChainBuilder = new DefaultHandleChainBuilder();
        defaultHandleChainBuilder.setUserHandles(super.getChannelHandlers());
        super.setChannelInitializer(defaultHandleChainBuilder.build());
    }

    @Override
    public void PutProperties() throws Exception {
        if (url==null){
            //ClassLoader classLoader = this.getClass().getClassLoader();
            if (!path.startsWith("/")){
                path = "/"+path;
            }
            url = this.getClass().getResource(path);
        }
        findProperty(new File(url.getFile()));
        loadAndSetProperties();
        String base = super.getProperty("basePackage");
        if (base==null) throw new Exception("basepackge属性不能为空！");
        super.setBasePackage(base);
    }

    private void loadAndSetProperties() {
        Properties properties = new Properties();
        InputStream inputStream = loadPropertyFile();
        if (inputStream == null) {
            return;
        }
        try {
            properties.load(inputStream);
            for (Map.Entry entry : properties.entrySet()){
                super.setProperty((String)entry.getKey(),(String)entry.getValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream loadPropertyFile() {
        try {
            if (propertyPath!=null)
                return new FileInputStream(new File(propertyPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registChannelHandler(ChannelHandler channelHandler) {
        super.getChannelHandlers().add(channelHandler);
    }

    private void scanClassInPath(String location) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        url = classLoader.getResource(location);
        File file = new File(url.getFile());
        findClass(file.getAbsolutePath());
    }

    private void findClass(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) return;
        if (file.isDirectory()){
            for (File dirFile : file.listFiles()){
                findClass(dirFile.getAbsolutePath());
            }
        }else {
            if (fileName.endsWith(".class")){
                beanClassNames.add(file.getPath().split(classSeprator)[1].replaceAll("\\\\","\\.").split("\\.class")[0]);
            }
        }
    }

    private void findProperty(File file) {

        if (!file.exists() || propertyPath!=null) return;
        if (file.isDirectory()){
            for (File dirFile : file.listFiles()){
                findProperty(dirFile);
            }
        }else {
            if (file.getName().endsWith(".properties")){
                propertyPath = file.getAbsolutePath();
            }
        }
    }

    private String checkPath(String location) {
        int index = location.indexOf("file://");
        location = index!=-1?location.substring(index):location;
        return location.replaceAll("\\.","/");
    }


}
