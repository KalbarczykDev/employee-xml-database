import main.java.repository.PersonXmlRepository;
import main.java.service.PersonService;
import test.java.service.PersonServiceTest;

void main() {
    var service = new PersonService(new PersonXmlRepository());
    var test = new PersonServiceTest(service);
    test.runTests();
}



