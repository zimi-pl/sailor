package pl.zimi.flashcards.flashcard;

import org.apache.commons.lang3.RandomStringUtils;

public class FlashcardScenarios {

    FlashcardService flashcardService;

    public FlashcardScenarios(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    Flashcard addFlashcard() {
        var flashcard = AddFlashcardRequestFixture.someAddFlashcardRequest();
        return flashcardService.add(flashcard);
    }

    Flashcard addFlashcardForSameUser(Flashcard template) {
        var flashcard = AddFlashcardRequestFixture.someAddFlashcardRequestBuilder()
                .userId(template.getUserId())
                .build();
        return flashcardService.add(flashcard);
    }

    AnswerResult answerCorrectly(Flashcard flashcard) {
        final var answer = Answer.builder()
                .flashcardId(flashcard.getId())
                .translation(flashcard.getTranslation().getText())
                .build();
        return flashcardService.answer(answer);
    }


    AnswerResult answerBadly(Flashcard flashcard) {
        final var answer = Answer.builder()
                .flashcardId(flashcard.getId())
                .translation(RandomStringUtils.randomAlphabetic(10))
                .build();
        return flashcardService.answer(answer);
    }
}
