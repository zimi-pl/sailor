package pl.zimi.flashcards.flashcard;

import org.apache.commons.lang3.RandomStringUtils;

public class AddFlashcardRequestFixture {

    public static AddFlashcardRequest someAddFlashcardRequest() {
        return someAddFlashcardRequestBuilder()
                .build();
    }

    public static AddFlashcardRequest.AddFlashcardRequestBuilder someAddFlashcardRequestBuilder() {
        return AddFlashcardRequest.builder()
                .original(WordFixture.someWord())
                .translation(WordFixture.someWord())
                .userId(UserId.of(RandomStringUtils.randomAlphabetic(10)));
    }
}
