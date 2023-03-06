package com.lepikhina.view.events;

public class DBDisconnectEvent extends AppEvent{

    @Override
    protected String getName() {
        return "db-disconnect-event";
    }
}