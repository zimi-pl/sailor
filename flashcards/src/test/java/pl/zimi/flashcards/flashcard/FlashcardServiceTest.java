package pl.zimi.flashcards.flashcard;

import org.junit.jupiter.api.Test;
import pl.zimi.clock.ClockManipulator;
import pl.zimi.flashcards.user.UserFixture;
import pl.zimi.repository.contract.MemoryPort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlashcardServiceTest {

    @Test
    void shouldReturnNext() {
        // given
        FlashcardRepository flashcardRepository = MemoryPort.port(FlashcardRepository.class);
        ClockManipulator clockManipulator = ClockManipulator.managable();
        FlashcardService flashcardService = new FlashcardService(flashcardRepository, clockManipulator.getClock());
        var flashcard = FlashcardFixture.someFlashcard();

        var saved = flashcardService.add(flashcard);

        // when
        var returned = flashcardService.next(saved.getUserId());

        // then
        assertEquals(saved, returned.get());
    }

    @Test
    void shouldReturnNullForOtherUser() {
        // given
        FlashcardRepository flashcardRepository = MemoryPort.port(FlashcardRepository.class);
        ClockManipulator clockManipulator = ClockManipulator.managable();
        FlashcardService flashcardService = new FlashcardService(flashcardRepository, clockManipulator.getClock());
        var flashcard = FlashcardFixture.someFlashcard();

        var saved = flashcardService.add(flashcard);

        // when
        var returned = flashcardService.next(UserFixture.someUserId());

        // then
        assertEquals(Optional.empty(), returned);
    }

    @Test
    void shouldReturnFlashcardWhichHasLowerMemorizationLevel() {
        // given
        FlashcardRepository flashcardRepository = MemoryPort.port(FlashcardRepository.class);
        ClockManipulator clockManipulator = ClockManipulator.managable();
        FlashcardService flashcardService = new FlashcardService(flashcardRepository, clockManipulator.getClock());
        var better = FlashcardFixture.someFlashcardBuilder()
                .memorizationLevel(MemorizationLevel.level(1))
                .build();

        var worse = FlashcardFixture.someFlashcardBuilder()
                .memorizationLevel(MemorizationLevel.none())
                .userId(better.getUserId())
                .build();

        flashcardService.add(better);
        var worseSaved = flashcardService.add(worse);

        // when
        var returned = flashcardService.next(better.getUserId());

        // then
        assertEquals(worseSaved, returned.get());
    }

    @Test
    void shouldUpgradeMemorizationLevelAfterCorrectAnswer() {
        // given
        FlashcardRepository flashcardRepository = MemoryPort.port(FlashcardRepository.class);
        ClockManipulator clockManipulator = ClockManipulator.managable();
        FlashcardService flashcardService = new FlashcardService(flashcardRepository, clockManipulator.getClock());
        var flashcard = FlashcardFixture.someFlashcardBuilder()
                .memorizationLevel(MemorizationLevel.none())
                .build();

        final var saved = flashcardService.add(flashcard);

        final var answer = Answer.builder()
                .flashcardId(saved.getId())
                .translation(saved.getTranslation())
                .build();

        // when
        var returned = flashcardService.answer(answer);

        // then
        assertEquals(AnswerResult.correct(), returned);
        final var upgraded = flashcardRepository.findById(saved.id).get();
        assertEquals(1, upgraded.getMemorizationLevel().getNumberOfSuccesses());
    }

    @Test
    void shouldDowngradeMemorizationLevelAfterWrongAnswer() {
        // given
        FlashcardRepository flashcardRepository = MemoryPort.port(FlashcardRepository.class);
        ClockManipulator clockManipulator = ClockManipulator.managable();
        FlashcardService flashcardService = new FlashcardService(flashcardRepository, clockManipulator.getClock());
        var flashcard = FlashcardFixture.someFlashcardBuilder()
                .memorizationLevel(MemorizationLevel.level(5))
                .build();

        final var saved = flashcardService.add(flashcard);

        final var answer = Answer.builder()
                .flashcardId(saved.getId())
                .translation("bad answer")
                .build();

        // when
        var returned = flashcardService.answer(answer);

        // then
        assertEquals(AnswerResult.mistake(), returned);
        assertEquals(MemorizationLevel.none(), flashcardRepository.findById(saved.id).get().getMemorizationLevel());
    }

    @Test
    void shouldFailOnMissingFlashcardIdInAnswer() {
        // given
        FlashcardRepository flashcardRepository = MemoryPort.port(FlashcardRepository.class);
        ClockManipulator clockManipulator = ClockManipulator.managable();
        FlashcardService flashcardService = new FlashcardService(flashcardRepository, clockManipulator.getClock());

        final var answer = Answer.builder()
                .flashcardId("missing-id")
                .translation("bad answer")
                .build();

        // when
        var returned = flashcardService.answer(answer);

        // then
        assertEquals(AnswerResult.failure(), returned);
    }

    @Test
    void shouldNotShowNextMessageWhenMemorizationLevelIsNotExceeded() {
        FlashcardRepository flashcardRepository = MemoryPort.port(FlashcardRepository.class);
        ClockManipulator clockManipulator = ClockManipulator.managable();
        FlashcardService flashcardService = new FlashcardService(flashcardRepository, clockManipulator.getClock());

        final var flashcardScenarios = new FlashcardScenarios(flashcardService);

        final var flashcard = flashcardScenarios.addFlashcard();
        flashcardScenarios.answerCorrectly(flashcard);

        clockManipulator.addMinutes(2);

        // when
        final var next = flashcardService.next(flashcard.getUserId());

        // then
        assertTrue(next.isEmpty());
    }

    @Test
    void shouldShowNextMessageWhenMemorizationLevelIsExceeded() {
        FlashcardRepository flashcardRepository = MemoryPort.port(FlashcardRepository.class);
        ClockManipulator clockManipulator = ClockManipulator.managable();
        FlashcardService flashcardService = new FlashcardService(flashcardRepository, clockManipulator.getClock());

        final var flashcardScenarios = new FlashcardScenarios(flashcardService);

        final var flashcard = flashcardScenarios.addFlashcard();
        flashcardScenarios.answerCorrectly(flashcard);

        clockManipulator.addMinutes(5).addSeconds(1);

        // when
        final var next = flashcardService.next(flashcard.getUserId());

        // then
        assertTrue(next.isPresent());
        assertEquals(next.get().getId(), flashcard.getId() );
    }

    @Test
    void shouldUseIncreasePeriodAfterConsecutiveSuccesses() {
        FlashcardRepository flashcardRepository = MemoryPort.port(FlashcardRepository.class);
        ClockManipulator clockManipulator = ClockManipulator.managable();
        FlashcardService flashcardService = new FlashcardService(flashcardRepository, clockManipulator.getClock());

        final var flashcardScenarios = new FlashcardScenarios(flashcardService);

        final var flashcard = flashcardScenarios.addFlashcard();
        flashcardScenarios.answerCorrectly(flashcard);
        clockManipulator.addMinutes(6);

        flashcardScenarios.answerCorrectly(flashcard);
        clockManipulator.addMinutes(6);

        // when
        final var next = flashcardService.next(flashcard.getUserId());

        // then
        assertTrue(next.isEmpty());
    }

}