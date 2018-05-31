package com.zht.frame.nodebuild;

public class WorkBuilder {
    private RoomParams roomParams = new RoomParams();
    //需要一个内部类对相关的属性进行镜像
    class RoomParams{
        public  String window;
        public  String door;
    }

    public WorkBuilder buildWindow(String window){
        roomParams.window = window;
        return this;
    }

    public WorkBuilder buildDoor(String door){
        roomParams.door = door;
        return this;
    }

    public Room build(){
        Room room = new Room();
        room.apply(roomParams);
        return room;
    }
}
