package io.spine.javaclasses.exlibris.c.aggregate.definition;

import com.google.protobuf.Message;
import io.spine.net.EmailAddress;
import javaclasses.exlibris.Book;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.Isbn62;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AddBook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddBookCommandTest extends BookCommandTest<AddBook> {

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
                                             
                                             .build();

        final AddBook addBook = createBookInstance(bookId, userId, bookDetails);

        System.out.println(addBook);

        List<? extends Message> messages = dispatchCommand(aggregate, envelopeOf(addBook));

    }
}
