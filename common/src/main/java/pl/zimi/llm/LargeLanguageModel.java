package pl.zimi.llm;

public interface LargeLanguageModel {

    String chat(String systemMessage, String userMessage);

}
