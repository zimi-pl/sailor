package pl.zimi.flashcard;

import pl.zimi.context.Context;
import pl.zimi.flashcards.flashcard.Flashcard;
import pl.zimi.flashcards.flashcard.FlashcardRepository;
import pl.zimi.flashcards.flashcard.SFlashcard;
import pl.zimi.repository.DynamoDbPort;
import pl.zimi.repository.query.Filter;
import pl.zimi.repository.query.Filters;
import pl.zimi.repository.query.Queries;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.time.Clock;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Context context = Context.create();
        DynamoDbClient client = DynamoDbClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
        context.register(FlashcardRepository.class, DynamoDbPort.port(FlashcardRepository.class, client, "flashcards-dev-"));
        context.register(Clock.class, Clock.systemUTC());
        FlashcardRepository flashcardRepository = context.getBean(FlashcardRepository.class);

        Filter useAfterIsNull = Filters.isNull(SFlashcard.flashcard.memorizationLevel.useAfter);
        Filter useAfterLtNow = Filters.lt(SFlashcard.flashcard.memorizationLevel.useAfter, Clock.systemUTC().instant());
        Filter userFilter = Filters.eq(SFlashcard.flashcard.userId.value, "asdfa");
        List<Flashcard> flashcards = flashcardRepository.find(Queries.query(Filters.and(userFilter, Filters.or(useAfterIsNull, useAfterLtNow)), null, null));
//        List<Flashcard> flashcards = flashcardRepository.find(Queries.filter(useAfterIsNull));

        for (Flashcard flashcard : flashcards) {
            System.out.println("Flashcard " + flashcard);
        }
        System.out.println(flashcards.size());
    }

}
