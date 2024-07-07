package pl.zimi.flashcards.flashcard;

import org.junit.jupiter.api.Test;
import pl.zimi.clock.ClockManipulator;
import pl.zimi.flashcards.user.UserFixture;
import pl.zimi.repository.contract.MemoryPort;

import java.time.Clock;
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

        var saved = flashcardService.add(AddFlashcardRequestFixture.someAddFlashcardRequest());

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

        var saved = flashcardService.add(AddFlashcardRequestFixture.someAddFlashcardRequest());

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

        final var flashcardScenarios = new FlashcardScenarios(flashcardService);

        final var better = flashcardScenarios.addFlashcard();

        flashcardScenarios.answerCorrectly(better);

        final var worse = flashcardScenarios.addFlashcardForSameUser(better);

        clockManipulator.addMinutes(6);

        // when
        var returned = flashcardService.next(better.getUserId());

        // then
        assertEquals(worse, returned.get());
    }

    @Test
    void shouldUpgradeMemorizationLevelAfterCorrectAnswer() {
        // given
        FlashcardRepository flashcardRepository = MemoryPort.port(FlashcardRepository.class);
        ClockManipulator clockManipulator = ClockManipulator.managable();
        FlashcardService flashcardService = new FlashcardService(flashcardRepository, clockManipulator.getClock());

        final var saved = flashcardService.add(AddFlashcardRequestFixture.someAddFlashcardRequest());

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

        final var saved = flashcardService.add(AddFlashcardRequestFixture.someAddFlashcardRequest());

        final var flashcardScenarios = new FlashcardScenarios(flashcardService);
        flashcardScenarios.answerCorrectly(saved);

        // when
        var returned = flashcardScenarios.answerBadly(saved);

        // then
        assertEquals(AnswerResult.mistake(), returned);
        assertEquals(0, flashcardRepository.findById(saved.id).get().getMemorizationLevel().getNumberOfSuccesses());
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

    @Test
    void shouldWaitBeforeNextShowAfterFailure() {
        FlashcardRepository flashcardRepository = MemoryPort.port(FlashcardRepository.class);
        ClockManipulator clockManipulator = ClockManipulator.managable();
        FlashcardService flashcardService = new FlashcardService(flashcardRepository, clockManipulator.getClock());

        final var flashcardScenarios = new FlashcardScenarios(flashcardService);

        final var flashcard = flashcardScenarios.addFlashcard();

        flashcardScenarios.answerBadly(flashcard);
        clockManipulator.addMinutes(4);

        // when
        final var next = flashcardService.next(flashcard.getUserId());

        // then
        assertTrue(next.isEmpty());
    }

    @Test
    void shouldCountViewsAndSuccesses() {
        FlashcardRepository flashcardRepository = MemoryPort.port(FlashcardRepository.class);
        ClockManipulator clockManipulator = ClockManipulator.managable();
        FlashcardService flashcardService = new FlashcardService(flashcardRepository, clockManipulator.getClock());

        final var flashcardScenarios = new FlashcardScenarios(flashcardService);

        final var flashcard = flashcardScenarios.addFlashcard();

        flashcardScenarios.answerCorrectly(flashcard);
        clockManipulator.addMinutes(6);

        flashcardScenarios.answerCorrectly(flashcard);
        clockManipulator.addMinutes(11);

        flashcardScenarios.answerBadly(flashcard);
        clockManipulator.addMinutes(6);

        flashcardScenarios.answerCorrectly(flashcard);
        // when
        final var next = flashcardRepository.findById(flashcard.getId()).get();

        // then
        assertEquals(4, next.getMemorizationLevel().getNumberOfAnswers());
        assertEquals(1, next.getMemorizationLevel().getNumberOfSuccesses());
    }

}