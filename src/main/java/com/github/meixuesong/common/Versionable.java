package com.github.meixuesong.common;

public interface Versionable {
    int NEW_VERSION = 0;
    int getVersion();
    void increaseVersion();
}
