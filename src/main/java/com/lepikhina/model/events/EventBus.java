package com.lepikhina.model.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

public class EventBus {

    Map<Class<? extends AppEvent>, List<ListenerMethod>> subscribers;

    private static EventBus instance;

    private EventBus() {
        subscribers = new HashMap<>();
    }

    public synchronized static EventBus getInstance() {
        if (instance == null) {
            instance =  new EventBus();
        }

        return instance;
    }

    public void addListener(Object listener) {
        Method[] listenerMethods = listener.getClass().getMethods();
        for (Method listenerMethod : listenerMethods) {
            EventListener listenerAnnotation = listenerMethod.getAnnotation(EventListener.class);
            if (listenerAnnotation != null) {
                List<ListenerMethod> eventListeners =
                        subscribers.computeIfAbsent(listenerAnnotation.value(), aClass -> new ArrayList<>());
                eventListeners.add(new ListenerMethod(listener, listenerMethod));
            }
        }
    }

    @SneakyThrows
    public static void sendEvent(AppEvent event) {
        EventBus eventBus = getInstance();
        List<ListenerMethod> listeners = eventBus.subscribers.get(event.getClass());

        for (ListenerMethod listenerMethod : listeners) {
            Method method = listenerMethod.getMethod();
            method.setAccessible(true);
            method.invoke(listenerMethod.target, event);
        }
    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class ListenerMethod {
        Object target;

        Method method;
    }
}
