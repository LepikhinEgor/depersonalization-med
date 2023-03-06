package com.lepikhina.view.events;

import com.lepikhina.model.data.DbColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ColumnRemoveEvent extends AppEvent  {

    private DbColumn dbColumn;

    @Override
    protected String getName() {
        return "column-remove";
    }
}
