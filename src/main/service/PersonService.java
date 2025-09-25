package main.service;

import main.model.Person;
import main.model.Type;
import main.repository.PersonXmlRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class PersonService {

    private final PersonXmlRepository repository;

    public PersonService(final PersonXmlRepository repository) {
        this.repository = repository;
    }

    public Optional<Person> findById(final UUID id) {
        return repository.findById(id);
    }

    public Optional<Person> find(
            final Type type,
            final String firstName,
            final String lastName,
            final String mobile,
            final String pesel,
            final String email
    ) {
        return repository.find(type, firstName, lastName, mobile, pesel, email);
    }

    public Person create(final Type type,
                         final String firstName,
                         final String lastName,
                         final String mobile,
                         final String pesel,
                         final String email) {

        var person = new Person(
                repository.generateId(), firstName, lastName, mobile, email, pesel, type
        );

        validatePerson(person);
        repository.create(person);
        return person;
    }

    public boolean deleteById(final UUID id) {
        return repository.remove(id);
    }

    public void modify(final Person person) {
        if (person == null) throw new IllegalArgumentException("person is null");
        validatePerson(person);
        repository.modify(person);
    }

    public List<Person> findAll() {
        return repository.findAll();
    }

    private void validatePerson(final Person person) {

        if (person.firstName() == null || person.firstName().isBlank()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }

        if (person.lastName() == null || person.lastName().isBlank()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }

        if (person.mobile() == null || !person.mobile().matches("\\d{9,15}")) {
            throw new IllegalArgumentException("Mobile must be numeric and 9-15 digits long");
        }


        if (person.pesel() == null || !person.pesel().trim().matches("\\d{11}")) {
            throw new IllegalArgumentException("PESEL must be exactly 11 digits");
        }


        if (person.email() == null || !person.email().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (person.type() == null) {
            throw new IllegalArgumentException("Type cannot be null");

        }
    }
}
