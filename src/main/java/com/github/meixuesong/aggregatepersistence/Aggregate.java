/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */

package com.github.meixuesong.aggregatepersistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The aggregate container. It will hold the root and it's snapshot, which is the old value of the root.
 * So it's very convenient to recognize whether the aggregate changed by comparing current root with the snapshot.
 *
 * The repository will return the aggregate to application service, and save the aggregate when the business is done.
 *
 <pre><code class='java'>
 Aggregate<Order> orderAggregate = repository.findById("orderId");
 orderAggregate.doSomething();
 repository.save(orderAggregate);
</code></pre>
 *
 *
 * @author meixuesong
 * @param <R> The aggregate root type, such as Order
 */
public class Aggregate<R extends Versionable> {
    protected R root;
    protected R snapshot;
    protected DeepComparator deepComparator;

    /**
     * Construct the aggregate object
     * @param root the aggregate root
     * @param copier the deepCopier which is used to create the snapshot of the root.
     * @param deepComparator the deepComparator which is used to compare the root and snapshot
     */
    Aggregate(R root, DeepCopier copier, DeepComparator deepComparator) {
        this.root = root;
        this.snapshot = copier.copy(root);
        this.deepComparator = deepComparator;
    }

    /**
     * The aggregate root
     * @return
     */
    public R getRoot() {
        return root;
    }

    /**
     * The snapshot of the aggregate root
     * @return
     */
    public R getRootSnapshot() {
        return snapshot;
    }

    /**
     * Whether the aggregate is changed.
     * @return true if the aggregate is changed, false if the aggregate is unchanged.
     */
    public boolean isChanged() {
        return !deepComparator.isDeepEquals(root, snapshot);
    }

    /**
     * Whether it is a new aggregate.
     * @return true if it's new
     */
    public boolean isNew() {
        return root.getVersion() == Versionable.NEW_VERSION;
    }

    /**
     * Find the new entities. New entities are going to be insert into the DB.
     *
     * <pre><code class="java">
     Collection<OrderItem> newEntities = orderAggregate.findNewEntities(Order::getItems, (item) -> item.getId() == null);
     //insert all the new entities.
     * </code></pre>
     *
     * @param getCollection The function of the aggregate root, used to get entity collection. e.g. Order:getItems
     * @param isNew The function used to identify whether the entity is new. e.g. OrderItem:isIdNull
     * @param <T> The entity type. e.g. OrderItem
     * @return All the entities that has been created.
     */
    public <T> Collection<T> findNewEntities(Function<R, Collection<T>> getCollection, Predicate<T> isNew) {
        Collection<T> newEntities = getCollection.apply(root);

        return newEntities.stream().filter(isNew).collect(Collectors.toList());
    }

    /**
     * Find the changed entities. Changed entities are going to be update into the DB.
     * @param getCollection The function of the aggregate root, used to get entity collection. e.g. Order:getItems
     * @param getId The function of get ID. Entity are identified by ID.
     * @param <T> The entity type
     * @param <ID> The type of the entity id.
     * @return All the entities that has been changed.
     */
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

    /**
     * Find the removed entities. Removed entities are going to be delete logically or physically from the DB.
     * @param getCollection  The function of the aggregate root, used to get entity collection. e.g. Order:getItems
     * @param getId The function of get ID. Entity are identified by ID.
     * @param <T> The entity type
     * @param <ID> The type of the entity id.
     * @return All the entities that has been removed.
     */
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
