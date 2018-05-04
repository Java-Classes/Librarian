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

package javaclasses.exlibris.c.integrational;

import io.spine.core.Command;
import io.spine.grpc.StreamObservers;
import io.spine.server.BoundedContext;
import io.spine.server.commandbus.CommandBus;
import io.spine.server.event.EventBus;
import io.spine.server.rejection.RejectionBus;
import javaclasses.exlibris.BoundedContexts;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.book.BookCommandTest;
import javaclasses.exlibris.c.rejection.Rejections;
import javaclasses.exlibris.testdata.BookCommandFactory;
import javaclasses.exlibris.testdata.BookEventSubscriber;
import javaclasses.exlibris.testdata.BookRejectionsSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.protobuf.TypeConverter.toMessage;
import static javaclasses.exlibris.testdata.TestValues.BOOK_ID;
import static javaclasses.exlibris.testdata.TestValues.BOOK_TITLE;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SubscribersTest extends BookCommandTest<AddBook> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("subscriber catches BookAlreadyExists rejection")
    void throwMenuNotAvailable() {
        final BoundedContext boundedContext = BoundedContexts.create();
        final CommandBus commandBus = boundedContext.getCommandBus();
        final RejectionBus rejectionBus = boundedContext.getRejectionBus();

        final Command addBook = requestFactory.createCommand(
                toMessage(BookCommandFactory.createBookInstance()));

        final BookRejectionsSubscriber bookRejectionsSubscriber = new BookRejectionsSubscriber();

        rejectionBus.register(bookRejectionsSubscriber);

        assertNull(bookRejectionsSubscriber.getRejection());

        commandBus.post(addBook, StreamObservers.noOpObserver());
        commandBus.post(addBook, StreamObservers.noOpObserver());

        Rejections.BookAlreadyExists bookAlreadyExists = bookRejectionsSubscriber.getRejection();

        assertEquals(USER_ID, bookAlreadyExists.getLibrarianId());
        assertEquals(BOOK_ID, bookAlreadyExists.getBookId());
        assertEquals(BOOK_TITLE, bookAlreadyExists.getBookTitle());
    }

    @Test
    @DisplayName("subscriber catches events")
    void catchEvent() {
        final BoundedContext boundedContext = BoundedContexts.create();
        final CommandBus commandBus = boundedContext.getCommandBus();
        final EventBus eventBus = boundedContext.getEventBus();
        final Command addBook =
                requestFactory.command()
                              .create(toMessage(BookCommandFactory.createBookInstance()));
        final BookEventSubscriber eventSubscriber = new BookEventSubscriber();
        eventBus.register(eventSubscriber);

        commandBus.post(addBook, StreamObservers.noOpObserver());
        final BookAdded event = eventSubscriber.getEvent();
        assertEquals(BOOK_ID, event.getBookId());
        assertEquals(BOOK_TITLE, event.getDetails()
                                      .getTitle());
        assertEquals(USER_ID, event.getLibrarianId());
    }
}
