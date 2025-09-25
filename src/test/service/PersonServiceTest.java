package test.service;

import main.model.Person;
import main.model.Type;
import main.service.PersonService;
import test.TestResult;
import test.TestRunner;

import java.util.UUID;


public class PersonServiceTest {

    private final PersonService service;

    public PersonServiceTest(final PersonService personService) {
        this.service = personService;
    }

    public void runTests() {
        runTest("shouldFindAll", this::shouldFindAll);
        runTest("shouldCreateAndDeleteValidPerson", this::shouldCreateAndDeleteValidPerson);
        runTest("shouldThrowWhenCreatingInvalidPerson", this::shouldThrowWhenCreatingInvalidPerson);
        runTest("shouldFindById", this::shouldFindById);
        runTest("shouldFailWhenSearchingForNonExistingId", this::shouldFailWhenSearchingForNonExistingId);
        runTest("shouldFindByFirstName", this::shouldFindByFirstName);
        runTest("shouldNotFindByFirstName", this::shouldNotFindByFirstName);
        runTest("shouldModifyPerson", this::shouldModifyPerson);
        runTest("shouldThrowWhenModifyingInvalidPerson", this::shouldThrowWhenModifyingInvalidPerson);
        runTest("shouldDeleteById", this::shouldDeleteById);
        runTest("shouldFailWhenDeletingByInvalidId", this::shouldFailWhenDeletingByInvalidId);
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

    private TestResult shouldCreateAndDeleteValidPerson() {
        try {
            var person = service.create(Type.EXTERNAL, "John", "Doe", "123456789", "12345678910", "john@example.com");
            service.deleteById(person.personId());
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
        var all = service.findAll();
        if (all.isEmpty()) return TestResult.failed("No persons found");
        var existingId = all.getFirst().personId();
        var personOptional = service.findById(existingId);
        if (personOptional.isPresent()) {
            return TestResult.succeed();
        }
        return TestResult.failed("Person not found by ID");
    }

    private TestResult shouldFailWhenSearchingForNonExistingId() {
        try {
            var personOptional = service.findById(UUID.randomUUID());
            if (personOptional.isEmpty()) {
                return TestResult.succeed();
            }
        } catch (Exception e) {
            return TestResult.succeed();
        }

        return TestResult.failed("Found person for invalid ID");
    }

    // ------------------ FIND BY FIRST NAME ------------------

    private TestResult shouldFindByFirstName() {
        var personOpt = service.find(null, "ExternalFirst1", null, null, null, null);
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
            var newPerson = service.create(Type.INTERNAL, "new", "new", "123456789",
                    "12345678910", "new@example.com");

            var modified = new Person(newPerson.personId(), "modified",
                    "modified", "123456789", "modified@example.com", "12345678901", Type.INTERNAL);

            service.modify(modified);
            var retrieved = service.findById(modified.personId());

            if (retrieved.isPresent() && retrieved.get().firstName().equals("modified")) {
                service.deleteById(modified.personId());
                return TestResult.succeed();
            } else {
                return TestResult.failed("Person not modified");
            }
        } catch (Exception e) {
            return TestResult.failed(e.getMessage());
        }
    }

    private TestResult shouldThrowWhenModifyingInvalidPerson() {
        try {
            var all = service.findAll();
            if (all.isEmpty()) return TestResult.failed("No persons found");
            var existingId = all.getFirst().personId();
            var person = new Person(existingId, "John", "Doe", "123456789",
                    "invalid-email", "12345678910", Type.EXTERNAL);
            service.modify(person);
            return TestResult.failed("Expected exception");
        } catch (Exception e) {
            return TestResult.succeed();
        }
    }

    // ------------------ DELETE ------------------

    private TestResult shouldDeleteById() {

        var person = service.create(Type.INTERNAL, "deleted", "deleted", "123456789",
                "12345678910", "deleted@example.com");

        service.deleteById(person.personId());

        var deleted = service.findById(person.personId());

        if (deleted.isEmpty()) {
            return TestResult.succeed();
        }
        return TestResult.failed("Person not deleted");
    }

    private TestResult shouldFailWhenDeletingByInvalidId() {
        boolean removed = service.deleteById(UUID.randomUUID());
        if (!removed) {
            return TestResult.succeed();
        }
        return TestResult.failed("Deleted non-existent person");
    }


}
