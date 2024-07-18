package pl.zimi.flashcards.flashcard;

import org.junit.jupiter.api.Test;
import pl.zimi.clock.ClockManipulator;
import pl.zimi.flashcards.App;
import pl.zimi.flashcards.user.UserFixture;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlashcardServiceTest {

    @Test
    void shouldReturnNext() {
        // given
        final var app = App.createApp();
        FlashcardService flashcardService = app.getBean(FlashcardService.class);

        var saved = flashcardService.add(AddFlashcardRequestFixture.someAddFlashcardRequest());

        // when
        var returned = flashcardService.next(saved.getUserId());

        // then
        assertEquals(new Question(saved.getId(), saved.getOriginal()), returned.get());
    }

    @Test
    void shouldReturnNullForOtherUser() {
        // given
        final var app = App.createApp();
        FlashcardService flashcardService = app.getBean(FlashcardService.class);

        var saved = flashcardService.add(AddFlashcardRequestFixture.someAddFlashcardRequest());

        // when
        var returned = flashcardService.next(UserFixture.someUserId());

        // then
        assertEquals(Optional.empty(), returned);
    }

    @Test
    void shouldReturnFlashcardWhichHasLowerMemorizationLevel() {
        // given
        final var app = App.createApp();
        FlashcardService flashcardService = app.getBean(FlashcardService.class);

        final var flashcardScenarios = app.getBean(FlashcardScenarios.class);

        final var better = flashcardScenarios.addFlashcard();

        flashcardScenarios.answerCorrectly(better);

        final var worse = flashcardScenarios.addFlashcardForSameUser(better);

        final var clockManipulator = app.getBean(ClockManipulator.class);
        clockManipulator.addMinutes(6);

        // when
        var returned = flashcardService.next(better.getUserId());

        // then
        assertEquals(new Question(worse.getId(), worse.getOriginal()), returned.get());
    }

    @Test
    void shouldUpgradeMemorizationLevelAfterCorrectAnswer() {
        // given
        final var app = App.createApp();
        FlashcardService flashcardService = app.getBean(FlashcardService.class);
        FlashcardRepository flashcardRepository = app.getBean(FlashcardRepository.class);
        FlashcardScenarios flashcardScenarios = app.getBean(FlashcardScenarios.class);

        final var saved = flashcardScenarios.addFlashcard();

        // when
        final var returned = flashcardScenarios.answerCorrectly(saved);

        // then
        assertEquals(AnswerResult.correct(), returned);
        final var upgraded = flashcardRepository.findById(saved.id).get();
        assertEquals(1, upgraded.getMemorizationLevel().getNumberOfSuccesses());
    }

    @Test
    void shouldDowngradeMemorizationLevelAfterWrongAnswer() {
        // given
        final var app = App.createApp();
        FlashcardService flashcardService = app.getBean(FlashcardService.class);
        FlashcardScenarios flashcardScenarios = app.getBean(FlashcardScenarios.class);
        FlashcardRepository flashcardRepository = app.getBean(FlashcardRepository.class);

        final var saved = flashcardScenarios.addFlashcard();
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
        final var app = App.createApp();
        FlashcardService flashcardService = app.getBean(FlashcardService.class);

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
        final var app = App.createApp();
        FlashcardService flashcardService = app.getBean(FlashcardService.class);
        FlashcardScenarios flashcardScenarios = app.getBean(FlashcardScenarios.class);
        ClockManipulator clockManipulator = app.getBean(ClockManipulator.class);

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
        final var app = App.createApp();
        FlashcardService flashcardService = app.getBean(FlashcardService.class);
        FlashcardScenarios flashcardScenarios = app.getBean(FlashcardScenarios.class);
        ClockManipulator clockManipulator = app.getBean(ClockManipulator.class);

        final var flashcard = flashcardScenarios.addFlashcard();
        flashcardScenarios.answerCorrectly(flashcard);

        clockManipulator.addMinutes(5).addSeconds(1);

        // when
        final var next = flashcardService.next(flashcard.getUserId());

        // then
        assertTrue(next.isPresent());
        assertEquals(next.get().getFlashcardId(), flashcard.getId());
    }

    @Test
    void shouldUseIncreasePeriodAfterConsecutiveSuccesses() {
        final var app = App.createApp();
        FlashcardService flashcardService = app.getBean(FlashcardService.class);
        FlashcardScenarios flashcardScenarios = app.getBean(FlashcardScenarios.class);
        ClockManipulator clockManipulator = app.getBean(ClockManipulator.class);

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
        final var app = App.createApp();
        FlashcardService flashcardService = app.getBean(FlashcardService.class);
        FlashcardScenarios flashcardScenarios = app.getBean(FlashcardScenarios.class);
        ClockManipulator clockManipulator = app.getBean(ClockManipulator.class);

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
        final var app = App.createApp();
        FlashcardScenarios flashcardScenarios = app.getBean(FlashcardScenarios.class);
        ClockManipulator clockManipulator = app.getBean(ClockManipulator.class);
        FlashcardRepository flashcardRepository = app.getBean(FlashcardRepository.class);

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