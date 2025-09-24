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
import java.util.*;

public final class PersonXmlRepository {

    private static final String ROOT_ELEMENT = "person";

    private Document loadXml(final File file) throws ParserConfigurationException, IOException, SAXException {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }

    private void writeXml(final Document document, final File file) throws TransformerException {
        var transformer = TransformerFactory.newInstance().newTransformer();
        var source = new DOMSource(document);
        var result = new StreamResult(file);
        transformer.transform(source, result);
    }

    private String getElementValue(final Document document, final String tagName) {
        var list = document.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return null;
    }

    private Document entityToDocument(final Person entity) {
        try {

            var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            var root = document.createElement(ROOT_ELEMENT);
            document.appendChild(root);

            var id = document.createElement("personId");
            id.setTextContent(entity.personId());
            root.appendChild(id);

            var firstName = document.createElement("firstName");
            firstName.setTextContent(entity.firstName());
            root.appendChild(firstName);

            var lastName = document.createElement("lastName");
            lastName.setTextContent(entity.lastName());
            root.appendChild(lastName);

            var mobile = document.createElement("mobile");
            mobile.setTextContent(entity.mobile());
            root.appendChild(mobile);

            var email = document.createElement("email");
            email.setTextContent(entity.email());
            root.appendChild(email);

            var pesel = document.createElement("pesel");
            pesel.setTextContent(entity.pesel());

            root.appendChild(pesel);
            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Person documentToEntity(final Document document) {
        var id = getElementValue(document, "personId");
        var firstName = getElementValue(document, "firstName");
        var lastName = getElementValue(document, "lastName");
        var mobile = getElementValue(document, "mobile");
        var email = getElementValue(document, "email");
        var pesel = getElementValue(document, "pesel");

        var type = id != null && new File("db/external/" + id + ".xml").exists() ? Type.EXTERNAL : Type.INTERNAL;

        return new Person(id, firstName, lastName, mobile, email, pesel, type);
    }

    private String typeToBasePath(final Type type) {
        return switch (type) {
            case EXTERNAL -> "db/external";
            case INTERNAL -> "db/internal";
        };
    }

    private File resolvePath(final Type type, final String personId) {
        return new File(typeToBasePath(type) + personId + ".xml");
    }


    public Optional<Person> findById(final String id) {
        for (var type : Type.values()) {
            var file = resolvePath(type, id);
            if (file.exists()) {
                try {
                    final var document = loadXml(file);
                    return Optional.of(documentToEntity(document));
                } catch (Exception e) {
                    e.printStackTrace(); //TODO: add more robust handling
                }
            }
        }
        return Optional.empty();
    }


    public Optional<Person> find(
            final Type type,
            final String firstName,
            final String lastName,
            final String mobile,
            final String pesel,
            final String email
    ) {
        var baseDir = new File(typeToBasePath(type));
        var xmlFiles = baseDir.listFiles((_, name) -> name.endsWith(".xml"));

        if (xmlFiles == null)
            return Optional.empty();


        return Arrays.stream(xmlFiles)
                .map(file -> {
                    try {
                        final var document = loadXml(file);
                        return documentToEntity(document);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(p ->
                        (firstName == null || p.firstName().equals(firstName)) &&
                                (lastName == null || p.lastName().equals(lastName)) &&
                                (mobile == null || p.mobile().equals(mobile)) &&
                                (pesel == null || p.pesel().equals(pesel)) &&
                                (email == null || p.email().equals(email))
                )
                .findFirst();
    }


    public void create(final Person entity) {
        try {
            final var document = entityToDocument(entity);
            final var file = resolvePath(entity.type(), entity.personId());
            writeXml(document, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean remove(String id) {
        for (var type : Type.values()) {
            final var file = resolvePath(type, id);
            if (file.exists()) {
                return file.delete();
            }
        }
        return false;
    }


    public void modify(Person entity) {
        create(entity);
    }


    public List<Person> findAll() {

        final var baseDir = new File("db/");
        final var xmlFiles = baseDir.listFiles((_, name) -> name.endsWith(".xml"));

        if (xmlFiles == null) {
            return List.of();
        }

        return Arrays.stream(xmlFiles).map(file -> {
            try {
                final var document = loadXml(file);
                return documentToEntity(document);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }


}
