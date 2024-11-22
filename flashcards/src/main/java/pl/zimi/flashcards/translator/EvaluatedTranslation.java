package pl.zimi.flashcards.translator;

import lombok.Data;

import java.util.List;

@Data
public class EvaluatedTranslation {
    String original;
    String translation;
    List<EvaluatedPhraseTranslation> phraseTranslations;
}
