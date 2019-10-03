package com.github.meixuesong.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Aggregate<R extends Versionable> {
    private R root;
    private R snapshot;
    private ObjectMapper mapper;

    public Aggregate(R root) {
        this.root = root;
        this.snapshot = createSnapshot();
    }

    public R getRoot() {
        return root;
    }

    public boolean isChanged() {
        return !isAllPropertiesEqual(root, snapshot);
    }

    public boolean isNew() {
        return root.getVersion() == Versionable.NEW_VERSION;
    }

    public <T> Collection<T> findInsertedEntities(Function<R, Collection<T>> getCollection, Predicate<T> isNew) {
        Collection<T> newEntities = getCollection.apply(root);

        return newEntities.stream().filter(isNew).collect(Collectors.toList());
    }

    public <T, ID> Collection<T> findUpdatedEntities(Function<R, Collection<T>> getCollection, Function<T, ID> getId) {
        Collection<T> newEntities = getCollection.apply(root);
        Collection<T> oldEntities = getCollection.apply(snapshot);

        Set<ID> newIds = getEntityIds(newEntities, getId);
        Set<ID> oldIds = getEntityIds(oldEntities, getId);

        oldIds.retainAll(newIds);

        Collection<T> results = new ArrayList<>();
        for (ID id : oldIds) {
            T oldEntity = getEntity(oldEntities, id, getId);
            T newEntity = getEntity(newEntities, id, getId);

            if (!isAllPropertiesEqual(oldEntity, newEntity)) {
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

    private R createSnapshot() {
        String json = getJson(root);

        ObjectMapper mapper = getObjectMapper();
        try {
            return mapper.readValue(json, (Class<R>) (root.getClass()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getJson(Object object) {
        ObjectMapper mapper = getObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectMapper getObjectMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        return mapper;
    }

    private <T, ID> Set<ID> getEntityIds(Collection<T> entity, Function<T, ID> getId) {
        return entity.stream().map(item -> getId.apply(item)).collect(Collectors.toSet());
    }

    private <T> boolean isAllPropertiesEqual(T entity1, T entity2) {
        String entityJson1 = getJson(entity1);
        String entityJson2 = getJson(entity2);

        return entityJson2.equals(entityJson1);
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
