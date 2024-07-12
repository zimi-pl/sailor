package pl.zimi.flashcards.flashcard;

import org.apache.commons.lang3.RandomStringUtils;

public class WordFixture {

    public static Word someWord() {
        return someWordBuilder()
                .build();
    }

    public static Word.WordBuilder someWordBuilder() {
        return Word.builder()
                .text(RandomStringUtils.randomAlphabetic(10))
                .context(RandomStringUtils.randomAlphabetic(10));
    }

}
