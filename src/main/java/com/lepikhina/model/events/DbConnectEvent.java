package com.lepikhina.model.events;

public class DbConnectEvent extends AppEvent{

    @Override
    protected String getName() {
        return "db-connect-event";
    }
}
