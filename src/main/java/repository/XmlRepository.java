package main.java.repository;


import main.java.model.Type;
import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class XmlRepository<T> implements Repository<T> {
    protected abstract String rootElement();

    protected abstract T parseEntity(final File file) throws IOException;

    protected abstract void writeEntity(final File file, final T entity) throws IOException;

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

    protected String extractTagValue(final List<String> lines,final String tag) {
        for (var line : lines) {
            line = line.trim();
            if (line.startsWith("<" + tag + ">") && line.endsWith("</" + tag + ">")) {
                return line.substring(tag.length() + 2, line.length() - tag.length() - 3);
            }
        }
        return null;
    }

    @Override
    public Optional<T> findById(final String id) {
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

    @Override
    public void create(final T entity) {
        var file = resolvePath(entityType(entity), entityId(entity));
        try {
            writeEntity(file, entity);
        } catch (Exception e) {
            throw new RuntimeException("Error creating entity", e);
        }
    }

    @Override
    public boolean remove(final String id) {
        for (var type : Type.values()) {
            var file = resolvePath(type, id);
            if (file.exists())
                return file.delete();
        }
        return false;
    }

    @Override
    public void modify(final T entity) {
        create(entity);
    }

    @Override
    public List<T> findAll() throws IOException {
        var all = new ArrayList<T>();

        for (var type : Type.values()) {
            var dir = new File(typeToBasePath(type));
            if (!dir.exists()) continue;
            var files = dir.listFiles((d, name) -> name.endsWith(".xml"));
            if (files == null) continue;
            for (var file : files) {
                all.add(parseEntity(file));
            }
        }
        return all;
    }

}
