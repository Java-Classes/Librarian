package io.spine.javaclasses.exlibris.c.aggregate.definition;

import com.google.protobuf.Message;
import io.spine.net.EmailAddress;
import io.spine.net.Url;
import io.spine.people.PersonName;
import javaclasses.exlibris.AuthorName;
import javaclasses.exlibris.Book;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookSynopsis;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.Category;
import javaclasses.exlibris.Isbn62;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AddBook;
import javafx.concurrent.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddBookCommandTest extends BookCommandTest<AddBook> {


    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("create the book")
    void createTask() {

        BookId bookId = BookId.newBuilder()
                              .setIsbn62(Isbn62.newBuilder()
                                               .setValue("12345"))
                              .build();
        UserId userId = UserId.newBuilder()
                              .setEmail(EmailAddress.newBuilder()
                                                    .setValue("paulageyev@gmail.com"))
                              .build();
        BookDetails bookDetails = BookDetails.newBuilder()
                                             .setTitle(BookTitle.newBuilder()
                                                                .setTitle("Steve Jobs"))
                                                                .setAuthor(AuthorName.newBuilder().addAuthorName(PersonName.newBuilder().setFamilyName("Paul")))
                                                                .setBookCoverUrl(Url.newBuilder().setRaw("url"))
                                                                .setSynopsis(BookSynopsis.newBuilder().setBookSynopsis("bio"))
                                                                .addCategories(Category.newBuilder().setValue("it"))
                                             .build();

        final AddBook addBook = createBookInstance(bookId, userId, bookDetails);

        dispatchCommand(aggregate, envelopeOf(addBook));

        final Book state = aggregate.getState();
        assertEquals(state.getBookId(), addBook.getBookId());

    }
}
