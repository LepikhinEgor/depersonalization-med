package com.lepikhina.view.events;

import com.lepikhina.model.data.DepersonalizationColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ActionChangedEvent extends AppEvent{

    DepersonalizationColumn newAction;

    @Override
    protected String getName() {
        return "action-changed";
    }
}
