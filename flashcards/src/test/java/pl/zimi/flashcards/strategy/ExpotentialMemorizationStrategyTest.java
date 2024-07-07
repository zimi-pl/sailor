package pl.zimi.flashcards.strategy;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import pl.zimi.flashcards.strategy.ExpotentialMemorizationStrategy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class ExpotentialMemorizationStrategyTest {

    @TestFactory
    List<DynamicTest> repositoryTest() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        return Arrays.asList(
                dynamicTest("Successes " + 0, () -> invokeStrategy(now, 0, now.plus(5, ChronoUnit.MINUTES))),
                dynamicTest("Successes " + 1, () -> invokeStrategy(now, 1, now.plus(10, ChronoUnit.MINUTES))),
                dynamicTest("Successes " + 10, () -> invokeStrategy(now, 10, now.plus(5120, ChronoUnit.MINUTES)))
        );
    }

    void invokeStrategy(Instant now, int successes, Instant next) {
        // given
        final var memorizationStrategy = new ExpotentialMemorizationStrategy();

        // when
        final var result = memorizationStrategy.nextAfter(successes, now);

        // then
        assertEquals(next, result);
    }
}