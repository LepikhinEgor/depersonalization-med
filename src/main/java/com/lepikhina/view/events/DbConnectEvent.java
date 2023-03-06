package com.lepikhina.view.events;

public class DbConnectEvent extends AppEvent{

    @Override
    protected String getName() {
        return "db-connect-event";
    }
}
