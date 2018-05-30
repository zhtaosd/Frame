package com.zht.frame.export;

public class FileFactory extends Factory{
    @Override
    public Api factoryMethod() {
        return new FileImp();
    }
}
