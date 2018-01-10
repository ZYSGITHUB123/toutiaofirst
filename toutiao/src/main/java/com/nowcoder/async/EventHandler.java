package com.nowcoder.async;

import java.util.List;

public interface EventHandler {
    void doHandle(EventModel model);   //处理这个model
    List<EventType> getSupportEventTypes();   //关注某些eventType
}
