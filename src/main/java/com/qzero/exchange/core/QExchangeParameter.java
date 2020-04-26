package com.qzero.exchange.core;

import java.io.Serializable;
import java.util.Map;

public class QExchangeParameter implements Serializable {

    public enum ParameterType{
        PARAMETER_TYPE_NULL,
        PARAMETER_TYPE_STRING,
        PARAMETER_TYPE_INT,
        PARAMETER_TYPE_SHORT,
        PARAMETER_TYPE_LONG,
        PARAMETER_TYPE_BYTE,
        PARAMETER_TYPE_BOOLEAN,
        PARAMETER_TYPE_FLOAT,
        PARAMETER_TYPE_DOUBLE,
        PARAMETER_TYPE_CHAR,
        PARAMETER_TYPE_ENUM,

        /**
         * 实现了Serializable的就直接变成byte[]
         * 没实现Serializable的Object类型的用一个Map封装，用ParameterCoder转化为Map
         */
        PARAMETER_TYPE_OBJECT

    }

    private String parameterName;
    private ParameterType parameterType;
    private Object parameterObject;

    public QExchangeParameter() {
    }

    public QExchangeParameter(String parameterName, Object parameterObject) {
        this.parameterName = parameterName;
        this.parameterObject = parameterObject;
        parameterType=getParameterTypeByObject(parameterObject);
        if(parameterType==null)
            throw new IllegalArgumentException("错误，未知参数类型"+parameterObject.getClass().getName());
    }

    public QExchangeParameter(String parameterName, ParameterType parameterType, Object parameterObject) {
        this.parameterName = parameterName;
        this.parameterType = parameterType;
        this.parameterObject = parameterObject;
        if(!checkType())
            throw new IllegalArgumentException("错误，参数类型本应为"+parameterType+
                    "，但实际为"+((parameterObject==null)?"null":parameterObject.getClass().getName()));
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public ParameterType getParameterType() {
        return parameterType;
    }

    public void setParameterType(ParameterType parameterType) {
        this.parameterType = parameterType;
        if(!checkType())
            throw new IllegalArgumentException("错误，参数类型本应为"+parameterType+
                    "，但实际为"+((parameterObject==null)?"null":parameterObject.getClass().getName()));
    }

    public Object getParameterObject() {
        return parameterObject;
    }

    public void setParameterObject(Object parameterObject) {
        this.parameterObject = parameterObject;
        if(!checkType())
            throw new IllegalArgumentException("错误，参数类型本应为"+parameterType+
                    "，但实际为"+((parameterObject==null)?"null":parameterObject.getClass().getName()));
    }

    public static ParameterType getParameterTypeByObject(Object obj){
        if(obj==null)
            return ParameterType.PARAMETER_TYPE_NULL;
        else if(obj instanceof Integer)
            return ParameterType.PARAMETER_TYPE_INT;
        else if(obj instanceof Short)
            return ParameterType.PARAMETER_TYPE_SHORT;
        else if(obj instanceof Long)
            return ParameterType.PARAMETER_TYPE_LONG;
        else if(obj instanceof Byte)
            return ParameterType.PARAMETER_TYPE_BYTE;
        else if(obj instanceof Double)
            return ParameterType.PARAMETER_TYPE_DOUBLE;
        else if(obj instanceof Float)
            return ParameterType.PARAMETER_TYPE_FLOAT;
        else if(obj instanceof Character)
            return ParameterType.PARAMETER_TYPE_CHAR;
        else if(obj instanceof Boolean)
            return ParameterType.PARAMETER_TYPE_BOOLEAN;
        else if(obj instanceof String)
            return ParameterType.PARAMETER_TYPE_STRING;
        else if(obj instanceof Enum)
            return ParameterType.PARAMETER_TYPE_ENUM;
        else
            return ParameterType.PARAMETER_TYPE_OBJECT;
    }

    private boolean checkType(){
        if(parameterType==null && parameterObject!=null)
            return false;
        if(parameterType==null && parameterObject==null)
            return true;

        switch (parameterType){
            case PARAMETER_TYPE_NULL:
                return parameterObject==null;
            case PARAMETER_TYPE_INT:
                return parameterObject instanceof Integer;
            case PARAMETER_TYPE_BYTE:
                return parameterObject instanceof Byte;
            case PARAMETER_TYPE_CHAR:
                return parameterObject instanceof Character;
            case PARAMETER_TYPE_LONG:
                return parameterObject instanceof Long;
            case PARAMETER_TYPE_FLOAT:
                return parameterObject instanceof Float;
            case PARAMETER_TYPE_SHORT:
                return parameterObject instanceof Short;
            case PARAMETER_TYPE_DOUBLE:
                return parameterObject instanceof Double;
            case PARAMETER_TYPE_BOOLEAN:
                return parameterObject instanceof Boolean;
            case PARAMETER_TYPE_STRING:
                return parameterObject instanceof String;
            case PARAMETER_TYPE_ENUM:
                return parameterObject instanceof Enum;
            case PARAMETER_TYPE_OBJECT:
                return (parameterObject instanceof Map) | (parameterObject instanceof byte[]);
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "IQExchangeParameter{" +
                "parameterName='" + parameterName + '\'' +
                ", parameterType=" + parameterType +
                ", parameterObject=" + parameterObject +
                '}';
    }
}
