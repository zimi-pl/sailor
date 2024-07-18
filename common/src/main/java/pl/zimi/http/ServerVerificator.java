package pl.zimi.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import pl.zimi.repository.contract.Executable;

import java.util.*;
import java.util.function.Supplier;

public class ServerVerificator {

    public static void assertThese(final Supplier<Server> serverSupplier) {
        Server server = serverSupplier.get();
        for (final Test single : test(server)) {
            single.runnable.run();
        }
    }

    private static List<Test> test(final Server server) {
        final var list = Arrays.asList(
                new Test("get", () -> get(server))
        );
        final var tests = new ArrayList<>(list);

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

        public Some(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    public static <T> void get(final Server server) {
        // given
        Endpoint endpoint = Endpoint.get()
                .path("/")
                .requestClass(Void.class)
                .handler(v -> new Some("a", "b"))
                .scheme(new NoArgScheme())
                .build();

        server.setupEndpoint(endpoint);

        Request request = RequestBuilder.get("/").build();

        // when
        Response response = server.handleRequest(request);

        // then
        JsonParser parser = new JsonParser();
        JsonElement expected = parser.parse("{'a' : 'a', 'b' : 'b'}");
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
