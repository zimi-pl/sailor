package pl.zimi.flashcards.deck;

import pl.zimi.repository.contract.Contract;
import pl.zimi.repository.query.Repository;

public interface DeckRepository extends Repository<Deck> {

    Contract<Deck> CONTRACT = Contract.repository(Deck.class).id(SDeck.deck.id);

}
