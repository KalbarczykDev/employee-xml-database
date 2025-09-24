package main.java.repository;

import main.java.model.Person;
import main.java.model.Type;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class XmlRepository<T> implements Repository<T> {
    protected abstract String rootElement();

    protected abstract T documentToEntity(Document document);

    protected abstract Document entityToDocument(T entity);

    protected abstract String entityId(T entity);

    protected abstract Type entityType(T entity);

    protected Document loadXml(final File file) throws ParserConfigurationException, IOException, SAXException {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }

    protected void writeXml(final Document document, final File file) throws TransformerException {
        var transformer = TransformerFactory.newInstance().newTransformer();
        var source = new DOMSource(document);
        var result = new StreamResult(file);
        transformer.transform(source, result);
    }

    protected String getElementValue(final Document document, final String tagName) {
        var list = document.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return null;
    }

    protected String typeToBasePath(final Type type) {
        return switch (type) {
            case EXTERNAL -> "db/external";
            case INTERNAL -> "db/internal";
        };
    }

    protected File resolvePath(final Type type, final String entityId) {
        return new File(typeToBasePath(type) + entityId + ".xml");
    }

    public Optional<T> findById(final String id) {
        for (var type : Type.values()) {
            var file = resolvePath(type, id);
            if (file.exists()) {
                try {
                    final var document = loadXml(file);
                    return Optional.of(documentToEntity(document));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return Optional.empty();
    }

    public void create(final T entity) {
        try {
            var document = entityToDocument(entity);
            var file = resolvePath(entityType(entity), entityId(entity));
            writeXml(document, file);
        } catch (Exception e) {
            throw new RuntimeException("Error creating entity", e);
        }
    }

    public boolean remove(final String id) {
        for (var type : Type.values()) {
            final var file = resolvePath(type, id);
            if (file.exists()) {
                return file.delete();
            }
        }
        return false;
    }

    public void modify(final T entity) {
        create(entity);
    }

    public List<T> findAll() {

        var baseDir = new File("db/");
        var xmlFiles = baseDir.listFiles((_, name) -> name.endsWith(".xml"));

        if (xmlFiles == null) {
            return List.of();
        }

        return Arrays.stream(xmlFiles).map(file -> {
            try {
                var document = loadXml(file);
                return documentToEntity(document);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

}
