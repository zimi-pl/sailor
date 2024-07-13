package pl.zimi.flashcards.deck;

import pl.zimi.flashcards.flashcard.Phrase;

import java.util.*;
import java.util.stream.Collectors;

public class ContentDeckCreator {

    public List<String> splitSentences(Content content) {
        StringBuilder wholeContent = new StringBuilder(content.getText());

        List<String> sentences = new ArrayList<>();
        String[] dotExclusions = {"Dr", "dr", "Mr", "mr"};
        while (wholeContent.length() > 0) {
            int dotIndex = wholeContent.indexOf(".");
            dotIndex = dotIndex != -1 ? dotIndex : wholeContent.length();
            for (String dotExclusion : dotExclusions) {
                if (wholeContent.substring(dotIndex - dotExclusion.length(), dotIndex).equals(dotExclusion)) {
                    dotIndex = wholeContent.indexOf(dotExclusion, dotIndex + 1);
                    dotIndex = dotIndex != -1 ? dotIndex : wholeContent.length();
                    break;
                }
            }
            int questionMarkIndex = wholeContent.indexOf("?");
            questionMarkIndex = questionMarkIndex != -1 ? questionMarkIndex : wholeContent.length();

            int exclamationMarkIndex = wholeContent.indexOf("!");
            exclamationMarkIndex = exclamationMarkIndex != -1 ? exclamationMarkIndex : wholeContent.length();
            final var minIndex = Arrays.asList(dotIndex, questionMarkIndex, exclamationMarkIndex)
                    .stream()
                    .min(Comparator.naturalOrder())
                    .orElse(null);
            String sentence;
            if ("...".equals(substring(wholeContent, minIndex, minIndex + 2 + 1))) {
                sentence = substring(wholeContent, 0, minIndex + 2 + 1).strip();
                wholeContent = new StringBuilder(wholeContent.substring(minIndex + 2 + 1));
            } else {
                sentence = substring(wholeContent, 0, minIndex + 1).strip();
                wholeContent = new StringBuilder(wholeContent.substring(minIndex + 1));
            }
            sentences.add(sentence);
        }

        return sentences;
    }

    String substring(StringBuilder str, int start, int end) {
        if (str.length() < end || str.length() < start) {
            return null;
        }
        return str.substring(start, end);
    }

    public List<Phrase> splitPhrases(String sentence) {
        final var phrases = sentence.split(" ");
        return Arrays.stream(phrases)
                .map(phrase -> phrase.replace(".", "").replace("?", "").replace("!", ""))
                .map(phrase -> new Phrase(phrase, sentence))
                .collect(Collectors.toList());
    }
}
