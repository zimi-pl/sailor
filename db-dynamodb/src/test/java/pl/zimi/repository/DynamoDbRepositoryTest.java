package pl.zimi.repository;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.contract.ContractVerificator;
import pl.zimi.repository.example.Foo;
import pl.zimi.repository.example.SFoo;
import pl.zimi.repository.query.Queries;
import pl.zimi.repository.query.Query;
import pl.zimi.repository.query.Repository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class DynamoDbRepositoryTest {

    private static LocalStackContainer localstack;

    private static DynamoDbClient client;

    @BeforeAll
    public static void init() {
        localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.11.3"))
                .withServices(LocalStackContainer.Service.DYNAMODB);
        localstack.start();

        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.builder()
                        .accessKeyId(localstack.getAccessKey())
                        .secretAccessKey(localstack.getSecretKey()).build()
        );

        client = DynamoDbClient.builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB))
                .region(Region.of(localstack.getRegion()))
                .credentialsProvider(credentialsProvider)
                .build();

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName("flashcards-dev-Foo")
                .attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName("id")
                                .attributeType(ScalarAttributeType.S)
                                .build()
                )
                .keySchema(
                        KeySchemaElement.builder()
                                .attributeName("id")
                                .keyType(KeyType.HASH)
                                .build()
                )
                .provisionedThroughput(
                        ProvisionedThroughput.builder()
                                .readCapacityUnits(10L)
                                .writeCapacityUnits(5L)
                                .build()

                )
                .build();

        client.createTable(request);
    }
    @AfterAll
    public static void close() {
        localstack.close();
    }

    @TestFactory
    List<DynamicTest> repositoryTest() {
        final Contract<Foo> contract = Contract.repository(Foo.class).id(SFoo.foo.id).version(SFoo.foo.version);


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
