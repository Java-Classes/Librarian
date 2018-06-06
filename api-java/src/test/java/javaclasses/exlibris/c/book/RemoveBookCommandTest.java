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

package javaclasses.exlibris.c.book;

import com.google.common.base.Throwables;
import com.google.protobuf.Message;
import io.spine.server.entity.LifecycleFlags;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.BookRemoved;
import javaclasses.exlibris.c.RemoveBook;
import javaclasses.exlibris.c.rejection.CannotRemoveMissingBook;
import javaclasses.exlibris.testdata.BookCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.c.RemoveBook.BookRemovalReasonCase.BOOKREMOVALREASON_NOT_SET;
import static javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static javaclasses.exlibris.testdata.BookCommandFactory.removalCustomReason;
import static javaclasses.exlibris.testdata.BookCommandFactory.removalOutdatedReason;
import static javaclasses.exlibris.testdata.BookCommandFactory.removeBookInstance;
import static javaclasses.exlibris.testdata.TestValues.BOOK_ID;
import static javaclasses.exlibris.testdata.TestValues.LIBRARIAN_ID;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Dmytry Dyachenko
 * @author Paul Ageyev
 */
@DisplayName("RemoveBook command should be interpreted by BookAggregate and")
public class RemoveBookCommandTest extends BookCommandTest<RemoveBook> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("remove a book with an outdated reason")
    void removeBookWithOutdatedReason() {
        dispatchAddBookCmd();

        final RemoveBook removeBook = removeBookInstance(BOOK_ID, LIBRARIAN_ID,
                                                         BookCommandFactory.removalOutdatedReason);

        dispatchCommand(aggregate, envelopeOf(removeBook));
        final LifecycleFlags lifecycleFlags = aggregate.getLifecycleFlags();
        assertTrue(lifecycleFlags.getDeleted());
    }

    @Test
    @DisplayName("remove a book with a custom reason")
    void removeBookWithCustomReason() {
        dispatchAddBookCmd();

        final RemoveBook removeBook = removeBookInstance(BOOK_ID, LIBRARIAN_ID,
                                                         removalCustomReason);

        dispatchCommand(aggregate, envelopeOf(removeBook));
        final LifecycleFlags lifecycleFlags = aggregate.getLifecycleFlags();
        assertTrue(lifecycleFlags.getDeleted());
    }

    @Test
    @DisplayName("remove a book without a reason")
    public void removeBookWithoutReason() {
        dispatchAddBookCmd();
        final Throwable exception = assertThrows(IllegalArgumentException.class,
                                                 () -> {
                                                     removeBookInstance(BOOK_ID,
                                                                        LIBRARIAN_ID,
                                                                        BOOKREMOVALREASON_NOT_SET);
                                                 });
    }

    @Test
    @DisplayName("produce BookRemoved event")
    void produceEvent() {
        dispatchAddBookCmd();
        final RemoveBook removeBook = removeBookInstance(BOOK_ID, LIBRARIAN_ID,
                                                         removalOutdatedReason);

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(removeBook));
        assertEquals(1, messageList.size());
        assertEquals(BookRemoved.class, messageList.get(0)
                                                   .getClass());
        final BookRemoved bookRemoved = (BookRemoved) messageList.get(0);

        assertEquals(BOOK_ID, bookRemoved.getBookId());
    }

    @Test
    @DisplayName("has the same removal reason")
    void sameRemovalReason() {
        dispatchAddBookCmd();

        final RemoveBook removeBook = removeBookInstance(BOOK_ID, LIBRARIAN_ID,
                                                         removalOutdatedReason);

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(removeBook));
        final BookRemoved bookRemoved = (BookRemoved) messageList.get(0);

        assertEquals(removeBook.getBookRemovalReasonCase()
                               .getNumber(),
                     bookRemoved.getBookRemovalReasonCase()
                                .getNumber());

    }

    @Test
    @DisplayName("throw CannotRemoveMissingBook rejection upon " +
            "an attempt to remove a missing book")
    void notRemoveBook() {
        final RemoveBook removeBook = removeBookInstance(bookId, USER_ID,
                                                         RemoveBook.BookRemovalReasonCase.OUTDATED);

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(removeBook)));

        final Throwable cause = Throwables.getRootCause(t);

        final CannotRemoveMissingBook rejection = (CannotRemoveMissingBook) cause;
        assertEquals(rejection.getMessageThrown()
                              .getBookId(), bookId);
    }

    private void dispatchAddBookCmd() {
        final AddBook addBook = createBookInstance();
        dispatchCommand(aggregate, envelopeOf(addBook));
    }
}
