import main.repository.PersonXmlRepository;
import main.service.PersonService;
import test.service.PersonServiceTest;

void main() {
    var service = new PersonService(new PersonXmlRepository());
    var test = new PersonServiceTest(service);
    test.runTests();

}



