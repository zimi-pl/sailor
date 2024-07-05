package pl.zimi.flashcards.flashcard;

import org.junit.jupiter.api.Test;
import pl.zimi.flashcards.user.UserFixture;
import pl.zimi.repository.query.Repository;
import pl.zimi.repository.contract.MemoryPort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FlashcardServiceTest {

    @Test
    void shouldReturnNext() {
        // given
        Repository<Flashcard> flashcardRepository = MemoryPort.port(FlashcardContract.CONTRACT);
        FlashcardService flashcardService = new FlashcardService(flashcardRepository);
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
        Repository<Flashcard> flashcardRepository = MemoryPort.port(FlashcardContract.CONTRACT);
        FlashcardService flashcardService = new FlashcardService(flashcardRepository);
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
        Repository<Flashcard> flashcardRepository = MemoryPort.port(FlashcardContract.CONTRACT);
        FlashcardService flashcardService = new FlashcardService(flashcardRepository);
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
        Repository<Flashcard> flashcardRepository = MemoryPort.port(FlashcardContract.CONTRACT);
        FlashcardService flashcardService = new FlashcardService(flashcardRepository);
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
        assertEquals(MemorizationLevel.level(1), flashcardRepository.findById(saved.id).get().getMemorizationLevel());
    }

    @Test
    void shouldDowngradeMemorizationLevelAfterWrongAnswer() {
        // given
        Repository<Flashcard> flashcardRepository = MemoryPort.port(FlashcardContract.CONTRACT);
        FlashcardService flashcardService = new FlashcardService(flashcardRepository);
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
        Repository<Flashcard> flashcardRepository = MemoryPort.port(FlashcardContract.CONTRACT);
        FlashcardService flashcardService = new FlashcardService(flashcardRepository);

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
        Repository<Flashcard> flashcardRepository = MemoryPort.port(FlashcardContract.CONTRACT);
        FlashcardService flashcardService = new FlashcardService(flashcardRepository);

        var flashcard = FlashcardFixture.someFlashcardBuilder()
                .memorizationLevel(MemorizationLevel.level(5))
                .build();

        final var saved = flashcardService.add(flashcard);

        final var answer = Answer.builder()
                .flashcardId(saved.getId())
                .translation("bad answer")
                .build();
        var returned = flashcardService.answer(answer);

        // when
        final var next = flashcardService.next(saved.getUserId());

        // then
        assertEquals(Optional.empty(), next);
    }

}