# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview
This is a Java library that provides a lightweight solution for Domain-Driven Design (DDD) aggregate persistence. It solves the problem of persisting aggregates (which may span multiple entities) in a clean, maintainable way without coupling domain logic to persistence concerns.

**Key Concept**: The library introduces an `Aggregate<T>` container that holds both the current aggregate root and a snapshot. This allows tracking changes to the entire aggregate, enabling efficient persistence operations that only update changed fields.

**Latest Version**: 1.3.2 - Upgraded to JDK 21

## Build Commands

- **Compile**: `mvn clean compile`
- **Run tests**: `mvn test`
- **Run single test**: `mvn test -Dtest=ClassName#methodName` (e.g., `mvn test -Dtest=AggregateTest#should_be_new_when_version_is_NEW_VERSION`)
- **Package**: `mvn clean package` (creates JAR in `target/`)
- **Install to local repository**: `mvn install`
- **Release build** (generates sources and Javadoc JARs): `mvn clean package -P release`

## Code Architecture

### Core Components

1. **Aggregate.java** - The main container class
   - Wraps an aggregate root and maintains a snapshot
   - Provides methods to identify changed entities: `findNewEntities()`, `findChangedEntities()`, `findRemovedEntities()`
   - Uses deep comparison to detect changes via `DeepComparator`
   - Key methods: `getRoot()`, `isChanged()`, `isNew()`

2. **AggregateFactory.java** - Factory for creating Aggregate instances
   - Static factory method: `createAggregate(R root)`
   - Configurable deep copier via `setCopier(DeepCopier)`

3. **DataObjectUtils.java** - Utility for comparing data objects
   - `getChangedFields(old, current)` - Returns set of changed field names
   - `getDelta(old, current)` - Creates delta object with only changed fields populated
   - Uses reflection and DeepEquals for deep field comparison

4. **Versionable.java** - Interface for optimistic locking
   - Requires `version` field for aggregate roots
   - NEW_VERSION constant indicates new entities

### Supporting Classes

- **ChangedEntity.java** - Holds old and new entity values for change tracking
- **DeepComparator.java** - Interface for deep comparison strategy
- **JavaUtilDeepComparator.java** - Default implementation using java-util library
- **DeepCopier.java** - Interface for deep copying strategy
- **SerializableDeepCopier.java** - Default serialization-based copier

## Testing

- JUnit 4.13.1
- Mockito 2.19.1 for mocking
- Test files in `src/test/java/`
- Complex integration tests use entities like Loan, Contract in `complex_object` package
- Test setup uses `AggregateFactory.setCopier()` to configure copier

## Dependencies

- **java-util 4.0.0** - DeepEquals and ReflectionUtils for object comparison
- **commons-lang3 3.18.0** - Apache Commons Lang utilities
- **JUnit 4.13.1** - Testing framework
- **Mockito 2.19.1** - Mocking framework

Java 8+ compatibility.

## Usage Pattern

```java
// Repository finds aggregate
Aggregate<Order> aggregate = orderRepository.findById(orderId);
Order order = aggregate.getRoot();

// Modify aggregate
order.doSomething();

// Repository saves - only changed entities/fields are persisted
orderRepository.save(aggregate);
```

Inside repository:
```java
void save(Aggregate<Order> aggregate) {
    if (aggregate.isNew()) {
        // Insert new aggregate
    } else if (aggregate.isChanged()) {
        // Update only changed root fields
        Set<String> changedFields = DataObjectUtils.getChangedFields(
            aggregate.getRootSnapshot(), aggregate.getRoot());

        // Find changed child entities
        Collection<OrderItem> changedItems = aggregate.findChangedEntities(
            Order::getItems, OrderItem::getId);
    }
}
```

## Important Notes

- Aggregate roots must implement `Versionable` interface
- Deep comparison uses java-util's DeepEquals library (not standard equals())
- The library doesn't handle actual persistence - it only identifies what changed
- Repository implementation must handle actual database operations
- Optimistic locking via version field prevents concurrent modification issues
