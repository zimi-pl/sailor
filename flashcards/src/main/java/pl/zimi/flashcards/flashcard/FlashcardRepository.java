package pl.zimi.flashcards.flashcard;

import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.query.Repository;

public interface FlashcardRepository extends Repository<Flashcard> {

    Contract<Flashcard> CONTRACT = Contract.repository(Flashcard.class).id(SFlashcard.flashcard.id);

}
