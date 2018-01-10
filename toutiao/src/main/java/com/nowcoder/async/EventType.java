package com.nowcoder.async;

public enum EventType {
    LIKE(0),        //以下为4个枚举的事件,可能发生的事情
    COMMENT(1),
    LOGIN(2),
    MAIL(3);

    private int value;
    EventType(int value){
        this.value=value;
    }
    public int getValue(){
        return value;
    }
}
