package pl.zimi.flashcards.flashcard;

import lombok.RequiredArgsConstructor;
import pl.zimi.flashcards.deck.DeckId;
import pl.zimi.flashcards.strategy.ExpotentialMemorizationStrategy;
import pl.zimi.flashcards.user.UserId;
import pl.zimi.repository.query.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FlashcardService {

    final FlashcardRepository flashcardRepository;

    final Clock clock;

    public Optional<Question> next(UserId userId) {
        System.out.println(LocalDateTime.now() + " next");
        final var userFilter = Filters.eq(SFlashcard.flashcard.userId.value, userId.getValue());
        final var useAfterFilter = Filters.lt(SFlashcard.flashcard.memorizationLevel.useAfter, clock.instant());
        final var useAfterIsNullFilter = Filters.isNull(SFlashcard.flashcard.memorizationLevel.useAfter);
        final var filter = Filters.and(userFilter, Filters.or(useAfterFilter, useAfterIsNullFilter));

        final var query = Queries.filter(filter);
        final var found = flashcardRepository.find(query);
        return found.stream()
                .sorted(Comparator.comparingInt(flashcard -> flashcard.getMemorizationLevel().getNumberOfSuccesses()))
                .findFirst()
                .map(flashcard -> Question.builder().flashcardId(flashcard.getId()).original(flashcard.getOriginal()).translation(flashcard.getTranslation()).build());
    }

    public Flashcard add(AddFlashcardRequest request) {
        System.out.println(LocalDateTime.now() + " add");
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
        System.out.println(LocalDateTime.now() + " answer");
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
        System.out.println(LocalDateTime.now() + " listDeck");
        Query query = Query.builder().filter(Filters.eq(SFlashcard.flashcard.deckId.value, deckId.getValue())).build();
        return flashcardRepository.find(query);
    }
}
