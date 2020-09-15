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

/**
 * The aggregate factory will create the aggregate.
 *
 * @author meixuesong
 */
public class AggregateFactory {
    private AggregateFactory() {
        throw new IllegalStateException("A factory class, please use static method");
    }

    private static DeepCopier copier = new SerializableDeepCopier();
    /**
     * The factory method.
     *
     * @param root The aggregate root
     * @param <R> The type of aggregate root
     * @return the aggregate object
     */
    public static <R extends Versionable> Aggregate<R> createAggregate(R root) {
        return new Aggregate<R>(root, copier, new JavaUtilDeepComparator());
    }

    /**
     * set deep copier.
     * @param copier the deepcopier object
     */
    public static void setCopier(DeepCopier copier) {
        AggregateFactory.copier = copier;
    }
}
