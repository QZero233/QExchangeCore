package com.qzero.exchange.core.coder;

import com.alibaba.fastjson.JSON;
import com.qzero.exchange.core.QExchangeParameter;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ParameterCoder {

    private static final Logger log=Logger.getRootLogger();

    private boolean isSerializable(Class clazz){
        Type[] types=clazz.getGenericInterfaces();
        for(Type type:types){
            if(type.getTypeName().equals("java.io.Serializable"))
                return true;
        }

        return false;
    }

    private byte[] objectToByteArray(Object obj){
        try {
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            ObjectOutputStream outputStream=new ObjectOutputStream(byteArrayOutputStream);

            outputStream.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            log.error("可序列化对象的序列化失败",e);
            return null;
        }
    }

    private<T> T byteArrayToSerializableObject(byte[] buf){
        try {
            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(buf);
            ObjectInputStream inputStream=new ObjectInputStream(byteArrayInputStream);

            return (T) inputStream.readObject();
        }catch (Exception e){
            log.error("可序列化对象反序列化失败",e);
            return null;
        }
    }

    public Map<String, QExchangeParameter> encodeParameter(Object obj){
        if(obj==null)
            return null;

        Class clazz=obj.getClass();

        Map<String,QExchangeParameter> parameterMap=new HashMap<>();

        Field[] fields=clazz.getDeclaredFields();
        for(Field field:fields){
            field.setAccessible(true);

            QExchangeParameterField fieldAnnotation=field.getAnnotation(QExchangeParameterField.class);
            if(fieldAnnotation==null || !fieldAnnotation.enabled())
                continue;

            String name=fieldAnnotation.name();
            if(name==null || name.equals(""))
                name=field.getName();

            Object value;
            try {
                value=field.get(obj);
            } catch (IllegalAccessException e) {
                log.error("获取参数类的内容时异常",e);
                return null;
            }

            if(value==null && !fieldAnnotation.optional()){
                log.error(String.format("错误，参数%s是必须参数，不能为空", name));
                return null;
            }


            QExchangeParameter.ParameterType parameterType=QExchangeParameter.getParameterTypeByObject(value);
            if(parameterType== QExchangeParameter.ParameterType.PARAMETER_TYPE_OBJECT){
                value= JSON.toJSONString(value);
            }

            QExchangeParameter parameter;
            try {
                parameter=new QExchangeParameter(name,parameterType,value);
            }catch (IllegalArgumentException e){
                log.error(String.format("参数%s类型异常", name),e);
                return null;
            }

            parameterMap.put(name,parameter);
        }

        return parameterMap;
    }

    public<T> T decodeParameter(Map<String, QExchangeParameter> parameterMap,Class<T> clazz){
        if(parameterMap==null || clazz==null)
            return null;

        T instance;
        try {
            instance=clazz.getConstructor(new Class[]{}).newInstance();
        } catch (Exception e){
            log.error("错误，新建实例对象失败",e);
            return null;
        }

        Field[] fields=clazz.getDeclaredFields();
        for(Field field:fields) {
            field.setAccessible(true);

            QExchangeParameterField fieldAnnotation = field.getAnnotation(QExchangeParameterField.class);
            if (fieldAnnotation == null || !fieldAnnotation.enabled())
                continue;

            String name = fieldAnnotation.name();
            if (name == null || name.equals(""))
                name=field.getName();

            QExchangeParameter parameter=parameterMap.get(name);


            if(parameter==null && !fieldAnnotation.optional()){
                log.error(String.format("错误，参数%s为必须参数", name));
                return null;
            }else if(parameter==null){
                continue;
            }

            Object value=parameter.getParameterObject();

            if(parameter.getParameterType()== QExchangeParameter.ParameterType.PARAMETER_TYPE_OBJECT){
                value=JSON.parseObject((String) value,field.getType());
            }

            try {
                field.set(instance,value);
            } catch (IllegalAccessException e) {
                log.error(String.format("填充参数%s时异常", name));
                return null;
            }
        }

        return instance;
    }
}
