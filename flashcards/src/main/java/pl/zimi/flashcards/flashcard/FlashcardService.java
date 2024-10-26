package pl.zimi.flashcards.flashcard;

import lombok.RequiredArgsConstructor;
import pl.zimi.flashcards.deck.DeckId;
import pl.zimi.flashcards.strategy.ExpotentialMemorizationStrategy;
import pl.zimi.flashcards.user.UserId;
import pl.zimi.repository.query.*;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FlashcardService {

    final FlashcardRepository flashcardRepository;

    final Clock clock;

    public Optional<Question> next(UserId userId) {
        final var userFilter = Filters.eq(SFlashcard.flashcard.userId, userId);
        final var useAfterFilter = Filters.lt(SFlashcard.flashcard.memorizationLevel.useAfter, clock.instant());
        final var useAfterIsNullFilter = Filters.isNull(SFlashcard.flashcard.memorizationLevel.useAfter);
        final var filter = Filters.and(userFilter, Filters.or(useAfterFilter, useAfterIsNullFilter));

        final var sorter = Sorters.asc(SFlashcard.flashcard.memorizationLevel.numberOfSuccesses);
        final var query = Queries.query(filter, sorter, new LimitOffset(1L, 0L));
        final var found = flashcardRepository.find(query);
        return found.stream()
                .findFirst()
                .map(flashcard -> Question.builder().flashcardId(flashcard.getId()).original(flashcard.getOriginal()).translation(flashcard.getTranslation()).build());
    }

    public Flashcard add(AddFlashcardRequest request) {
        final var flashcard = Flashcard.builder()
                .userId(request.getUserId())
                .original(request.getOriginal())
                .translation(request.getTranslation())
                .memorizationLevel(MemorizationLevel.none())
                .deckId(request.getDeckId())
                .build();
        return flashcardRepository.save(flashcard);
    }

    public AnswerResult answer(Answer answer) {
        final var memorizationStrategy = new ExpotentialMemorizationStrategy();
        final var flashcardOptional = flashcardRepository.findById(answer.getFlashcardId());
        if (flashcardOptional.isEmpty()) {
            return AnswerResult.failure("There is no flashcard with id: " + answer.getFlashcardId());
        }
        final var flashcard = flashcardOptional.get();
        if (flashcard.getTranslation().getText().equals(answer.getTranslation())) {
            Instant useAfter = Optional.ofNullable(flashcard.getMemorizationLevel()).map(MemorizationLevel::getUseAfter).orElse(Instant.MIN);
            if (!useAfter.isBefore(clock.instant())) {
                return AnswerResult.failure("This answer is not count as time have not elapsed for this flashcard.");
            }
            flashcard.memorizationLevel.upgrade(clock.instant(), memorizationStrategy);
            flashcardRepository.save(flashcard);
            return AnswerResult.correct();
        } else {
            flashcard.memorizationLevel.downgrade(clock.instant(), memorizationStrategy);
            flashcardRepository.save(flashcard);
            return AnswerResult.mistake();
        }
    }

    public List<Flashcard> listDeck(DeckId deckId) {
        Query query = Query.builder().filter(Filters.eq(SFlashcard.flashcard.deckId, deckId)).build();
        return flashcardRepository.find(query);
    }
}
