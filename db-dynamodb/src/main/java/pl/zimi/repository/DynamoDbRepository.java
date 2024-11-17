package pl.zimi.repository;

import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.contract.OptimisticLockException;
import pl.zimi.repository.contract.UnsupportedFeatureException;
import pl.zimi.repository.manipulation.Manipulator;
import pl.zimi.repository.manipulation.Value;
import pl.zimi.repository.query.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.paginators.ScanIterable;

import java.time.Instant;
import java.util.*;

public class DynamoDbRepository<T> implements Repository<T> {

    private final Contract<T> contract;
    private final DynamoDbClient dynamoDbClient;
    private final String dbPrefix;

    public DynamoDbRepository(Contract<T> contract, DynamoDbClient dynamoDbClient, String dbPrefix) {
        this.contract = contract;
        this.dynamoDbClient = dynamoDbClient;
        this.dbPrefix = dbPrefix;
    }

    @Override
    public T save(T entity) {
        T copy = Manipulator.deepCopy(entity);
        Object initVersion = null;
        if (contract.getVersion() != null) {
            Object object = Manipulator.get(copy, contract.getVersion()).getObject();
            initVersion = object;
            if (object != null) {
                Object toSet = null;
                if (object instanceof Integer) {
                    toSet = ((Integer) object) + 1;
                } else if (object instanceof Long) {
                    toSet = ((Long) object) + 1;
                }
                Manipulator.set(copy, contract.getVersion(), toSet);
            } else {
                Manipulator.set(copy, contract.getVersion(), 0);
            }

        }
        boolean isNew = false;
        Map<String, AttributeValue> itemValues = RepresentationBuilder.build(copy);
        if (contract.getId() != null) {
            Value value = Manipulator.get(copy, contract.getId());
            if (value.getObject() == null) {
                String uuid = UUID.randomUUID().toString();
                itemValues.put("id", AttributeValue.builder().s(uuid).build());
                Manipulator.set(copy, contract.getId(), uuid);
                isNew = true;
            }
        }
        String tableName = prepareTableName();
        PutItemRequest.Builder builder = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues);
        if (contract.getVersion() != null) {
            if (!isNew) {
                Map<String, AttributeValue> map = new HashMap<>();
                map.put(":version", AttributeValue.builder().n(initVersion.toString()).build());
                builder.conditionExpression("version = :version")
                        .expressionAttributeValues(map);
            }
        }
        PutItemRequest request = builder.build();

