package com.github.meixuesong.aggregatepersistence;

public interface DeepCopier {
    public <T> T copy(T object);
}
