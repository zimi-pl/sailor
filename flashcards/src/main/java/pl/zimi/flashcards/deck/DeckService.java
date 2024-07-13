package pl.zimi.flashcards.deck;

import lombok.RequiredArgsConstructor;
import pl.zimi.flashcards.flashcard.*;
import pl.zimi.flashcards.translator.Translator;
import pl.zimi.flashcards.user.UserId;

import java.util.*;

@RequiredArgsConstructor
public class DeckService {

    private final ContentDeckCreator contentDeckCreator;
    private final DeckRepository deckRepository;
    private final Translator translator;
    private final FlashcardService flashcardService;

    Deck createDeck(Content content, UserId userId) {
        final var deck = Deck.builder()
                .name(content.getTitle())
                .build();
        final var saved = deckRepository.save(deck);

        final var sentences = contentDeckCreator.splitSentences(content);
        TreeSet<AddFlashcardRequest> requests = new TreeSet<>(Comparator.comparing(this::to));
        for (String sentence : sentences) {
            List<Phrase> phrases = contentDeckCreator.splitPhrases(sentence);
            for (Phrase phrase : phrases) {
                Phrase translated = translator.translate(phrase);
                final var addFlashcardRequest = AddFlashcardRequest.builder()
                        .original(phrase)
                        .translation(translated)
                        .userId(userId)
                        .deckId(saved.getId())
                        .build();
                requests.add(addFlashcardRequest);
            }
        }
        for (AddFlashcardRequest request : requests) {
            flashcardService.add(request);
        }
        return saved;
    }

    String to(AddFlashcardRequest request) {
        return request.getOriginal().getText() + ":" + request.getTranslation().getText();
    }

}