        try {
            dynamoDbClient.putItem(request);
            return copy;
        } catch (ConditionalCheckFailedException e) {
            throw new OptimisticLockException("", e);
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DynamoDbException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<T> findById(Object id) {
        HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();

        keyToGet.put("id", AttributeValue.builder().s((String) id).build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(prepareTableName())
                .build();

        try {
            Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(request).item();
            if (returnedItem.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(EntityBuilder.build(returnedItem, contract.getEntityClass()));
        } catch (DynamoDbException e) {
            throw new RuntimeException(e);
        }
    }

    private String prepareTableName() {
        return dbPrefix + this.contract.getEntityClass().getSimpleName();
    }

    class DynamoDBExpression {
        int counter;
        Map<String, String> attributeNames;
        Map<String, AttributeValue> attributeValueMap;
        String expression;

    }

    private DynamoDBExpression prepareFilterExpression(Filter filter) {
        DynamoDBExpression expression = new DynamoDBExpression();
        expression.counter = 0;
        expression.attributeValueMap = new HashMap<>();
        expression.attributeNames = new HashMap<>();
        expression.expression = prepareFilterExpression(filter, expression);
        return expression;
    }

    private String prepareFilterExpression(Filter filter, DynamoDBExpression expression) {
        if (filter instanceof ConjunctionFilter) {
            ConjunctionFilter conjunctionFilter = (ConjunctionFilter) filter;
            String firstFilter = prepareFilterExpression(conjunctionFilter.getFirst(), expression);
            String secondFilter = prepareFilterExpression(conjunctionFilter.getSecond(), expression);
            return "(" + firstFilter + " " + conjunctionFilter.getOperator() + " " + secondFilter + ")";
        } else {
            BasicFilter innerFilter = (BasicFilter) filter;
            String[] parts = innerFilter.getPath().split("\\.");
            List<String> newParts = new ArrayList<>();
            for (String part : parts) {
                String attributeNameKey = attributeNameKey(expression, part);
                expression.attributeNames.put(attributeNameKey, part);
                expression.counter++;
                newParts.add(attributeNameKey);
            }
            String newAttribute = String.join(".", newParts);
            String attributeValueKey = null;
            if (!innerFilter.getOperator().equals(Operator.IS_NULL)) {
                AttributeValue av = null;
                if (innerFilter.getExpectedValue() instanceof Long) {
                    av = AttributeValue.builder().n(innerFilter.getExpectedValue().toString()).build(); // Using Java 9+ Map.of
                } else if (innerFilter.getExpectedValue() instanceof Integer) {
                    av = AttributeValue.builder().n(innerFilter.getExpectedValue().toString()).build(); // Using Java 9+ Map.of
                } else if (innerFilter.getExpectedValue() instanceof Instant) {
                    av = AttributeValue.builder().s(innerFilter.getExpectedValue().toString()).build();
                } else {
                    av = AttributeValue.builder().s((String) innerFilter.getExpectedValue()).build(); // Using Java 9+ Map.of
                }
                attributeValueKey = attributeValueKey(expression, innerFilter.getPath());
                expression.attributeValueMap.put(attributeValueKey, av);
            }
            String stringExpression = operator(innerFilter.getOperator(), newAttribute, attributeValueKey);
            expression.counter++;
            return stringExpression;
        }
    }

    private String attributeValueKey(DynamoDBExpression expression, String path) {
        return ":" + escapeReservedName(path.replace(".", "_")) + expression.counter;
    }

    private String attributeNameKey(DynamoDBExpression expression, String path) {
        return "#" + escapeReservedName(path.replace(".", "_")) + expression.counter;
    }

    private String operator(Operator operator, String newAttribute, String attributeValueKey) {
        switch (operator) {
            case LOWER_THAN:
                return newAttribute + " < " + attributeValueKey;
            case GREATER_THAN:
                return newAttribute + " > " + attributeValueKey;
            case EQUAL:
                return newAttribute + " = " + attributeValueKey;
            case IS_NULL:
                return "attribute_not_exists(" + newAttribute + ")";
            case REGEX:
                throw new UnsupportedFeatureException("Regex");
        }
        throw new RuntimeException("Unknown operator: " + operator);
    }

    String escapeReservedName(String name) {
        List<String> reserved = Arrays.asList("value");
        if (reserved.contains(name)) {
            return name + "___";
        } else {
            return name;
        }
    }

    @Override
    public List<T> find(Query query) {
        if (query.getSorter() != null) {
            throw new UnsupportedFeatureException("Sorting");
        }
        if (Optional.ofNullable(query.getLimitOffset()).map(LimitOffset::getOffset).orElse(null) != null) {
            throw new UnsupportedFeatureException("Offset");
        }

        ScanRequest.Builder builder = ScanRequest
                .builder()
                .tableName(prepareTableName());

        if (query.getFilter() != null) {
            DynamoDBExpression filterExpression = prepareFilterExpression(query.getFilter());
            builder
                    .filterExpression(filterExpression.expression)
                    .expressionAttributeNames(filterExpression.attributeNames);
            if (!filterExpression.attributeValueMap.isEmpty()) {
                builder
                        .expressionAttributeValues(filterExpression.attributeValueMap);
            }
        }

        if (query.getLimitOffset() != null) {
            builder.limit(query.getLimitOffset().getLimit().intValue());
        }

        ScanRequest request = builder.build();
        ScanIterable response = dynamoDbClient.scanPaginator(request);
        List<T> result = new ArrayList<>();
        Iterator<ScanResponse> iterator = response.iterator();
        if (iterator.hasNext()) {
            for (Map<String, AttributeValue> item : iterator.next().items()) {
                T entity = EntityBuilder.build(item, contract.getEntityClass());
                result.add(entity);
            }
        }
        return result;
    }

    @Override
    public T delete(T entity) {

        HashMap<String, AttributeValue> keyToGet = new HashMap<>();

        keyToGet.put("id", AttributeValue.builder()
                .s((String) Manipulator.get(entity, contract.getId()).getObject())
                .build());

        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName(prepareTableName())
                .key(keyToGet)
                .build();

        try {
            dynamoDbClient.deleteItem(deleteReq);
            return entity;
        } catch (DynamoDbException e) {
            throw new RuntimeException(e);
        }
    }
}
