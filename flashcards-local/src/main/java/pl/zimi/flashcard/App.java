package pl.zimi.flashcard;

import pl.zimi.context.Context;
import pl.zimi.flashcards.flashcard.FlashcardRepository;
import pl.zimi.flashcards.flashcard.FlashcardService;
import pl.zimi.http.JavalinServer;
import pl.zimi.repository.DynamoDbPort;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.time.Clock;

public class App {

    public static void main(String[] args) {
        Context context = Context.create();
        DynamoDbClient client = DynamoDbClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
        context.register(FlashcardRepository.class, DynamoDbPort.port(FlashcardRepository.class, client, "flashcards-dev-"));
        context.register(Clock.class, Clock.systemUTC());
        FlashcardService flashcardService = context.getBean(FlashcardService.class);

        JavalinServer.server()
                .setupService(flashcardService)
                .prepare()
                .start(7070);
    }

}
