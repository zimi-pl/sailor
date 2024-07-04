package pl.zimi.flashcards.flashcard;

import pl.zimi.repository.contract.Contract;

public class FlashcardContract {

    public final static Contract<Flashcard> CONTRACT = Contract.repository(Flashcard.class).id(SFlashcard.flashcard.id);
}
