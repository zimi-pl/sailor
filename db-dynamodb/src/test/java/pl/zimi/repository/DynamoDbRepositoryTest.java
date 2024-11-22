package pl.zimi.repository;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.contract.ContractVerificator;
import pl.zimi.repository.example.Foo;
import pl.zimi.repository.example.SFoo;
import pl.zimi.repository.query.Queries;
import pl.zimi.repository.query.Query;
import pl.zimi.repository.query.Repository;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class DynamoDbRepositoryTest {

    @TestFactory
    List<DynamicTest> repositoryTest() {
        final Contract<Foo> contract = Contract.repository(Foo.class).id(SFoo.foo.id).version(SFoo.foo.version);
        DynamoDbClient client = DynamoDbClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();

        String prefix = "flashcards-dev-";
        Function<Contract<Foo>, Repository<Foo>> port = c -> {
            Repository<Foo> port1 = DynamoDbPort.port(c, client, prefix);
            Query query = Queries.all();
            List<Foo> foos = port1.find(query);
            for (Foo foo : foos) {
                port1.delete(foo);
            }
            return port1;
        };
        return ContractVerificator.test(contract, port)
                .stream()
                .map(t -> dynamicTest("ContractVerificator." + t.name, () -> t.runnable.run()))
                .collect(Collectors.toList());
    }

}
