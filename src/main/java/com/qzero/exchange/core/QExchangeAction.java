package com.qzero.exchange.core;

import com.qzero.exchange.core.coder.ParameterCoder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class QExchangeAction implements Serializable {

    protected String actionId;
    protected String actionName;
    protected Map<String, QExchangeParameter> parameterMap;

    public enum ActionType{
        ACTION_TYPE_REQUEST,
        ACTION_TYPE_RESPONSE,
    }

    public QExchangeAction(String actionId, String actionName, Map<String, QExchangeParameter> parameterMap) {
        this.actionId = actionId;
        this.actionName = actionName;
        this.parameterMap = parameterMap;
    }

    public abstract ActionType getActionType();

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Map<String, QExchangeParameter> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, QExchangeParameter> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public QExchangeParameter getParameterByName(String name){
        if(parameterMap==null || parameterMap.isEmpty())
            return null;

        return parameterMap.get(name);
    }

    public<T> T getParameterInObject(Class<T> clazz){
        ParameterCoder coder=new ParameterCoder();
        return coder.decodeParameter(parameterMap,clazz);
    }

    public void addParameterInObject(Object obj){
        ParameterCoder coder=new ParameterCoder();
        Map map=coder.encodeParameter(obj);
        if(parameterMap==null)
            parameterMap=new HashMap<>();
        parameterMap.putAll(map);
    }

    @Override
    public String toString() {
        return "QExchangeAction{" +
                "actionId='" + actionId + '\'' +
                ", actionName='" + actionName + '\'' +
                ", parameterMap=" + parameterMap +
                '}';
    }
}
