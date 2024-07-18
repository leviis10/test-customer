package enigma.customer;

import enigma.customer.controller.TransactionControllerTests;
import enigma.customer.repository.CustomerRepositoryTests;
import enigma.customer.service.CustomerServiceTests;
import enigma.customer.service.TransactionServiceTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        TransactionControllerTests.class,
        CustomerRepositoryTests.class,
        CustomerServiceTests.class,
        TransactionServiceTests.class
})
public class TestSuite {
}
