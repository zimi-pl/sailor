package pl.zimi.flashcards.flashcard;

public class FlashcardScenarios {

    FlashcardService flashcardService;

    public FlashcardScenarios(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    Flashcard addFlashcard() {
        var flashcard = FlashcardFixture.someFlashcardBuilder()
                .memorizationLevel(MemorizationLevel.level(0))
                .build();

        return flashcardService.add(flashcard);
    }

    AnswerResult answerCorrectly(Flashcard flashcard) {
        final var answer = Answer.builder()
                .flashcardId(flashcard.getId())
                .translation(flashcard.getTranslation())
                .build();
        return flashcardService.answer(answer);
    }
}
