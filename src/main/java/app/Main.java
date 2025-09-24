import main.java.repository.PersonXmlRepository;
import main.java.service.PersonService;

void main() {
    var service = new PersonService(
            new PersonXmlRepository()
    );
}




