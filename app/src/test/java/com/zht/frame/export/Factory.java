package com.zht.frame.export;


//顶层的工厂类
public abstract class Factory {
    //抽象出一个创建实例的方法
    public abstract  Api factoryMethod();

    //到处所需内容的方法
    public void export(String data){
        Api api = factoryMethod();
        api.export(data);
    }
}
