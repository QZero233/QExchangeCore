package com.qzero.exchange.core;

/**
 * 封装了的对象
 * 包含对象类的名称以及对象本体
 */
public class PackedObject {

    /**
     * 对象类名称，可以称为类名称或对象名称
     * 用于告知另一端该对象的类别以及作用
     */
    private String name;
    /**
     * 对象本体
     */
    private Object object;

    public PackedObject() {
    }

    public PackedObject(Object object) throws IllegalArgumentException {
        this.object = object;
        name= GlobalClassLoader.getNameByType(object.getClass());
        if(name==null)
            throw new IllegalArgumentException("不能找到名称为t"+object.getClass().getSimpleName()+"的类，请注册");
    }

    public PackedObject(String name, Object object) {
        this.name = name;
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "PackedObject{" +
                "name='" + name + '\'' +
                ", object=" + object +
                '}';
    }

}
