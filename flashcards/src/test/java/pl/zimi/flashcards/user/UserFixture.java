package pl.zimi.flashcards.user;

import org.apache.commons.lang3.RandomStringUtils;
import pl.zimi.flashcards.flashcard.UserId;

public class UserFixture {

    public static UserId someUserId() {
        return UserId.of(RandomStringUtils.randomAlphabetic(10));
    }
}
