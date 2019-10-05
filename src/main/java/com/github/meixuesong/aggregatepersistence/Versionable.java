package com.github.meixuesong.aggregatepersistence;

public interface Versionable {
    int NEW_VERSION = 0;
    int getVersion();
}
