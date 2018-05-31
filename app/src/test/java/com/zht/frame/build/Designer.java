package com.zht.frame.build;

public class Designer {
    public Room build(Build build){
        build.buildWindow();
        build.buildDoor();
        return build.build();
    }
}
