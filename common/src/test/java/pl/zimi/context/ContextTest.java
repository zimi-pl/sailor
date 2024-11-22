package pl.zimi.context;

import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.*;

class ContextTest {

    @Test
    void shouldProvideBean() {
        // given
        Context context = Context.create();

        // when
        SomeService someService = context.getBean(SomeService.class);

        // then
        assertNotNull(someService);
        assertEquals(SomeService.class, someService.getClass());
    }

    @Test
    void shouldMoreComplicatedBean() {
        // given
        Context context = Context.create();

        // when
        SomeOuterService someOuterService = context.getBean(SomeOuterService.class);

        // then
        assertNotNull(someOuterService);
        assertEquals(SomeOuterService.class, someOuterService.getClass());
    }

    @Test
    void shouldRegister() {
        // given
        Context context = Context.create();
        context.register(Clock.class, Clock.systemUTC());

        // when
        final SomeClockBasedService someClockBasedService = context.getBean(SomeClockBasedService.class);

        // then
        assertNotNull(someClockBasedService);
        assertEquals(SomeClockBasedService.class, someClockBasedService.getClass());
    }

}