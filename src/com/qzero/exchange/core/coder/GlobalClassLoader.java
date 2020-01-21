package com.qzero.exchange.core.coder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GlobalClassLoader {

    private static Map<String,Class> classLoader=new HashMap<>();

    public static boolean registerClass(String name,Class type){
        if(type==null || classLoader==null || classLoader.containsKey(name))
            return false;
        classLoader.put(name,type);
        return true;
    }

    public static Class getClassByName(String name){
        return classLoader.get(name);
    }

    public static boolean unregisterClass(String name){
        if(classLoader==null || !classLoader.containsKey(name))
            return false;
        classLoader.remove(name);
        return true;
    }

    public static String getNameByType(Class type){
        Set<String> keySet=classLoader.keySet();
        for(String key:keySet){
            Class cls=classLoader.get(key);
            if(type.equals(cls))
                return key;
        }

        return null;
    }

}
