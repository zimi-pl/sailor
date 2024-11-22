package pl.zimi.flashcards.translator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Translation {
    String original;
    String translation;
    List<PhraseTranslation> phraseTranslations;
}
