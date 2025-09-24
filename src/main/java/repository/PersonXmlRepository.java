package main.java.repository;

import main.java.model.Person;
import main.java.model.Type;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public final class PersonXmlRepository extends XmlRepository<Person> {

    @Override
    protected String rootElement() {
        return "person";
    }
    @Override
    protected Person documentToEntity(final Document document) {
        var id = getElementValue(document, "personId");
        var firstName = getElementValue(document, "firstName");
        var lastName = getElementValue(document, "lastName");
        var mobile = getElementValue(document, "mobile");
        var email = getElementValue(document, "email");
        var pesel = getElementValue(document, "pesel");
        var type = Type.valueOf(getElementValue(document, "type"));
        return new Person(id, firstName, lastName, mobile, email, pesel, type);
    }

    @Override
    protected Document entityToDocument(final Person entity) {
        try {

            var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            var root = document.createElement(rootElement());
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

            var type = document.createElement("type");
            type.setTextContent(entity.type().toString());
            root.appendChild(type);

            root.appendChild(pesel);
            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public Optional<Person> find(
            final Object... attributes
    ) {

        if (attributes.length == 0) {
            return Optional.empty();
        }

        var type = (Type) attributes[0];
        String firstName = attributes.length > 1 ? (String) attributes[1] : null;
        String lastName = attributes.length > 2 ? (String) attributes[2] : null;
        String mobile = attributes.length > 3 ? (String) attributes[3] : null;
        String pesel = attributes.length > 4 ? (String) attributes[4] : null;
        String email = attributes.length > 5 ? (String) attributes[5] : null;

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

    @Override
    protected String entityId(Person entity) {
        return entity.personId();
    }

    @Override
    protected Type entityType(Person entity) {
        return entity.type();
    }


}
