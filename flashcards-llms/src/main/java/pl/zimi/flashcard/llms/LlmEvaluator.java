package pl.zimi.flashcard.llms;

import com.google.gson.Gson;
import pl.zimi.flashcards.translator.EvaluatedTranslation;
import pl.zimi.flashcards.translator.Evaluator;
import pl.zimi.flashcards.translator.Translation;
import pl.zimi.llm.LargeLanguageModel;

public class LlmEvaluator implements Evaluator {

    private final String SYSTEM_MESSAGE = "Oceń poprawność każdego z poniższych tłumaczeń w skali od 0 do 100. Rezultat zwróć w postaci JSON. Nie zwracaj nic oprócz JSON. Do każdego phrase dodaj pole: points w którym podaj swoją ocenę. Jeśli słowo jest nazwą własną jako ocenę zawsze wpisz 0.";

    private LargeLanguageModel largeLanguageModel;

    public LlmEvaluator(LargeLanguageModel largeLanguageModel) {
        this.largeLanguageModel = largeLanguageModel;
    }

    @Override
    public EvaluatedTranslation evaluate(Translation translation) {
        Gson gson = new Gson();
        String json = gson.toJson(translation);
        String chat = largeLanguageModel.chat(SYSTEM_MESSAGE, json);
        EvaluatedTranslation evaluatedTranslation = gson.fromJson(chat, EvaluatedTranslation.class);
        return evaluatedTranslation;
    }
}
