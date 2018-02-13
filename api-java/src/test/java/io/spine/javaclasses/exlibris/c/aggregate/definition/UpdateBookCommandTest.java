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

package io.spine.javaclasses.exlibris.c.aggregate.definition;

import com.google.protobuf.Message;
import io.spine.javaclasses.exlibris.testdata.BookCommandFactory;
import javaclasses.exlibris.Book;
import javaclasses.exlibris.BookDetailsChange;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.BookUpdated;
import javaclasses.exlibris.c.UpdateBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.bookDetails;
import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.bookDetails2;
import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.updateBookInstance;
import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    @DisplayName("produce BookUpdated event")
    void produceEvent() {

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

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(updateBook));

        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(BookUpdated.class, messageList.get(0)
                                                   .getClass());

        final BookUpdated bookUpdated = (BookUpdated) messageList.get(0);

        assertEquals(BookCommandFactory.bookId, bookUpdated.getBookId());

        assertEquals(bookDetails2.getTitle(), bookUpdated.getBookDetailsChange()
                                                         .getNewBookDetails()
                                                         .getTitle());
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

