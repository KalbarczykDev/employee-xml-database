package main.java.repository;


import main.java.model.Type;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class XmlRepository<T> {

    protected abstract T parseEntity(final File file);

    protected abstract void writeEntity(final File file, final T entity);

    protected abstract String entityId(final T entity);

    protected abstract Type entityType(final T entity);

    protected String typeToBasePath(final Type type) {
        return switch (type) {
            case EXTERNAL -> "db/external/";
            case INTERNAL -> "db/internal/";
        };
    }

    protected File resolvePath(final Type type, final String entityId) {
        return new File(typeToBasePath(type) + entityId + ".xml");
    }

    protected String extractTagValue(final List<String> lines, final String tag) {
        for (var line : lines) {
            line = line.trim();
            if (line.startsWith("<" + tag + ">") && line.endsWith("</" + tag + ">")) {
                return line.substring(tag.length() + 2, line.length() - tag.length() - 3);
            }
        }
        return null;
    }


    public Optional<T> findById(final Long id) {
        for (var type : Type.values()) {
            var file = resolvePath(type,String.valueOf(id));
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


    public boolean remove(final String id) {
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


    public List<T> findAll()  {
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


    public String findNextId(final Type type) {
        var maxId = 0;
        var dir = new File(typeToBasePath(type));
        if (!dir.exists()) {
            return "0";
        }
        var files = dir.listFiles((_, name) -> name.endsWith(".xml"));

        if (files != null) {
            for (var file : files) {
                var name = file.getName();
                var idPart = name.substring(0, name.length() - 4);
                try {
                    var id = Integer.parseInt(idPart);
                    if (id > maxId) maxId = id;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.valueOf(maxId + 1);
    }

}
