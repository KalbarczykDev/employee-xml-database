package main.java.repository;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    Optional<T> findById(final String id);

    Optional<T> find(final Object... attributes) throws IOException;

    void create(final T entity);

    boolean remove(final String id);

    void modify(final T entity);

    List<T> findAll() throws IOException;
}
