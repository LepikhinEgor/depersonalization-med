package com.lepikhina.view.events;

import com.lepikhina.model.data.DbColumn;
import lombok.Getter;

@Getter
public class ColumnSelectedEvent extends AppEvent {

    private DbColumn dbColumn;

    public ColumnSelectedEvent(DbColumn column) {
        this.dbColumn = column;
    }

    @Override
    protected String getName() {
        return "column-selected";
    }
}
