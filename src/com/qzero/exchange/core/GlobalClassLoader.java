package com.qzero.exchange.core;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 全局的类加载器
 * 可以通过自定义的类名称来找到一个类
 */
public class GlobalClassLoader {

    private static final Logger log=Logger.getRootLogger();

    /**
     * 已注册的类
     * key为类名称，value为类的class
     */
    private static Map<String,Class> classLoader=new HashMap<>();

    /**
     * 注册一个类
     * @param name 类名
     * @param type 类的class对象
     * @return 是否成功，一般的，如果加载器中包含这个名称了，会返回false
     */
    public static boolean registerClass(String name,Class type){
        if(type==null || classLoader==null || classLoader.containsKey(name))
            return false;
        classLoader.put(name,type);
        return true;
    }

    /**
     * 根据名称获取class对象
     * @param name 类名称
     * @return class对象，不存在会返回null
     */
    public static Class getClassByName(String name){
        return classLoader.get(name);
    }

    /**
     * 卸载已注册的类
     * @param name 类名
     * @return 如果不存在会返回false
     */
    public static boolean unregisterClass(String name){
        if(classLoader==null || !classLoader.containsKey(name))
            return false;
        classLoader.remove(name);
        return true;
    }

    /**
     * 根据class对象获取类名称
     * 只会返回最先找到的那个
     * @param type class对象
     * @return 第一个找到的类名称
     */
    public static String getNameByType(Class type){
        Set<String> keySet=classLoader.keySet();
        for(String key:keySet){
            Class cls=classLoader.get(key);
            if(type.equals(cls))
                return key;
        }

        return null;
    }

    /**
     * 根据类的常量来注册类
     * 会将一个类中的 名字以 NAME_开头的string类型常量作为名称，注册该类
     * @param clazz 待注册的类对象
     * @return 是否成功
     */
    public static boolean registerClassWithFields(Class clazz){

        try {
            Field[] fields=clazz.getDeclaredFields();
            for(Field field:fields){
                if(!field.getType().equals(String.class))
                    continue;

                if(!field.getName().startsWith("NAME_"))
                    continue;

                int mod=field.getModifiers();
                if(Modifier.isStatic(mod) && Modifier.isFinal(mod)){
                    registerClass((String) field.get(null),clazz);
                    //registerClass(ActionReport.getActionReportName((String) field.get(null)),ActionReport.class);
                }
            }

            return true;
        }catch (Exception e){
            log.error("很不幸的，类注册失败，当前类\n"+(clazz!=null?clazz.getSimpleName():clazz),e);
            return false;
        }

    }

}
