package com.qzero.exchange.test;

import com.alibaba.fastjson.JSON;
import com.qzero.exchange.core.QExchangeParameter;
import com.qzero.exchange.core.coder.QExchangeParameterField;
import com.qzero.exchange.core.utils.UUIDUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.*;

public class TestJSONCoder {

    private static final Logger log=Logger.getRootLogger();

    @Test
    public void testJsonEncode() throws Exception{
        TestBeanB b=new TestBeanB(2,"Ming");

        TestBeanB b1=new TestBeanB(21,"Ming1");
        TestBeanB b2=new TestBeanB(22,"Ming2");
        List<TestBeanB> testBeanBS=new LinkedList<>();
        testBeanBS.add(b1);
        testBeanBS.add(b2);

        TestBeanA a=new TestBeanA(UUIDUtils.getRandomUUID(),25,b);
        a.list=testBeanBS;
        a.ae= TestBeanA.AE.AE_B;
        a.is=new int[]{1,2,3,4};

        Map map=encodeParameter(a);
        log.debug(map);
        log.debug(decodeParameter(map,TestBeanA.class));
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

}
