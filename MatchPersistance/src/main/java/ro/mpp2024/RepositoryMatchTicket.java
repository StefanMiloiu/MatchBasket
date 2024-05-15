package ro.mpp2024;



import java.sql.SQLException;
import java.util.Optional;

public interface RepositoryMatchTicket<ID, E extends Entity<ID>> {

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

}
