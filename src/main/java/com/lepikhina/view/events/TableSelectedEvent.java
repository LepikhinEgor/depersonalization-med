package com.lepikhina.view.events;

import com.lepikhina.model.data.DbTable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TableSelectedEvent extends AppEvent {

    private final DbTable dbTable;

    @Override
    protected String getName() {
        return "table-selected";
    }
}
