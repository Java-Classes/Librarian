package io.spine.javaclasses.exlibris.c.aggregate.definition;

import com.google.protobuf.Message;
import io.spine.javaclasses.exlibris.testdata.BookCommandFactory;
import javaclasses.exlibris.Book;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.BookAdded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.userId;
import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Paul Ageyev
 */
@DisplayName("AddBook command should be interpreted by BookAggregate and")
public class AddBookCommandTest extends BookCommandTest<AddBook> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce BookAdded event")
    void produceEvent() {
        final AddBook addBook = createBookInstance();
        final List<? extends Message> messageList = dispatchCommand(aggregate, envelopeOf(addBook));

        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(BookAdded.class, messageList.get(0)
                                                 .getClass());

        final BookAdded bookAdded = (BookAdded) messageList.get(0);

        assertEquals(BookCommandFactory.bookId, bookAdded.getBookId());

        assertEquals(userId.getEmail()
                           .getValue(), bookAdded.getLibrarianId()
                                                 .getEmail()
                                                 .getValue());
    }

    @Test
    @DisplayName("add the book")
    void addBook() {
        final AddBook addBook = createBookInstance();
        dispatchCommand(aggregate, envelopeOf(addBook));

        final Book state = aggregate.getState();
        assertEquals(state.getBookId(), addBook.getBookId());
    }
}
