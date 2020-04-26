package com.qzero.exchange.core.loop;

import com.qzero.exchange.core.QExchangeAction;

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
     * @param listener 监听器
     * @return 是否成功
     */
    public boolean registerListener(IQExchangeListener listener) {
        if(listener==null)
            return false;

        String actionName=listener.getActionName();
        if(actionName==null || actionName.equals(""))
            return false;

        ListenerSet listenerSet = listeners.get(actionName);
        if (listenerSet == null)
            listenerSet = new ListenerSet();

        if (listenerSet.add(listener)) {
            listeners.put(actionName, listenerSet);
            return true;
        } else
            return false;

    }

    /**
     * 取消一个监听器
     * @param actionName 监听的动作名称
     * @param id 监听器ID
     * @param priority 监听器优先级
     * @return
     */
    public boolean unregisterListener(String actionName, String id, int priority) {
        ListenerSet listenerSet = listeners.get(actionName);
        if (listenerSet == null)
            return false;
        if (listenerSet.remove(priority, id)) {
            listeners.put(actionName, listenerSet);
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

                    String[] actionNameList=listenerAnnotation.actionNameList();
                    if(actionNameList==null || actionNameList.length==0)
                        continue;

                    int priority = listenerAnnotation.priority();

                    for (String actionName : actionNameList) {
                        if(actionName==null)
                            continue;

                        IQExchangeListener listener = new IQExchangeListener() {
                            @Override
                            public String getId() {
                                return cls.getName()+"-"+method.getName() + "-" + actionName;
                            }

                            @Override
                            public int getPriority() {
                                return priority;
                            }

                            @Override
                            public String getActionName() {
                                return actionName;
                            }

                            @Override
                            public QExchangeAction.ActionType getActionType() {
                                return listenerAnnotation.actionType();
                            }

                            @Override
                            public boolean onObjectReceived(QExchangeAction action) {
                                try {
                                    return (boolean) method.invoke(instance, action);
                                } catch (Exception e) {
                                    return false;
                                }
                            }
                        };

                        if (!registerListener(listener))
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
                    String[] actionNameList=listenerAnnotation.actionNameList();
                    if(actionNameList==null || actionNameList.length==0)
                        continue;

                    int priority = listenerAnnotation.priority();


                    for (String actionName : actionNameList) {
                        if (actionName == null)
                            continue;

                        String id=cls.getName()+"-"+method.getName() + "-" + actionName;

                        if (!unregisterListener(actionName, id, priority))
                            continue;
                    }

                }
            }

        } catch (Exception e) {

        }
    }

    /**
     * 接受到操作，调用此方法会调用相应的监听方法
     * @param action 收到的操作
     * @return 是否成功
     */
    public boolean onActionReceived(QExchangeAction action) {
        String actionName=action.getActionName();
        ListenerSet listenerSet = listeners.get(actionName);
        if (listenerSet == null)
            return false;

        return listenerSet.onReceived(action);
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
     * @param action 收到的操作
     * @return 是否成功
     */
    public boolean onReceived(QExchangeAction action){
        for(int i=1;i<=10;i++){
            List<IQExchangeListener> listenerList=listenerMap.get(i);
            if(listenerList!=null){
                for(IQExchangeListener listener:listenerList){
                    QExchangeAction.ActionType type=action.getActionType();
                    if(listener.getActionType()==null || listener.getActionType().equals(type)){
                        if(!listener.onObjectReceived(action))
                            return false;
                    }else{
                        continue;
                    }

                }
            }
        }

        return true;
    }

}