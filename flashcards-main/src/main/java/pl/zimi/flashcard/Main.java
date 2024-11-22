//package pl.zimi.flashcard;
//
//import com.google.gson.Gson;
//import dev.langchain4j.data.message.AiMessage;
//import dev.langchain4j.data.message.SystemMessage;
//import dev.langchain4j.data.message.UserMessage;
//import dev.langchain4j.model.chat.ChatLanguageModel;
//import dev.langchain4j.model.openai.OpenAiChatModel;
//import dev.langchain4j.model.output.Response;
//import pl.zimi.flashcard.llms.LlmEvaluator;
//import pl.zimi.flashcard.llms.LlmTranslator;
//import pl.zimi.flashcards.translator.EvaluatedTranslation;
//import pl.zimi.flashcards.translator.Translation;
//import pl.zimi.llm.LargeLanguageModel;
//
//import java.util.Arrays;
//
//public class Main {
//
//    private final static String SOME_CONTENT = "Initially only available in English, editions of Wikipedia in more than 300 other languages " +
//            "have been developed. The English Wikipedia, with its over 6.9 million articles, is the largest of the " +
//            "editions, which together comprise more than 63 million articles and attract more than 1.5 billion unique " +
//            "device visits and 13 million edits per month (about 5 edits per second on average) as of April 2024. " +
//            "As of November 2024, over 25% of Wikipedia's traffic was from the United States, followed by Japan at" +
//            " 6.2%, the United Kingdom at 5.6%, Russia at 5.0%, Germany at 4.8%, and the remaining 53.3% split among " +
//            "other countries.";
//
//    public static void main(String[] args) {
////        Context context = Context.create();
//////        DynamoDbClient client = DynamoDbClient.builder()
//////                .region(Region.EU_CENTRAL_1)
//////                .build();
//////        context.register(FlashcardRepository.class, DynamoDbPort.port(FlashcardRepository.class, client, "flashcards-dev-"));
////        context.register(DeckRepository.class, MemoryPort.port(DeckRepository.class));
////        context.register(FlashcardRepository.class, MemoryPort.port(FlashcardRepository.class));
////        context.register(Translator.class, new Translator() {
////            @Override
////            public Phrase translate(Phrase phrase) {
////                return new Phrase(phrase.getText().toUpperCase(), phrase.getContext().toUpperCase());
////            }
////        });
////        context.register(Clock.class, Clock.systemUTC());
////        DeckService deckService = context.getBean(DeckService.class);
////        FlashcardService flashcardService = context.getBean(FlashcardService.class);
////
////        Content content = new Content("Wikipedia", SOME_CONTENT);
////
////        Deck zimi = deckService.createDeck(content, UserId.of("zimi"));
////
////        List<Flashcard> flashcards = flashcardService.listDeck(zimi.getId());
////        for (Flashcard flashcard : flashcards) {
////            System.out.println(flashcard);
////        }
////        System.out.println("Flashcard " + zimi);
//
//        LargeLanguageModel largeLanguageModel = new LargeLanguageModel() {
//
//            ChatLanguageModel model = OpenAiChatModel.builder()
//                    .baseUrl("https://api.groq.com/openai/v1")
//                    .apiKey("XXXXXXX")
////                    .modelName("llama-3.2-90b-text-preview")
//                    .modelName("gemma2-9b-it")
//                    .build();
//            @Override
//            public String chat(String systemMessage, String userMessage) {
//                Response<AiMessage> response = model.generate(Arrays.asList(SystemMessage.from(systemMessage), UserMessage.userMessage(userMessage)));
//                String text = response.content().text();
//                System.out.println(text);
//                return text;
//            }
//        };
//
//        LlmTranslator llmTranslator = new LlmTranslator(largeLanguageModel);
//        String sentence = "This is an entrance.";
//        Translation translate = llmTranslator.translate(sentence);
//
//        System.out.println(translate);
//
//        System.out.println("");
//        LlmEvaluator evaluator = new LlmEvaluator(largeLanguageModel);
//        EvaluatedTranslation evaluate = evaluator.evaluate(translate);
//        System.out.println(evaluate);
//
//
////        System.out.println("łagodzić ich zarządzenia oraz uratować jak największą liczbę istnień ludzkich. Był jednocześnie silnie krytykowany przez żydowskie podziemie oraz darzony niechęcią przez większość mieszkańców getta warszawskiego. Popełnił samobójstwo w drugim dniu wielkiej akcji deportacyjnej w getcie, nie chcąc współpracować z Niemcami przy wywózkach Żydów do obozu zagłady w Treblince. Od września 1939 roku do dnia swej samobójczej śmierci prowadził dziennik, uważany za jeden z najważniejszych dokumentów na temat historii");
//    }
//
//}
