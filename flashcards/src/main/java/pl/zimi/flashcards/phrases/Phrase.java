package pl.zimi.flashcards.phrases;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Phrase {

    String id;
    String word;
    String context;
    List<String> synonyms;
    String language;

}
