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

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import javaclasses.exlibris.Book;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookVBuilder;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.BookRemoved;
import javaclasses.exlibris.c.BookUpdated;
import javaclasses.exlibris.c.RemoveBook;
import javaclasses.exlibris.c.UpdateBook;

import java.util.List;

import static io.spine.time.Time.getCurrentTime;
import static java.util.Collections.singletonList;

/**
 * The aggregate managing the state of a {@link Book}.
 *
 * @author Alexander Karpets
 * @author Paul Ageyev
 */

@SuppressWarnings({"ClassWithTooManyMethods", /* Task definition cannot be separated and should
                                                 process all commands and events related to it
                                                 according to the domain model.
                                                 The {@code Aggregate} does it with methods
                                                 annotated as {@code Assign} and {@code Apply}.
                                                 In that case class has too many methods.*/
        "OverlyCoupledClass"}) /* As each method needs dependencies  necessary to perform execution
                                                 that class also overly coupled.*/

public class BookAggregate extends Aggregate<BookId, Book, BookVBuilder> {
    /**
     * Creates a new instance.
     *
     * <p>Constructors of derived classes should have package access level
     * because of the following reasons:
     * <ol>
     * <li>These constructors are not public API of an application.
     * Commands and aggregate IDs are.
     * <li>These constructors need to be accessible from tests in the same package.
     * </ol>
     *
     * <p>Because of the last reason consider annotating constructors with
     * {@code @VisibleForTesting}. The package access is needed only for tests.
     * Otherwise aggregate constructors (that are invoked by {@link javaclasses.exlibris.repository.BookRepository}
     * via Reflection) may be left {@code private}.
     *
     * @param id the ID for the new aggregate
     */
    public BookAggregate(BookId id) {
        super(id);
    }

    @Assign
    List<? extends Message> handle(AddBook cmd) {

        final BookId bookId = cmd.getBookId();
        final UserId userId = cmd.getLibrarianId();
        final BookDetails bookDetails = cmd.getBookDetails();

        final long currentTimeMillis = System.currentTimeMillis();
        final Timestamp whenAdded = Timestamp.newBuilder()
                                             .setSeconds(currentTimeMillis / 1000)
                                             .setNanos(
                                                     (int) ((currentTimeMillis % 1000) * 1000000))
                                             .build();

        final BookAdded result = BookAdded.newBuilder()
                                          .setBookId(bookId)
                                          .setLibrarianId(userId)
                                          .setDetails(bookDetails)
                                          .setWhenAdded(whenAdded)
                                          .build();

        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(UpdateBook cmd) {

        final BookId bookId = cmd.getBookId();
        final UserId userId = cmd.getLibrarianId();

        final BookDetails bookDetails = cmd.getBookDetails();

        final long currentTimeMillis = System.currentTimeMillis();
        final Timestamp whenAdded = Timestamp.newBuilder()
                                             .setSeconds(currentTimeMillis / 1000)
                                             .setNanos((int) ((currentTimeMillis % 1000) * 1000000))
                                             .build();

        final BookUpdated result = BookUpdated.newBuilder()
                                              .setBookId(bookId)
                                              .setLibrarianId(userId)
                                              .setNewBookDetails(bookDetails)
                                              .setWhenUpdated(whenAdded)
                                              .build();

        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(RemoveBook cmd) {

        final BookId bookId = cmd.getBookId();
        final UserId userId = cmd.getLibrarianId();

        final RemoveBook.BookRemovalReasonCase reasonCase = cmd.getBookRemovalReasonCase();

        final String customReason = cmd.getCustomReason();

        final BookRemoved bookRemoved = BookRemoved.newBuilder()
                                                   .setBookId(bookId)
                                                   .setLibrarianId(userId)
                                                   .setWhenRemoved(getCurrentTime())
                                                   .build();

        switch (cmd.getBookRemovalReasonCase()) {
            case OUTDATED: {
                bookRemoved.toBuilder()
                           .setOutdated(true)
                           .build();
                break;
            }
            case CUSTOM_REASON: {
                bookRemoved.toBuilder()
                           .setCustomReason(customReason)
                           .build();
                break;
            }
/*            case BOOKREMOVALREASON_NOT_SET: {

                break;
            }*/

        }
        return singletonList(bookRemoved);
    }

    @Apply
    private void bookAdded(BookAdded event) {

        getBuilder().setBookId(event.getBookId())
                    .setBookDetails(event.getDetails());
    }
}
