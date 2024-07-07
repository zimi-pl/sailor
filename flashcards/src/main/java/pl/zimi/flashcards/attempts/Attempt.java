package pl.zimi.flashcards.attempts;

import java.time.Instant;

public class Attempt {
    String id;
    Instant previous;
    Instant current;
    Confidence confidence;
    Integer exposureNumber;
}
