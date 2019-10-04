package com.github.meixuesong.common;

public class AggregateFactory {
    public static <R extends Versionable> Aggregate<R> createAggregate(R root) {
        return new Aggregate<R>(root, new DeepCopierImpl(), new PropertyComparatorImpl());
    }
}
