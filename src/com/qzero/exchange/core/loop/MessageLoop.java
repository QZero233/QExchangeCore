package com.qzero.exchange.core.loop;

import com.qzero.exchange.core.GlobalClassLoader;
import com.qzero.exchange.core.PackedObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息循环池
 */
public class MessageLoop {


    /**
     * 保存有所有监听器的一个Map对象
     * 其中key为监听对象的名称,value为监听器集合
     */
    private Map<String, ListenerSet> listeners = new HashMap<>();

    /**
     * 四种不同的优先级
     */
    public static final int PRIORITY_SYSTEM_LEVEL_URGENT = 1;
    public static final int PRIORITY_SYSTEM_LEVEL = 2;
    public static final int PRIORITY_USER_LEVEL_URGENT = 3;
    public static final int PRIORITY_USER_LEVEL = 4;

    /**
     * 注册一个监听器
     * @param target 对象的名称
     * @param listener 监听器
     * @return 是否成功
     */
    public boolean registerListener(String target, IQExchangeListener listener) {
        ListenerSet listenerSet = listeners.get(target);
        if (listenerSet == null)
            listenerSet = new ListenerSet();

        if (listenerSet.add(listener)) {
            listeners.put(target, listenerSet);
            return true;
        } else
            return false;

    }

    /**
     * 取消一个监听器
     * @param target 对象名称
     * @param id 监听器ID
     * @param priority 监听器优先级
     * @return
     */
    public boolean unregisterListener(String target, String id, int priority) {
        ListenerSet listenerSet = listeners.get(target);
        if (listenerSet == null)
            return false;
        if (listenerSet.remove(priority, id)) {
            listeners.put(target, listenerSet);
            return true;
        } else
            return false;
    }

    /**
     * 注册处理类
     * 也就是注册一个类中所有带有QExchangeListener注解的方法，将其视为一个监听器
     * @param cls 处理类
     * @param instance 处理类的对象
     * @return
     */
    public boolean registerProcessClass(Class cls, Object instance) {
        try {
            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                method.setAccessible(true);
                QExchangeListener listenerAnnotation = method.getAnnotation(QExchangeListener.class);
                if (listenerAnnotation != null) {

                    Class[] targetClass = listenerAnnotation.targetsClass();
                    String[] targetsName = listenerAnnotation.targetsName();

                    if (targetClass.length == 0 && targetsName.length == 0)
                        continue;

                    Object[] targets;
                    if (targetsName.length == 0)
                        targets = targetClass;
                    else
                        targets = targetsName;

                    int priority = listenerAnnotation.priority();

                    for (Object target : targets) {
                        String targetName;
                        if (target instanceof String)
                            targetName = (String) target;
                        else
                            targetName = GlobalClassLoader.getNameByType((Class) target);

                        if (targetName == null)
                            continue;

                        IQExchangeListener listener = new IQExchangeListener() {
                            @Override
                            public String getId() {
                                return method.getName() + "-" + targetName;
                            }

                            @Override
                            public int getPriority() {
                                return priority;
                            }

                            @Override
                            public boolean onObjectReceived(Object obj) {
                                try {
                                    return (boolean) method.invoke(instance, obj);
                                } catch (Exception e) {
                                    return false;
                                }

                            }
                        };

                        if (!registerListener(targetName, listener))
                            return false;
                    }
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 卸载一个注册类的监听器
     * @param cls 注册类
     */
    public void unregisterProcessClass(Class cls) {
        try {
            Method[] methods = cls.getDeclaredMethods();

            for (Method method : methods) {
                method.setAccessible(true);
                QExchangeListener listenerAnnotation = method.getAnnotation(QExchangeListener.class);
                if (listenerAnnotation != null) {
                    Class[] targetClass = listenerAnnotation.targetsClass();
                    String[] targetsName = listenerAnnotation.targetsName();

                    if (targetClass.length == 0 && targetsName.length == 0)
                        continue;

                    Object[] targets;
                    if (targetsName.length == 0)
                        targets = targetClass;
                    else
                        targets = targetsName;

                    int priority = listenerAnnotation.priority();

                    for (Object target : targets) {
                        String targetName;
                        if (target instanceof String)
                            targetName = (String) target;
                        else
                            targetName = GlobalClassLoader.getNameByType((Class) target);

                        if (targetName == null)
                            continue;

                        if (!unregisterListener(targetName, method.getName() + "-" + targetName, priority))
                            continue;
                    }

                }
            }

        } catch (Exception e) {

        }
    }

    /**
     * 接受到对象，调用此方法会调用相应的监听方法
     * @param packedObject 收到的对象
     * @return 是否成功
     */
    public boolean onObjectReceived(PackedObject packedObject) {
        Object obj = packedObject.getObject();
        String name = packedObject.getName();
        if (obj == null || name == null)
            return false;

        ListenerSet listenerSet = listeners.get(name);
        if (listenerSet == null)
            return false;

        return listenerSet.onReceived(obj);
    }

}

/**
 * 监听器集合
 */
class ListenerSet {

    /**
     * 保存有所有监听器的一个map对象
     * Key为优先级，value为监听器列表
     */
    private Map<Integer, List<IQExchangeListener>> listenerMap=new HashMap<>();

    /**
     * 添加一个监听器
     * @param listener 监听器对象
     * @return 如果含义该ID且优先级相同的监听器存在会返回false
     */
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

    /**
     * 卸载一个监听器
     * @param priority 优先级
     * @param id 监听器ID
     * @return 监听器不存在会返回false
     */
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

    /**
     * 按照优先级从小到大的顺序调用监听方法
     * 如果有一个监听方法返回false将停止调用接下来的监听器
     * @param obj 收到的对象
     * @return 是否成功
     */
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