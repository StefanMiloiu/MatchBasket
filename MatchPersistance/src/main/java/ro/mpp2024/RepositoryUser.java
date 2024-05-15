package ro.mpp2024;


import ro.mpp2024.Entity;
import java.sql.SQLException;
import java.util.Optional;

/*
 * CRUD operations repository interface
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */
public interface RepositoryUser<ID, E extends Entity<ID>> {

    /**
     * Find the entity with the given {@code id}.
     *
     * @param id must be not null.
     * @return an {@code Optional} encapsulating the entity with the given id.
     * @throws IllegalArgumentException if the given id is null.
     */
    Optional<E> findOne(ID id) throws SQLException;

    /**
     * @return all entities.
     */
    Iterable<E> findAll() throws SQLException;

    /**
     * Saves the given entity.
     *
     * @param entity must not be null.
     * @return an {@code Optional} - null if the entity was saved otherwise (e.g. id already exists) returns the entity.
     * @throws IllegalArgumentException if the given entity is null.
     */
    Optional<E> save(E entity) throws SQLException;

//    /**
//     * Removes the entity with the given id.
//     *
//     * @param id must not be null.
//     * @return an {@code Optional} - null if there is no entity with the given id, otherwise the removed entity.
//     * @throws IllegalArgumentException if the given id is null.
//     */
//    Optional<E> delete(ID id) throws IOException;
//
//    /**
//     * Updates the given entity.
//     *
//     * @param entity must not be null.
//     * @return an {@code Optional} - null if the entity was updated otherwise (e.g. id does not exist) returns the entity.
//     * @throws IllegalArgumentException if the given entity is null.
//     */
//    Optional<E> update(E entity);
}
