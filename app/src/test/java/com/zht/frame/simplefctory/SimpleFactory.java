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
}
