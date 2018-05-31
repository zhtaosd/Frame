package com.zht.frame.nodebuild;

public class Room {
    private String window;
    private String door;

    public void apply(WorkBuilder.RoomParams params){
         this.window =  params.window;
         this.door = params.door;
    }

    @Override
    public String toString() {
        System.out.println(window+"/n"+door);
        return window+"/n"+door;
    }
}
