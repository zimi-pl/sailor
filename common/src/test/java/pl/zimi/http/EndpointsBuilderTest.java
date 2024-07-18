package pl.zimi.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class EndpointsBuilderTest {

    static class Crud {
        String id;
        String fieldA;

        public Crud(String id, String fieldA) {
            this.id = id;
            this.fieldA = fieldA;
        }
    }

    static class Query {
        String id;
        String fieldA;

        public Query(String id, String fieldA) {
            this.id = id;
            this.fieldA = fieldA;
        }
    }

    class InvokeHolder {

        private boolean invoked;

        public boolean isInvoked() {
            return invoked;
        }

        void invoke() {
            invoked = true;
        }
    }

    @Test
    void testFindMethod() {
        // given
        InvokeHolder invoked = new InvokeHolder();

        class CrudService {

            Crud find(String id) {
                invoked.invoke();
                return new Crud(id, "some value");
            }

        }
        CrudService crudService = new CrudService();

        // when
        List<Endpoint> endpoints = EndpointsBuilder.prepareEndpoints(crudService);

        // then
        Assertions.assertThat(endpoints.size()).isEqualTo(1);
        Assertions.assertThat(endpoints.get(0).getMethod()).isEqualTo(HttpMethod.GET);
        Assertions.assertThat(endpoints.get(0).getPath()).isEqualTo("/crud/{id}");
        Assertions.assertThat(endpoints.get(0).getScheme()).isInstanceOf(IdScheme.class);
        Assertions.assertThat(endpoints.get(0).getRequestClass()).isEqualTo(String.class);

        endpoints.get(0).getHandler().apply("someId");

        Assertions.assertThat(invoked.isInvoked()).isTrue();
    }

    @Test
    void testGetMethod() {
        // given
        InvokeHolder invoked = new InvokeHolder();

        class CrudService {

            Crud get(String id) {
                invoked.invoke();
                return new Crud(id, "some value");
            }

        }
        CrudService crudService = new CrudService();

        // when
        List<Endpoint> endpoints = EndpointsBuilder.prepareEndpoints(crudService);

        // then
        Assertions.assertThat(endpoints.size()).isEqualTo(1);
        Assertions.assertThat(endpoints.get(0).getMethod()).isEqualTo(HttpMethod.GET);
        Assertions.assertThat(endpoints.get(0).getPath()).isEqualTo("/crud/{id}");
        Assertions.assertThat(endpoints.get(0).getScheme()).isInstanceOf(IdScheme.class);
        Assertions.assertThat(endpoints.get(0).getRequestClass()).isEqualTo(String.class);

        endpoints.get(0).getHandler().apply("someId");

        Assertions.assertThat(invoked.isInvoked()).isTrue();
    }

    @Test
    void testDeleteMethod() {
        // given
        InvokeHolder invoked = new InvokeHolder();

        class CrudService {

            Crud delete(String id) {
                invoked.invoke();
                return new Crud(id, "some value");
            }

        }
        CrudService crudService = new CrudService();

        // when
        List<Endpoint> endpoints = EndpointsBuilder.prepareEndpoints(crudService);

        // then
        Assertions.assertThat(endpoints.size()).isEqualTo(1);
        Assertions.assertThat(endpoints.get(0).getMethod()).isEqualTo(HttpMethod.DELETE);
        Assertions.assertThat(endpoints.get(0).getPath()).isEqualTo("/crud/{id}");
        Assertions.assertThat(endpoints.get(0).getScheme()).isInstanceOf(IdScheme.class);
        Assertions.assertThat(endpoints.get(0).getRequestClass()).isEqualTo(String.class);

        endpoints.get(0).getHandler().apply("someId");

        Assertions.assertThat(invoked.isInvoked()).isTrue();
    }

    @Test
    void testRemoveMethod() {
        // given
        InvokeHolder invoked = new InvokeHolder();

        class CrudService {

            Crud remove(String id) {
                invoked.invoke();
                return new Crud(id, "some value");
            }

        }
        CrudService crudService = new CrudService();

        // when
        List<Endpoint> endpoints = EndpointsBuilder.prepareEndpoints(crudService);

        // then
        Assertions.assertThat(endpoints.size()).isEqualTo(1);
        Assertions.assertThat(endpoints.get(0).getMethod()).isEqualTo(HttpMethod.DELETE);
        Assertions.assertThat(endpoints.get(0).getPath()).isEqualTo("/crud/{id}");
        Assertions.assertThat(endpoints.get(0).getScheme()).isInstanceOf(IdScheme.class);
        Assertions.assertThat(endpoints.get(0).getRequestClass()).isEqualTo(String.class);

        endpoints.get(0).getHandler().apply("someId");

        Assertions.assertThat(invoked.isInvoked()).isTrue();
    }

}