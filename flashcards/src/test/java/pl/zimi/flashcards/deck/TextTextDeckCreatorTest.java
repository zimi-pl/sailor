package pl.zimi.flashcards.deck;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class TextTextDeckCreatorTest {

    @Test
    public void shouldListSentences() {
        // given
        Content content = new Content("some article", "You're only here temporarily? That's the plan for now, but I could be enticed to stay. " +
                "I do believe there was subtext there. Did you pick up on it? And it was last try that day... So he was really tired.");
        final var creator = new ContentDeckCreator();

        // when
        final var sentences = creator.splitSentences(content);

        // then
        Assertions.assertEquals(6, sentences.size());
        Assertions.assertEquals(sentences, Arrays.asList(
                "You're only here temporarily?",
                "That's the plan for now, but I could be enticed to stay.",
                "I do believe there was subtext there.",
                "Did you pick up on it?",
                "And it was last try that day...",
                "So he was really tired."
        ));


    }

}