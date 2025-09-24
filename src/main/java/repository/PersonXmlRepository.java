package main.java.repository;

import main.java.model.Person;
import main.java.model.Type;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public final class PersonXmlRepository extends XmlRepository<Person> {
    @Override
    protected Person parseEntity(final File file) {
        try {
            var lines = Files.readAllLines(file.toPath());
            var id = Long.valueOf(extractTagValue(lines, "personId"));
            var firstName = extractTagValue(lines, "firstName");
            var lastName = extractTagValue(lines, "lastName");
            var mobile = extractTagValue(lines, "mobile");
            var email = extractTagValue(lines, "email");
            var pesel = extractTagValue(lines, "pesel");
            var typeStr = extractTagValue(lines, "type");
            var type = Type.fromString(typeStr);
            return new Person(id, firstName, lastName, mobile, email, pesel, type);
        } catch (IOException e) {
            throw new RuntimeException("Error when parsing entity: " + e);
        }

    }

    @Override
    protected void writeEntity(final File file, final Person entity) {
        var lines = List.of(
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
        try {
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            throw new RuntimeException("Error when writing entity: " + e);
        }

    }


    public Optional<Person> find(
            final Type type,
            final String firstName,
            final String lastName,
            final String mobile,
            final String pesel,
            final String email
    ) {

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
        return String.valueOf(entity.personId());
    }

    @Override
    protected Type entityType(final Person entity) {
        return entity.type();
    }

}
