package com.tss.notifications;

import com.tss.entity.Order;

public interface Observer {
    void update(Order order);
}
