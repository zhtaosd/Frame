package com.zht.frame.simplefctory;

public class SimpleFactory {
    public static Api creat(int type) {
        switch (type) {
            case 1:
                return new ImpA();
            case 2:
                return new ImpB();
            case 3:
                return new ImpC();
            default:
                return new ImpA();
        }
    }
    public static  <T extends Api> T creatProduct(Class<T> clz){
        Api api  = null;
        try {
            api = (Api) Class.forName(clz.getName()).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (T) api;
    }
}
