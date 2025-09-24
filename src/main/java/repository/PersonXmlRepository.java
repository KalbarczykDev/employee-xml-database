package main.java.repository;

import main.java.model.Person;
import main.java.model.Type;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public final class PersonXmlRepository extends XmlRepository<Person> {

    @Override
    protected String rootElement() {
        return "person";
    }

    @Override
    protected Person parseEntity(final File file) throws IOException {
        var lines = Files.readAllLines(file.toPath());
        var id = extractTagValue(lines, "personId");
        var firstName = extractTagValue(lines, "firstName");
        var lastName = extractTagValue(lines, "lastName");
        var mobile = extractTagValue(lines, "mobile");
        var email = extractTagValue(lines, "email");
        var pesel = extractTagValue(lines, "pesel");
        var typeStr = extractTagValue(lines, "type");
        var type = Type.fromString(typeStr);
        return new Person(id, firstName, lastName, mobile, email, pesel, type);
    }

    @Override
    protected void writeEntity(final File file,final Person entity) throws IOException {
        List<String> lines = List.of(
                "<person>",
                "  <personId>" + entity.personId() + "</personId>",
                "  <firstName>" + entity.firstName() + "</firstName>",
                "  <lastName>" + entity.lastName() + "</lastName>",
                "  <mobile>" + entity.mobile() + "</mobile>",
                "  <email>" + entity.email() + "</email>",
                "  <pesel>" + entity.pesel() + "</pesel>",
                "  <type>" + entity.type().toString() + "</type>",
                "</person>"
        );
        Files.write(file.toPath(), lines);
    }


    @Override
    public Optional<Person> find(final Object... attributes) throws IOException {
        if (attributes.length == 0) return Optional.empty();

        var type = (Type) attributes[0];
        var firstName = attributes.length > 1 ? (String) attributes[1] : null;
        var lastName = attributes.length > 2 ? (String) attributes[2] : null;
        var mobile = attributes.length > 3 ? (String) attributes[3] : null;
        var pesel = attributes.length > 4 ? (String) attributes[4] : null;
        var email = attributes.length > 5 ? (String) attributes[5] : null;

        return findAll().stream()
                .filter(p -> p.type() == type)
                .filter(p -> firstName == null || p.firstName().equals(firstName))
                .filter(p -> lastName == null || p.lastName().equals(lastName))
                .filter(p -> mobile == null || p.mobile().equals(mobile))
                .filter(p -> pesel == null || p.pesel().equals(pesel))
                .filter(p -> email == null || p.email().equals(email))
                .findFirst();
    }

    @Override
    protected String entityId(final Person entity) {
        return entity.personId();
    }

    @Override
    protected Type entityType(final Person entity) {
        return entity.type();
    }


}
