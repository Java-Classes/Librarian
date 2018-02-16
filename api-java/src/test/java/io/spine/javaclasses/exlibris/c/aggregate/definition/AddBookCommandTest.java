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
import io.spine.core.Event;
import io.spine.javaclasses.exlibris.testdata.BookCommandFactory;
import io.spine.server.BoundedContext;
import io.spine.server.command.TestEventFactory;
import javaclasses.exlibris.Book;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.aggregate.BookAggregate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static io.spine.javaclasses.exlibris.testdata.BookCommandFactory.userId;
import static io.spine.protobuf.AnyPacker.pack;
import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static io.spine.server.command.TestEventFactory.newInstance;
import static javaclasses.exlibris.context.BoundedContexts.create;
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

        final BoundedContext sourceContext = create();

        final Event event = bookAdded();
        sourceContext.getEventBus()
                     .post(event);

    }

    public static Event bookAdded() {

        final TestEventFactory eventFactory = newInstance(pack(BookCommandFactory.bookId),
                                                          BookAggregate.class);
        return eventFactory.createEvent(BookAdded.newBuilder()
                                                 .setBookId(BookCommandFactory.bookId)
                                                 .build()
        );
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
