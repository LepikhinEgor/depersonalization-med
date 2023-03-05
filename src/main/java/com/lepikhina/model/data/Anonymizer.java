package com.lepikhina.model.data;

import java.util.List;

public interface Anonymizer {

    <T> List<TableRow<T>> anonymize(List<TableRow<T>> oldValues);

    <T> List<TableRow<T>> generate(Integer count);
}
