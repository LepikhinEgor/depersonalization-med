package com.lepikhina.model.events;

import java.util.EventListener;

public interface AppEventListener<T> extends EventListener {

    void onEvent(T event);

    @SuppressWarnings("unchecked")
    default void onEvent(AppEvent event) {
        onEvent((T)event);
    };

}
