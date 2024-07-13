package pl.zimi.flashcards.flashcard;

import org.apache.commons.lang3.RandomStringUtils;

public class WordFixture {

    public static Phrase someWord() {
        return someWordBuilder()
                .build();
    }

    public static Phrase.PhraseBuilder someWordBuilder() {
        return Phrase.builder()
                .text(RandomStringUtils.randomAlphabetic(10))
                .context(RandomStringUtils.randomAlphabetic(10));
    }

}
