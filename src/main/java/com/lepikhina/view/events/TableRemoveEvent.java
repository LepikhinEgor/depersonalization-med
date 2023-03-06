package com.lepikhina.view.events;

import com.lepikhina.model.data.DbTable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TableRemoveEvent extends AppEvent {

    private DbTable dbTable;

    @Override
    protected String getName() {
        return "table-remove";
    }
}
