package pl.zimi.flashcards.flashcard;

import org.apache.commons.lang3.RandomStringUtils;

public class FlashcardFixture {

    public static Flashcard someFlashcard() {
        return someFlashcardBuilder()
                .build();
    }

    public static Flashcard.FlashcardBuilder someFlashcardBuilder() {
        return Flashcard.builder()
                .word(RandomStringUtils.randomAlphabetic(10))
                .translation(RandomStringUtils.randomAlphabetic(10))
                .memorizationLevel(MemorizationLevel.none())
                .userId(UserId.of(RandomStringUtils.randomAlphabetic(10)));
    }
}
