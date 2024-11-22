package pl.zimi.flashcards.translator;

import lombok.Data;

@Data
public class EvaluatedPhraseTranslation {
    String original;
    String translated;
    int points;
}
