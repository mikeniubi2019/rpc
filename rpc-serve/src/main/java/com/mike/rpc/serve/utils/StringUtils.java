package com.mike.rpc.serve.utils;

public class StringUtils {
    public static String BeanNametranst(String beanName) throws Exception {
        if (beanName.isEmpty()) throw new  Exception("字符串为空");
        String tempBeanName = beanName.trim();
        return tempBeanName;
    }
}
