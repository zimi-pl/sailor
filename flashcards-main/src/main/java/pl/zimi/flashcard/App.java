package pl.zimi.flashcard;

import pl.zimi.context.Context;
import pl.zimi.flashcards.deck.DeckId;
import pl.zimi.flashcards.flashcard.AddFlashcardRequest;
import pl.zimi.flashcards.flashcard.FlashcardRepository;
import pl.zimi.flashcards.flashcard.FlashcardService;
import pl.zimi.flashcards.flashcard.Phrase;
import pl.zimi.flashcards.user.UserId;
import pl.zimi.http.JavalinServer;
import pl.zimi.repository.contract.MemoryPort;

import java.time.Clock;

public class App {

    public static void main(String[] args) {
        Context context = Context.create();
        context.register(FlashcardRepository.class, MemoryPort.port(FlashcardRepository.class));
        context.register(Clock.class, Clock.systemUTC());
        FlashcardService flashcardService = context.getBean(FlashcardService.class);

        for (int i = 0; i < 100; i++) {
            AddFlashcardRequest request = AddFlashcardRequest.builder()
                    .userId(UserId.of("asdfa"))
                    .original(Phrase.builder().text("a" + i).context("Tu jest a" + i).build())
                    .translation(Phrase.builder().text("A" + i).context("TU JEST A" + i).build())
                    .deckId(new DeckId("talia"))
                    .build();
            flashcardService.add(request);
        }

        JavalinServer.server()
                .setupService(flashcardService)
                .prepare()
                .start(7070);
    }

}
