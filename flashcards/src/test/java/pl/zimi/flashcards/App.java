package pl.zimi.flashcards;

import pl.zimi.clock.ClockManipulator;
import pl.zimi.context.Context;
import pl.zimi.flashcards.flashcard.FlashcardRepository;
import pl.zimi.repository.contract.MemoryPort;

import java.time.Clock;

public class App {

    public static Context createApp() {
        final var clockManipulator = ClockManipulator.managable();
        return Context.create()
                .register(ClockManipulator.class, clockManipulator)
                .register(Clock.class, clockManipulator.getClock())
                .register(FlashcardRepository.class, MemoryPort.port(FlashcardRepository.class));
    }

}
