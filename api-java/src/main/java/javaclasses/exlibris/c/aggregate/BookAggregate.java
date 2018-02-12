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
/*
 *
 * The aggregate managing the state of a {@link Book}.
 *
 * @author Alexander Karpets
 */

public class BookAggregate extends Aggregate<BookId, Book, BookVBuilder> {
    /*
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
     * Otherwise aggregate constructors (that are invoked by {@link }
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
        final UserId userId = cmd.getUserId();
        final BookDetails bookDetails = cmd.getBookDetails();

        final BookAdded result = BookAdded.newBuilder()
                                          .setBookId(bookId)
                                          .setLibrarianId(userId)
                                          .setDetails(bookDetails)
                                          .setWhenAdded(getCurrentTime())
                                          .build();

        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(UpdateBook cmd) {

        final BookId bookId = cmd.getBookId();
        final UserId userId = cmd.getUserId();

        final BookDetails bookDetails = cmd.getBookDetails();

        final BookUpdated result = BookUpdated.newBuilder()
                                              .setBookId(bookId)
                                              .setLibrarianId(userId)
                                              .setNewBookDetails(bookDetails)
                                              .setWhenUpdated(getCurrentTime())
                                              .build();

        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(RemoveBook cmd) {

        final BookId bookId = cmd.getBookId();
        final UserId userId = cmd.getUserId();

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
            } // TODO 12-Feb-2018[Dmytry Dyachenko]: write the rejection below
/*            case BOOKREMOVALREASON_NOT_SET: {

                break;
            }*/
            default: {
                break;
            }

        }
        return singletonList(bookRemoved);
    }

    @Apply
    private void bookAdded(BookAdded event) {

        final BookId bookId = event.getBookId();
        final BookDetails bookDetails = event.getDetails();

        getBuilder().setBookId(bookId)
                    .setBookDetails(bookDetails);
    }

  /*  @Apply
    private void bookUpdated(BookUpdated event) {

        final BookDetails newBookDetails = event.getNewBookDetails();

        getBuilder().setBookDetails(newBookDetails);
    }

    @Apply
    private void bookRemoved(BookRemoved event) {

        getBuilder().clearBookId()
                    .clearBookDetails();

    }*/
}
