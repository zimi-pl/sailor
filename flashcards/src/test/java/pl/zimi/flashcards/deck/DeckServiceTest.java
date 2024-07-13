package pl.zimi.flashcards.deck;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.zimi.flashcards.App;
import pl.zimi.flashcards.flashcard.Flashcard;
import pl.zimi.flashcards.flashcard.FlashcardRepository;
import pl.zimi.flashcards.flashcard.Phrase;
import pl.zimi.flashcards.flashcard.SFlashcard;
import pl.zimi.flashcards.user.UserId;
import pl.zimi.repository.query.Filters;
import pl.zimi.repository.query.Queries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

class DeckServiceTest {

    @Test
    void shouldCreateDeck() {
        // given
        final var app = App.createApp();
        final var deckService = app.getBean(DeckService.class);

        Content content = new Content("Some article", "Here are few words about animals. Are they important in our lives?");

        // when
        final var deck = deckService.createDeck(content, UserId.of("abc"));

        // then
        Assertions.assertEquals(content.getTitle(), deck.getName());
        Assertions.assertNotNull(deck.getId());
        final var flashcardRepository = app.getBean(FlashcardRepository.class);
        final var flashcards = flashcardRepository.find(Queries.filter(Filters.eq(SFlashcard.flashcard.deckId, deck.getId())));
        Assertions.assertEquals(12, flashcards.size());
        final var words = flashcards.stream().map(Flashcard::getOriginal).map(Phrase::getText).collect(Collectors.toSet());

        Assertions.assertEquals(new HashSet<>(Arrays.asList("Here", "are", "few", "words", "about", "animals", "Are", "they", "important", "in", "our", "lives")), words);
    }

}