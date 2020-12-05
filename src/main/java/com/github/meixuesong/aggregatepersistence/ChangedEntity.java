package com.github.meixuesong.aggregatepersistence;

/**
 * ChangedEntity
 * @author meixuesong
 */
public class ChangedEntity<T> {

    private final T oldEntity;
    private final T newEntity;

    public ChangedEntity(T oldEntity, T newEntity) {

        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
    }

    public T getOldEntity() {
        return oldEntity;
    }

    public T getNewEntity() {
        return newEntity;
    }
}
