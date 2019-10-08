package com.github.meixuesong.aggregatepersistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Aggregate<R extends Versionable> {
    protected R root;
    protected R snapshot;
    protected DeepComparator deepComparator;

    Aggregate(R root, DeepCopier copier, DeepComparator deepComparator) {
        this.root = root;
        this.snapshot = copier.copy(root);
        this.deepComparator = deepComparator;
    }

    public R getRoot() {
        return root;
    }

    public R getRootSnapshot() {
        return snapshot;
    }

    public boolean isChanged() {
        return !deepComparator.isDeepEquals(root, snapshot);
    }

    public boolean isNew() {
        return root.getVersion() == Versionable.NEW_VERSION;
    }

    public <T> Collection<T> findNewEntities(Function<R, Collection<T>> getCollection, Predicate<T> isNew) {
        Collection<T> newEntities = getCollection.apply(root);

        return newEntities.stream().filter(isNew).collect(Collectors.toList());
    }

    public <T, ID> Collection<T> findChangedEntities(Function<R, Collection<T>> getCollection, Function<T, ID> getId) {
        Collection<T> newEntities = getCollection.apply(root);
        Collection<T> oldEntities = getCollection.apply(snapshot);

        Set<ID> newIds = getEntityIds(newEntities, getId);
        Set<ID> oldIds = getEntityIds(oldEntities, getId);

        oldIds.retainAll(newIds);

        Collection<T> results = new ArrayList<>();
        for (ID id : oldIds) {
            T oldEntity = getEntity(oldEntities, id, getId);
            T newEntity = getEntity(newEntities, id, getId);

            if (!deepComparator.isDeepEquals(oldEntity, newEntity)) {
                results.add(newEntity);
            }
        }

        return results;
    }

    public <T, ID> Collection<T> findRemovedEntities(Function<R, Collection<T>> getCollection, Function<T, ID> getId) {
        Collection<T> newEntities = getCollection.apply(root);
        Collection<T> oldEntities = getCollection.apply(snapshot);

        Set<ID> newIds = getEntityIds(newEntities, getId);
        Set<ID> oldIds = getEntityIds(oldEntities, getId);

        oldIds.removeAll(newIds);

        return oldEntities.stream().filter(item -> oldIds.contains(getId.apply(item))).collect(Collectors.toList());
    }

    private <T, ID> Set<ID> getEntityIds(Collection<T> entity, Function<T, ID> getId) {
        return entity.stream().map(item -> getId.apply(item)).collect(Collectors.toSet());
    }

    private <T, ID> T getEntity(Collection<T> entities, ID id, Function<T, ID> getId) {
        for (T entity : entities) {
            if (id != null && id.equals(getId.apply(entity))) {
                return entity;
            }
        }

        throw new RuntimeException("Internal error. Couldn't find entity with id: " + id.toString());
    }
}
