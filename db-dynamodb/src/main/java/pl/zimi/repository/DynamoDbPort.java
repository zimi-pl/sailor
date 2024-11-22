package pl.zimi.repository;

import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.proxy.ContractException;
import pl.zimi.repository.proxy.ProxyProvider;
import pl.zimi.repository.query.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.lang.reflect.Field;

public class DynamoDbPort {

    public static <T> Repository<T> port(final Contract<T> contract, DynamoDbClient dynamoDbClient, String dbPrefix) {
        return new DynamoDbRepository<T>(contract, dynamoDbClient, dbPrefix);
    }

    public static <T> T port(final Class<T> repositoryClass, DynamoDbClient dynamoDbClient, String dbPrefix) {
        try {
            final Field contractField = repositoryClass.getField("CONTRACT");
            final Contract contract = (Contract)contractField.get(repositoryClass);
            Repository repository = port(contract, dynamoDbClient, dbPrefix);
            return ProxyProvider.provide(repositoryClass, repository);
        } catch (Exception e) {
            throw new ContractException(e);
        }
    }


}
