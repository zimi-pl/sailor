package pl.zimi.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import pl.zimi.client.HttpClient;
import pl.zimi.client.Request;
import pl.zimi.client.RequestBuilder;
import pl.zimi.client.Response;
import pl.zimi.repository.contract.Executable;

import java.util.*;
import java.util.function.Supplier;

public class ServerVerificator {

    public static void assertThese(final Supplier<Server> serverSupplier) {
        for (final Test single : test(serverSupplier)) {
            single.runnable.run();
        }
    }

    private static List<Test> test(final Supplier<Server> serverSupplier) {
        final List<Test> list = Arrays.asList(
                new Test("getNoArg", () -> getNoArg(serverSupplier.get())),
                new Test("getId", () -> getId(serverSupplier.get())),
                new Test("getIdObject", () -> getIdObject(serverSupplier.get())),
                new Test("getIdLong", () -> getIdLong(serverSupplier.get())),
                new Test("getIdLongInObject", () -> getIdLongInObject(serverSupplier.get())),
                new Test("getIdInteger", () -> getIdInteger(serverSupplier.get())),
                new Test("getIdIntegerInObject", () -> getIdIntegerInObject(serverSupplier.get()))
        );
        final List<Test> tests = new ArrayList<>(list);

//        if (contract.getVersion() != null) {
//            tests.add(new Test("versionContract", () -> versionContract(supplier.apply(contract), contract.getEntityClass(), contract.getVersion())));
//            tests.add(new Test("versionContractNextValue", () -> versionContractNextValue(supplier.apply(contract), contract.getEntityClass(), contract.getVersion())));
//            tests.add(new Test("versionContractOptimisticLock", () -> versionContractOptimisticLock(supplier.apply(contract), contract.getEntityClass(), contract.getId(), contract.getVersion())));
//        }
        Collections.shuffle(tests);
        return tests;
    }

    static class Test {
        public String name;
        public Runnable runnable;

        Test(final String name, final Runnable runnable) {
            this.name = name;
            this.runnable = runnable;
        }
    }

    static class Some {
        String a;
        String b;
        Long c;

        public Some(String a, String b, Long c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    public static <T> void getNoArg(final Server server) {
        // given
        Endpoint endpoint = Endpoint.get()
                .path("/")
                .requestClass(Void.class)
                .handler(v -> new Some("a", "b", 0L))
                .scheme(new NoArgScheme())
                .build();

        server.setupEndpoint(endpoint);

        Request request = RequestBuilder.get("/").build();

        // when
        Response response = server.handleRequest(request);

        // then
        JsonElement expected = JsonParser.parseString("{'a' : 'a', 'b' : 'b', 'c': 0}");
        assertEquals(expected, response.json());
    }

    public static <T> void getId(final Server server) {
        // given
        Endpoint endpoint = Endpoint.get()
                .path("/some/{id}")
                .requestClass(String.class)
                .handler(v -> new Some((String)v, "b", 0L))
                .scheme(new IdScheme())
                .build();

        server.setupEndpoint(endpoint);

        Request request = RequestBuilder.get("/some/hello").build();

        // when
        Response response = server.handleRequest(request);

        // then
        JsonElement expected = JsonParser.parseString("{'a' : 'hello', 'b' : 'b', 'c': 0}");
        assertEquals(expected, response.json());
    }

    public static class UserId {
        String value;

        public UserId(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static <T> void getIdObject(final Server server) {
        // given
        Endpoint endpoint = Endpoint.get()
                .path("/some/{id}")
                .requestClass(UserId.class)
                .handler(v -> new Some(((UserId)v).getValue(), "b", 0L))
                .scheme(new IdScheme())
                .build();

        server.setupEndpoint(endpoint);

        Request request = RequestBuilder.get("/some/hello").build();

        // when
        Response response = server.handleRequest(request);

        // then
        JsonElement expected = JsonParser.parseString("{'a' : 'hello', 'b' : 'b', 'c': 0}");
        assertEquals(expected, response.json());
    }

    public static <T> void getIdLong(final Server server) {
        // given
        Endpoint endpoint = Endpoint.get()
                .path("/some/{id}")
                .requestClass(Long.class)
                .handler(v -> new Some("a", "b", (Long)v))
                .scheme(new IdScheme())
                .build();

        server.setupEndpoint(endpoint);

        Request request = RequestBuilder.get("/some/10").build();

        // when
        Response response = server.handleRequest(request);

        // then
        JsonElement expected = JsonParser.parseString("{'a' : 'a', 'b' : 'b', 'c': 10}");
        assertEquals(expected, response.json());
    }

    public static class UserLongId {
        Long value;

        public UserLongId(Long value) {
            this.value = value;
        }

        public Long getValue() {
            return value;
        }
    }

    public static <T> void getIdLongInObject(final Server server) {
        // given
        Endpoint endpoint = Endpoint.get()
                .path("/some/{id}")
                .requestClass(UserLongId.class)
                .handler(v -> new Some("a", "b", ((UserLongId)v).getValue()))
                .scheme(new IdScheme())
                .build();

        server.setupEndpoint(endpoint);

        Request request = RequestBuilder.get("/some/10").build();

        // when
        Response response = server.handleRequest(request);

        // then
        JsonElement expected = JsonParser.parseString("{'a' : 'a', 'b' : 'b', 'c': 10}");
        assertEquals(expected, response.json());
    }


    public static <T> void getIdInteger(final Server server) {
        // given
        Endpoint endpoint = Endpoint.get()
                .path("/some/{id}")
                .requestClass(Long.class)
                .handler(v -> new Some("a", "b", (Long)v))
                .scheme(new IdScheme())
                .build();

        server.setupEndpoint(endpoint);

        Request request = RequestBuilder.get("/some/10").build();

        // when
        Response response = server.handleRequest(request);

        // then
        JsonElement expected = JsonParser.parseString("{'a' : 'a', 'b' : 'b', 'c': 10}");
        assertEquals(expected, response.json());
    }

    public static class UserIntegerId {
        Integer value;

        public UserIntegerId(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    public static <T> void getIdIntegerInObject(final Server server) {
        // given
        Endpoint endpoint = Endpoint.get()
                .path("/some/{id}")
                .requestClass(UserIntegerId.class)
                .handler(v -> new Some("a", "b", ((UserIntegerId)v).getValue().longValue()))
                .scheme(new IdScheme())
                .build();

        server.setupEndpoint(endpoint);

        Request request = RequestBuilder.get("/some/10").build();

        // when
        Response response = server.handleRequest(request);

        // then
        JsonElement expected = JsonParser.parseString("{'a' : 'a', 'b' : 'b', 'c': 10}");
        assertEquals(expected, response.json());
    }

    private static <T> T assertThrows(final Class<T> clazz, final Executable executable) {
        try {
            executable.execute();
            throw new RuntimeException("Exception was not thrown.");
        } catch (final Exception ex) {
            if (clazz.isInstance(ex)) {
                return (T) ex;
            } else {
                throw new RuntimeException("Unexpected type of exception", ex);
            }
        }
    }

    private static void assertTrue(final boolean test) {
        if (!test) {
            throw new RuntimeException("Condition not satisfied");
        }
    }

    private static void assertEquals(final Object expected, final Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new RuntimeException("Values are not equals, expected: " + expected + ", actual: " + actual);
        }
    }

    private static void assertNotEquals(final Object expected, final Object actual) {
        if (Objects.equals(expected, actual)) {
            throw new RuntimeException("Values are equals but they should not");
        }
    }

    private static void assertNotNull(final Object value) {
        if (value == null) {
            throw new RuntimeException("value is null");
        }
    }

}
