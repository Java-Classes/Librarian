/*
 * Copyright 2018, TeamDev Ltd. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package javaclasses.exlibris.c.aggregate;

import com.google.common.base.Throwables;
import com.google.protobuf.Message;
import javaclasses.exlibris.Book;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.rejection.BookAlreadyExists;
import javaclasses.exlibris.testdata.BookCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static javaclasses.exlibris.testdata.BookCommandFactory.userId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals(userId, bookAdded.getLibrarianId());
    }

    @Test
    @DisplayName("add a book")
    void addBook() {

        final AddBook addBook = createBookInstance();
        dispatchCommand(aggregate, envelopeOf(addBook));

        final Book state = aggregate.getState();
        assertEquals(state.getBookId(), addBook.getBookId());
    }

    @Test
    @DisplayName("throw BookAlreadyExists rejection upon " +
            "an attempt to add a book with the same title")
    void notAddBook() {

        final AddBook addBook = createBookInstance();
        dispatchCommand(aggregate, envelopeOf(addBook));

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(addBook)));
        final Throwable cause = Throwables.getRootCause(t);

        assertThat(cause, instanceOf(BookAlreadyExists.class));

        final BookAlreadyExists rejection = (BookAlreadyExists) cause;
        final BookId actualBookId = rejection.getMessageThrown()
                                             .getBookId();
        assertEquals(addBook.getBookId(), actualBookId);

        final BookTitle actualBookTitle = rejection.getMessageThrown()
                                                   .getBookTitle();
        assertEquals(addBook.getBookDetails()
                            .getTitle(), actualBookTitle);
    }

}
