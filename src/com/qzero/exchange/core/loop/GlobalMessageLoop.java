package com.qzero.exchange.core.loop;

import com.qzero.exchange.core.coder.GlobalClassLoader;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalMessageLoop {

    private static Map<String,ListenerSet> listeners=new HashMap<>();

    public static final int PRIORITY_SYSTEM_LEVEL_URGENT=1;
    public static final int PRIORITY_SYSTEM_LEVEL=2;
    public static final int PRIORITY_USER_LEVEL_URGENT=3;
    public static final int PRIORITY_USER_LEVEL=4;

    public static boolean registerListener(String target,IQExchangeListener listener){
        ListenerSet listenerSet=listeners.get(target);
        if(listenerSet==null)
            listenerSet=new ListenerSet();

        if(listenerSet.add(listener)){
            listeners.put(target,listenerSet);
            return true;
        }else
            return false;

    }

    public static boolean unregisterListener(String target,String id,int priority){
        ListenerSet listenerSet=listeners.get(target);
        if(listenerSet==null)
            return false;
        if(listenerSet.remove(priority,id)){
            listeners.put(target,listenerSet);
            return true;
        }else
            return false;
    }

    public static boolean registerProcessClass(Class cls,Object instance){
        try {
            Method[] methods=cls.getDeclaredMethods();
            for(Method method:methods){
                method.setAccessible(true);
                QExchangeListener listenerAnnotation=method.getAnnotation(QExchangeListener.class);
                if(listenerAnnotation!=null){
                    Class[] targets=listenerAnnotation.targets();
                    int priority=listenerAnnotation.priority();

                    for(Class target:targets){
                        String targetName= GlobalClassLoader.getNameByType(target);
                        if(targetName==null)
                            return false;

                        IQExchangeListener listener=new IQExchangeListener() {
                            @Override
                            public String getId() {
                                return method.getName()+"-"+targetName;
                            }

                            @Override
                            public int getPriority() {
                                return priority;
                            }

                            @Override
                            public boolean onObjectReceived(Object obj) {
                                try {
                                    return (boolean) method.invoke(instance,obj);
                                }catch (Exception e){
                                    return false;
                                }

                            }
                        };

                        if(!registerListener(targetName,listener))
                            return false;
                    }
                }
            }

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static void unregisterProcessClass(Class cls){
        try {
            Method[] methods=cls.getDeclaredMethods();

            for(Method method:methods){
                method.setAccessible(true);
                QExchangeListener listenerAnnotation=method.getAnnotation(QExchangeListener.class);
                if(listenerAnnotation!=null){
                    Class[] targets=listenerAnnotation.targets();
                    int priority=listenerAnnotation.priority();

                    for(Class target:targets){
                        String targetName= GlobalClassLoader.getNameByType(target);
                        if(targetName==null)
                            continue;

                        if(!unregisterListener(targetName,method.getName()+"-"+targetName,priority))
                            continue;
                    }

                }
            }

        }catch (Exception e){

        }
    }

    public static void onObjectReceived(Object obj){
        if(obj==null)
            return;

        String name=GlobalClassLoader.getNameByType(obj.getClass());
        if(name==null)
            return;

        ListenerSet listenerSet=listeners.get(name);
        if(listenerSet==null)
            return;

        listenerSet.onReceived(obj);
    }

}

class ListenerSet{

    private Map<Integer, List<IQExchangeListener>> listenerMap=new HashMap<>();

    public boolean add(IQExchangeListener listener){
        int priority=listener.getPriority();
        String id=listener.getId();

        List<IQExchangeListener> listenerList=new ArrayList<>();
        if(listenerMap.containsKey(priority))
            listenerList=listenerMap.get(priority);

        for(IQExchangeListener listener1:listenerList){
            if(listener1.getId().equals(id))
                return false;
        }

        listenerList.add(listener);
        listenerMap.put(priority,listenerList);
        return true;
    }

    public boolean remove(int priority,String id){
        if(!listenerMap.containsKey(priority))
            return false;

        List<IQExchangeListener> listenerList=listenerMap.get(priority);
        for(int i=0;i<listenerList.size();i++){
            IQExchangeListener listener=listenerList.get(i);
            if(listener.getId().equals(id)){
                listenerList.remove(i);
                listenerMap.put(priority,listenerList);
                return true;
            }
        }

        return false;
    }

    public boolean onReceived(Object obj){
        for(int i=1;i<=10;i++){
            List<IQExchangeListener> listenerList=listenerMap.get(i);
            if(listenerList!=null){
                for(IQExchangeListener listener:listenerList){
                    if(!listener.onObjectReceived(obj))
                        return false;
                }
            }
        }

        return true;
    }

}