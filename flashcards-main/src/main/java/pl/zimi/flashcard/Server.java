package pl.zimi.flashcard;

import pl.zimi.context.Context;
import pl.zimi.flashcards.flashcard.FlashcardRepository;
import pl.zimi.flashcards.flashcard.FlashcardService;
import pl.zimi.http.JavalinPort;
import pl.zimi.repository.contract.MemoryPort;

import java.time.Clock;

public class Server {

    public static void main(String[] args) {

        Context context = Context.create();
        context.register(FlashcardRepository.class, MemoryPort.port(FlashcardRepository.class));
        context.register(Clock.class, Clock.systemUTC());
        FlashcardService flashcardService = context.getBean(FlashcardService.class);

//        Endpoint endpoint = Endpoint.get()
//                .path("/flashcards/{a}/{b}/{c}")
//                .mapping("a", SSomeRequest.someRequest.a)
//                .mapping("b", SSomeRequest.someRequest.b)
//                .mapping("c", SSomeRequest.someRequest.c)
//                .requestClass(SomeRequest.class)
//                .handler(o -> someService.add((SomeRequest) o))
//                .build();

        JavalinPort.server()
//                .setupEndpoint(endpoint)
                .setupService(flashcardService)
                .prepare()
                .start(7070);
    }

}
