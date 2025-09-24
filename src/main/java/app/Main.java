import main.java.model.Person;
import main.java.model.Type;
import main.java.repository.PersonXmlRepository;

void main() throws IOException {

    var repo = new PersonXmlRepository();

    var person = new Person("1", "John", "Doe", "123456789", "john.doe@example.com", "12345678901", Type.EXTERNAL);

    // --- TEST CREATE ---
    repo.create(person);
    System.out.println("Created person: " + person);

    // --- TEST FIND BY ID ---
    var foundById = repo.findById("1");
    System.out.println("Found by ID: " + foundById);

    // --- TEST FIND WITH ATTRIBUTES ---
    var found = repo.find(Type.EXTERNAL, "John", "Doe", null, null, null);
    System.out.println("Found by attributes: " + found);

    // --- TEST MODIFY ---
    Person modified = new Person("1", "Johnny", "Doe", "987654321", "johnny.doe@example.com", "12345678901", Type.EXTERNAL);
    repo.modify(modified);
    System.out.println("Modified person: " + modified);

    // --- TEST FIND ALL ---
    var all = repo.findAll();
    System.out.println("All persons: " + all);

    // --- TEST REMOVE ---
   // boolean removed = repo.remove("1");
   // System.out.println("Removed person: " + removed);
}


