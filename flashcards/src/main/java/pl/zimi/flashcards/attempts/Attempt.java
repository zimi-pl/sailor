package pl.zimi.flashcards.attempts;

import pl.zimi.flashcards.flashcard.Confidence;

import java.time.Instant;

public class Attempt {
    String id;
    Instant previous;
    Instant current;
    Confidence confidence;
    Integer exposureNumber;
}
