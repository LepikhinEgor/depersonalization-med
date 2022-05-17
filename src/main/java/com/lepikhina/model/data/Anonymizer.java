package com.lepikhina.model.data;

import java.util.List;

public interface Anonymizer {

    <T> List<TableRow<T>> anonymize(List<TableRow<T>> oldValues);
}
