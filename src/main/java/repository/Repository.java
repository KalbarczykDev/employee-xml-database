package main.java.repository;


import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    Optional<T> findById(final String id);

    Optional<T> find(final Object... attributes);

    void create(final T entity);

    boolean remove(final String id);

    void modify(final T entity);

    List<T> findAll();
}
