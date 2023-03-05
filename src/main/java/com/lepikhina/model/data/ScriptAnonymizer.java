package com.lepikhina.model.data;

import com.lepikhina.ScriptsExecutor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScriptAnonymizer implements Anonymizer {

    String scriptPath;
    HashMap<String, Object> variables;

    @Override
    public <T> List<TableRow<T>> anonymize(List<TableRow<T>> oldValues) {
        List<T> values = oldValues.stream()
                .map(TableRow::getValue)
                .collect(Collectors.toList());

        variables.put("oldValues", values);
        List<T> newValues = ScriptsExecutor.executeScript(scriptPath, variables);

        if (newValues.size() != values.size()) {
            throw new RuntimeException("Не для всех рядов были добавлены значения");
        }

        List<TableRow<T>> updatedRows = new ArrayList<>();
        for (int i = 0; i < oldValues.size(); i++) {
            updatedRows.add(new TableRow<>(newValues.get(i), oldValues.get(i).getIds()));
        }

        return updatedRows;
    }

    @Override
    public <T> List<TableRow<T>> generate(Integer count) {
        List<Object> oldValues = new ArrayList<>(); //ПРидумать дефолтные значения для каждого типа
        for (int i = 0; i < count; i++) {
            oldValues.add(new Object());
        }
        variables.put("oldValues", oldValues);

        List<T> newValues = ScriptsExecutor.executeScript(scriptPath, variables);

        return newValues.stream()
                .map(newValue -> new TableRow<>(newValue, null))
                .collect(Collectors.toList());
    }
}
