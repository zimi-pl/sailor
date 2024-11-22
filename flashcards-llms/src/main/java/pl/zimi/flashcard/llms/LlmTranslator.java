package pl.zimi.flashcard.llms;

import com.google.gson.Gson;
import pl.zimi.flashcards.translator.Translation;
import pl.zimi.flashcards.translator.Translator;
import pl.zimi.llm.LargeLanguageModel;

public class LlmTranslator implements Translator {

    private final String SYSTEM_MESSAGE = "Podziel na słowa i przetłumacz każde z nich a następnie zwróć jako JSON. Nie zwracaj nic poza JSON. Użyj formatu:" +
                "{\n" +
                "  \"translation\": \"{{TRANSLATED_SENTENCE}}\",\n" +
                "  \"phraseTranslations\": [\n" +
                "    {\"original\": \"{{ORIGINAL_PHRASE}}\", \"translated\": \"{{TRANSLATED_PHRASE}}\"},\n" +
                "    ...\n" +
                "  ]\n" +
                "}";

    private LargeLanguageModel largeLanguageModel;

    public LlmTranslator(LargeLanguageModel largeLanguageModel) {
        this.largeLanguageModel = largeLanguageModel;
    }

    public Translation translate(String sentence) {
        String json = largeLanguageModel.chat(SYSTEM_MESSAGE, sentence);
        Translation translation = new Gson().fromJson(json, Translation.class);
        return translation;
    }
}
