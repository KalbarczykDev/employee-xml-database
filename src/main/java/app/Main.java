import main.java.model.Person;
import main.java.model.Type;
import main.java.repository.PersonXmlRepository;

void main() throws IOException {

    var repository = new PersonXmlRepository();

    seedDB(repository);

}

void seedDB(final PersonXmlRepository repository) {
    for (int i = 0; i < 10; i++) {
        var id = repository.findNextId(Type.EXTERNAL);
        var person = new Person(
                id,
                "ExternalFirst" + i,
                "ExternalLast" + i,
                "60000000" + i,
                "external" + i + "@example.com",
                "9000000000" + i,
                Type.EXTERNAL
        );
        repository.create(person);
        System.out.println("Created: " + person);
    }

    for (int i = 0; i < 10; i++) {
        var id = repository.findNextId(Type.INTERNAL);
        var person = new Person(
                id,
                "InternalFirst" + i,
                "InternalLast" + i,
                "70000000" + i,
                "internal" + i + "@example.com",
                "8000000000" + i,
                Type.INTERNAL
        );
        repository.create(person);
        System.out.println("Created: " + person);
    }
}


