package com.lepikhina.model.events;

public class DBDisconnectEvent extends AppEvent{

    @Override
    protected String getName() {
        return "db-disconnect-event";
    }
}