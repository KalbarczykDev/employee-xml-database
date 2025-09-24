package test.java.service;

import main.java.model.Person;
import main.java.model.Type;
import main.java.service.PersonService;
import test.java.TestResult;
import test.java.TestRunner;

import java.util.List;


public class PersonServiceTest {

    private final PersonService service;

    public PersonServiceTest(final PersonService personService) {
        this.service = personService;
    }

    public void runTests() {
        runTest("shouldFindAll", this::shouldFindAll);
        runTest("shouldCreateValidPerson", this::shouldCreateValidPerson);
        runTest("shouldThrowWhenCreatingInvalidPerson", this::shouldThrowWhenCreatingInvalidPerson);
        runTest("shouldFindById", this::shouldFindById);
        runTest("shouldThrowWhenSearchingInvalidId", this::shouldThrowWhenSearchingInvalidId);
        runTest("shouldFindByFirstName", this::shouldFindByFirstName);
        runTest("shouldNotFindByFirstName", this::shouldNotFindByFirstName);
        runTest("shouldModifyPerson", this::shouldModifyPerson);
        runTest("shouldThrowWhenModifyingInvalidPerson", this::shouldThrowWhenModifyingInvalidPerson);
        runTest("shouldDeleteById", this::shouldDeleteById);
        runTest("shouldThrowWhenDeletingByInvalidId", this::shouldThrowWhenDeletingByInvalidId);
    }

    private void runTest(String name, TestRunner testRunner) {
        TestResult result = testRunner.run();
        System.out.println(name + ": " + (result.success() ? "PASSED" : "FAILED - " + result.message()));
    }

    // ------------------ FIND ALL TESTS ----------------

    private TestResult shouldFindAll() {
        var persons = service.findAll();
        if (persons.size() < 10) {
            return TestResult.failed("Persons not found");
        }
        return TestResult.succeed();
    }

    // ------------------ CREATE TESTS ------------------

    private TestResult shouldCreateValidPerson() {
        try {
            service.create(Type.EXTERNAL, "John", "Doe", "123456789", "12345678910", "john@example.com");
            return TestResult.succeed();
        } catch (Exception e) {
            return TestResult.failed(e.getMessage());
        }
    }

    private TestResult shouldThrowWhenCreatingInvalidPerson() {
        try {
            service.create(Type.EXTERNAL, "John", "Doe", "123456789", "12345678910", "invalid-email");
            return TestResult.failed("Created invalid person");
        } catch (Exception e) {
            return TestResult.succeed();
        }
    }

    // ------------------ FIND BY ID ------------------

    private TestResult shouldFindById() {
        var personOptional = service.findById(1L);
        if (personOptional.isPresent()) {
            return TestResult.succeed();
        }
        return TestResult.failed("Person not found by ID");
    }

    private TestResult shouldThrowWhenSearchingInvalidId() {
        try {
            var personOptional = service.findById(-1L);
            if (personOptional.isEmpty()) {
                return TestResult.failed("Exception expected");
            }
        } catch (Exception e) {
            return TestResult.succeed();
        }

        return TestResult.failed("Found person for invalid ID");
    }

    // ------------------ FIND BY FIRST NAME ------------------

    private TestResult shouldFindByFirstName() {
        var personOpt = service.find(null, "ExternalFirst0", null, null, null, null);
        if (personOpt.isPresent()) {
            return TestResult.succeed();
        }
        return TestResult.failed("Person not found by first name");
    }

    private TestResult shouldNotFindByFirstName() {
        var personOpt = service.find(null, "Nonexistent", null, null, null, null);
        if (personOpt.isEmpty()) {
            return TestResult.succeed();
        }
        return TestResult.failed("Found non-existent person");
    }

    // ------------------ MODIFY ------------------

    private TestResult shouldModifyPerson() {
        try {
            var modified = new Person(1L, "Johnny", "Doe", "123456789", "12345678901", "john@example.com", Type.EXTERNAL);
            service.modify(modified);

            var retrieved = service.findById(1L);
            if (retrieved.isPresent() && retrieved.get().firstName().equals("Johnny")) {
                return TestResult.succeed();
            }
        } catch (Exception e) {
            return TestResult.failed(e.getMessage());
        }
        return TestResult.failed("Person not modified");
    }

    private TestResult shouldThrowWhenModifyingInvalidPerson() {
        try {
            var personOptional = service.findById(1L);

            if (personOptional.isEmpty()) {
                return TestResult.failed("Person not found by ID");
            }
            var person = new Person(personOptional.get().personId(), "John", "Doe", "123456789",
                    "12345678901", "invalid-email", Type.EXTERNAL);
            service.modify(person);
            return TestResult.failed("Expected exception");
        } catch (Exception e) {
            return TestResult.succeed();
        }
    }

    // ------------------ DELETE ------------------

    private TestResult shouldDeleteById() {
        service.deleteById(1L);
        var deleted = service.findById(1L);
        if (deleted.isEmpty()) {
            return TestResult.succeed();
        }
        return TestResult.failed("Person not deleted");
    }

    private TestResult shouldThrowWhenDeletingByInvalidId() {
        boolean removed = service.deleteById(-1L);
        if (!removed) {
            return TestResult.succeed();
        }
        return TestResult.failed("Deleted non-existent person");
    }


}
