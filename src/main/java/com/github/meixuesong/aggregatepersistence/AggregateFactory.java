package com.github.meixuesong.aggregatepersistence;

public class AggregateFactory {
    public static <R extends Versionable> Aggregate<R> createAggregate(R root) {
        return new Aggregate<R>(root, new JsonDeepCopier(), new JavaUtilDeepComparator());
    }
}
