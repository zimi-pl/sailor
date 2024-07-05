package pl.zimi.flashcards.flashcard;

import lombok.RequiredArgsConstructor;
import pl.zimi.repository.query.*;

import java.util.Optional;

@RequiredArgsConstructor
public class FlashcardService {

    final Repository<Flashcard> flashcardRepository;

    Optional<Flashcard> next(UserId userId) {
        final var filter = Filters.eq(SFlashcard.flashcard.userId, userId);
        final var sorter = Sorters.asc(SFlashcard.flashcard.memorizationLevel.numberOfSuccesses);
        final var query = Queries.query(filter, sorter, new LimitOffset(1L, 0L));
        final var found = flashcardRepository.find(query);
        return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
    }

    public Flashcard add(Flashcard flashcard) {
        return flashcardRepository.save(flashcard);
    }

    public AnswerResult answer(Answer answer) {
        final var flashcards = flashcardRepository.findById(answer.getFlashcardId());
        if (flashcards.isEmpty()) {
            return AnswerResult.failure();
        }
        final var flashcard = flashcards.get();
        if (flashcard.getTranslation().equals(answer.getTranslation())) {
            flashcard.memorizationLevel.upgrade();
            flashcardRepository.save(flashcard);
            return AnswerResult.correct();
        } else {
            flashcard.setMemorizationLevel(MemorizationLevel.none());
            flashcardRepository.save(flashcard);
            return AnswerResult.mistake();
        }
    }
}
