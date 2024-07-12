package pl.zimi.flashcards.user;

import org.apache.commons.lang3.RandomStringUtils;

public class UserFixture {

    public static UserId someUserId() {
        return UserId.of(RandomStringUtils.randomAlphabetic(10));
    }
}
