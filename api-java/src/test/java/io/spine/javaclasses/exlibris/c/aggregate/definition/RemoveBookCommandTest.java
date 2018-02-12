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
import javaclasses.exlibris.Book;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.BookRemoved;
import javaclasses.exlibris.c.RemoveBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.librarianId;
import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.removalReason;
import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.removeBookInstance;
import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Dmytry Dyachenko
 */
@DisplayName("RemoveBook command should be interpreted by BookAggregate and")
public class RemoveBookCommandTest extends BookCommandTest<AddBook> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("remove the book")
    void removeBook() {
        dispatchAddBookCmd();

        final RemoveBook removeBook = removeBookInstance(bookId, librarianId, removalReason);

        dispatchCommand(aggregate, envelopeOf(removeBook));
        final Book state = aggregate.getState();

        assertEquals("", state.getBookId()
                              .toString());

        assertEquals("", state.getBookDetails()
                              .toString());
    }

    @Test
    @DisplayName("produce BookRemoved event")
    void produceEvent() {
        dispatchAddBookCmd();

        final RemoveBook removeBook = removeBookInstance(bookId, librarianId, removalReason);

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(removeBook));
        assertEquals(1, messageList.size());
        assertEquals(BookRemoved.class, messageList.get(0)
                                                   .getClass());
        final BookRemoved bookRemoved = (BookRemoved) messageList.get(0);

        assertEquals(bookId, bookRemoved.getBookId());
    }

    @Test
    @DisplayName("has the same removal reason")
    void sameRemovalReason() {
        dispatchAddBookCmd();

        final RemoveBook removeBook = removeBookInstance(bookId, librarianId, removalReason);

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(removeBook));
        final BookRemoved bookRemoved = (BookRemoved) messageList.get(0);

        assertEquals(removeBook.getBookRemovalReasonCase()
                               .getNumber(),
                     bookRemoved.getBookRemovalReasonCase()
                                .getNumber());

    }

    private void dispatchAddBookCmd() {
        final AddBook addBook = createBookInstance();
        dispatchCommand(aggregate, envelopeOf(addBook));
    }
}
