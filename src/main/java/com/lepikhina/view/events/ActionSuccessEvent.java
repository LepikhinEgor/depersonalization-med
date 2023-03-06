package com.lepikhina.view.events;

import com.lepikhina.model.data.DepersonalizationColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActionSuccessEvent extends AppEvent {

    DepersonalizationColumn column;

    @Override
    protected String getName() {
        return "action-success";
    }
}
