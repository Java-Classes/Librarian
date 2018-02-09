package io.spine.javaclasses.exlibris.c.aggregate.definition;

import javaclasses.exlibris.c.AddBook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddBookCommandTest extends BookCommandTest<AddBook>{

    @Test
    @DisplayName("create the book")
    void createTask() {
        final AddBook addBook = createBookInstance();

        dispatchCommand(aggregate, envelopeOf(addBook));
    }
}
