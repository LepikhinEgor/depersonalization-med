package com.lepikhina.model.events;

import com.lepikhina.model.data.DepersonalizationColumn;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ActionFailEvent extends AppEvent {

    DepersonalizationColumn column;

    @Override
    protected String getName() {
        return "fail-success";
    }
}