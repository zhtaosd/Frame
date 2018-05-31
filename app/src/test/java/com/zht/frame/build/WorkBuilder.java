package com.zht.frame.build;

public class WorkBuilder implements Build{
     private Room room = new Room();

    @Override
    public void buildWindow() {
        room.setWindow("添加窗户");
    }

    @Override
    public void buildDoor() {
        room.setDoor("添加门");
    }

    @Override
    public Room build() {
        return room;
    }
}
