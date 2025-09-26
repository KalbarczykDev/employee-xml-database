package main.repository;


import main.model.Type;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public abstract class XmlRepository<T> {

    protected abstract T parseEntity(final File file);

    protected abstract void writeEntity(final File file, final T entity);

    protected abstract UUID entityId(final T entity);

    protected abstract Type entityType(final T entity);

    protected String typeToBasePath(final Type type) {
        return switch (type) {
            case EXTERNAL -> "db/external/";
            case INTERNAL -> "db/internal/";
        };
    }

    protected File resolvePath(final Type type, final UUID entityId) {
        return new File(typeToBasePath(type) + entityId.toString() + ".xml");
    }

    protected Map<String, String> extractTagValuesFromFile(final File file) {

        try {
            var lines = Files.readAllLines(file.toPath());
            var map = new HashMap<String, String>();
            for (var line : lines) {
                line = line.trim();
                if (line.startsWith("<") && line.contains(">") && line.endsWith(">")) {
                    var start = line.indexOf('<') + 1;
                    var end = line.indexOf('>', start);
                    var tag = line.substring(start, end);
                    var closing = "</" + tag + ">";
                    if (line.endsWith(closing)) {
                        var value = line.substring(end + 1, line.length() - closing.length());
                        map.put(tag, value);
                    }
                }
            }
            return map;
        } catch (IOException e) {
            throw new RuntimeException("Error when parsing entity", e);
        }
    }


    public Optional<T> findById(final UUID id) {
        for (var type : Type.values()) {
            var file = resolvePath(type, id);
            if (file.exists()) {
                try {
                    return Optional.of(parseEntity(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return Optional.empty();
    }


    public void create(final T entity) {
        var file = resolvePath(entityType(entity), entityId(entity));
        if (file.exists()) {
            throw new RuntimeException("Entity with ID '" + entityId(entity) + "' already exists.");
        }
        try {
            writeEntity(file, entity);
        } catch (Exception e) {
            throw new RuntimeException("Error creating entity", e);
        }
    }


    public boolean remove(final UUID id) {
        for (var type : Type.values()) {
            var file = resolvePath(type, id);
            if (file.exists()) return file.delete();
        }
        return false;
    }


    public void modify(final T entity) {
        var file = resolvePath(entityType(entity), entityId(entity));

        try {
            writeEntity(file, entity);
        } catch (Exception e) {
            throw new RuntimeException("Error modifying entity", e);
        }
    }


    public List<T> findAll() {
        var all = new ArrayList<T>();

        for (var type : Type.values()) {
            var dir = new File(typeToBasePath(type));
            if (!dir.exists()) continue;
            var files = dir.listFiles((_, name) -> name.endsWith(".xml"));
            if (files == null) continue;
            for (var file : files) {
                all.add(parseEntity(file));
            }
        }
        return all;
    }


    public UUID generateId() {
        return UUID.randomUUID();
    }

}
