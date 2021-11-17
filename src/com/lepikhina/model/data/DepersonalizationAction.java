package com.lepikhina.model.data;

public abstract class DepersonalizationAction<T> {

    public abstract T depersonalize(T inputData);

    public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }
}
