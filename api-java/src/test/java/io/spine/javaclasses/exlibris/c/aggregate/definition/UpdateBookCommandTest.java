package io.spine.javaclasses.exlibris.c.aggregate.definition;

import io.spine.javaclasses.exlibris.testdata.BookCommandFactory;
import io.spine.net.EmailAddress;
import io.spine.net.Url;
import io.spine.people.PersonName;
import javaclasses.exlibris.AuthorName;
import javaclasses.exlibris.Book;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookDetailsChange;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookSynopsis;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.Category;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.UpdateBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.bookDetails;
import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.bookDetails2;
import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.updateBookInstance;
import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Paul Ageyev
 */
@DisplayName("UpdateBook command should be interpreted by BookAggregate and")
public class UpdateBookCommandTest extends BookCommandTest<UpdateBook> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("update the book")
    void updateBook() {

        dispatchAddBookCmd();

        final BookDetailsChange bookDetailsChange = BookDetailsChange.newBuilder()
                                                                     .setPreviousBookDetails(
                                                                             bookDetails)
                                                                     .setNewBookDetails(
                                                                             bookDetails2)
                                                                     .build();

        final UpdateBook updateBook = updateBookInstance(BookCommandFactory.bookId,
                                                         BookCommandFactory.userId,
                                                         bookDetailsChange);

        dispatchCommand(aggregate, envelopeOf(updateBook));

        Book state = aggregate.getState();

        assertEquals(BookCommandFactory.bookId, state.getBookId());
        assertEquals(state.getBookDetails()
                          .getTitle(), state.getBookDetails()
                                            .getTitle());

    }

    private void dispatchAddBookCmd() {
        final AddBook addBook = createBookInstance();
        dispatchCommand(aggregate, envelopeOf(addBook));
    }
}

