package pl.zimi.flashcard;

import pl.zimi.context.Context;
import pl.zimi.flashcards.flashcard.FlashcardRepository;
import pl.zimi.flashcards.flashcard.FlashcardService;
import pl.zimi.http.JavalinServer;
import pl.zimi.repository.contract.MemoryPort;

import java.time.Clock;

public class App {

    public static void main(String[] args) {

        Context context = Context.create();
        context.register(FlashcardRepository.class, MemoryPort.port(FlashcardRepository.class));
        context.register(Clock.class, Clock.systemUTC());
        FlashcardService flashcardService = context.getBean(FlashcardService.class);

        JavalinServer.server()
                .setupService(flashcardService)
                .prepare()
                .start(7070);
    }

}
