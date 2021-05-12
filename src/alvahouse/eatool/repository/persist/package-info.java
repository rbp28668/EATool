/**
 * This package defines the persistence layer interfaces that build up a persistence model for the repository.
 * By using these abstractions both the calling code and the lower level implementation work to a common abstraction
 * to decouple the implementations.
 * These also allow the production of interceptor layers e.g. logging, audit and authentication as separate concerns.
 * The unit of persistence should be fairly granular:
 * A meta entitity will be treated as a single unit with all of its meta properties and any display hint.  Base meta entities would be persisted separately.
 * Meta relationships include any meta properties and meta roles.
 * MetaPropertyTypes are persisted as discrete entities as part of the metamodel.
 * Entities contain all their properties.
 * Relationships contain their roles.
 * Diagrams are persisted as a single entity.
 */
package alvahouse.eatool.repository.persist;
