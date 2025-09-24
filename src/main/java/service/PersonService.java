package main.java.service;

import main.java.model.Person;
import main.java.model.Type;
import main.java.repository.PersonXmlRepository;

import java.util.List;
import java.util.Optional;

public final class PersonService {

    private final PersonXmlRepository repository;

    public PersonService(final PersonXmlRepository repository) {
        this.repository = repository;
    }

    public Optional<Person> findById(final Long id) {
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

    public void create(final Type type,
                       final String firstName,
                       final String lastName,
                       final String mobile,
                       final String pesel,
                       final String email) {
        //TODO:  Basic validation

        var person = new Person(
                repository.findNextId(type), firstName, lastName, mobile, pesel, email, type
        );

        repository.create(person);
    }

    public void deleteById(final Long id) {
        if (repository.remove(id)) {
            IO.println("Deleted person with id: " + id);
        }
        {
            IO.println("Person with id: " + id + " was not deleted.");
        }
    }

    public void modify(final Person person) {
        //TODO: Basic validation
        repository.modify(person);
    }

    public List<Person> findAll() {
        return repository.findAll();
    }
}
