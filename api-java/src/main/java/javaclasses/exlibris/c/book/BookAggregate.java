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

import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import javaclasses.exlibris.Book;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookDetailsChange;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookVBuilder;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.BookRemoved;
import javaclasses.exlibris.c.BookUpdated;
import javaclasses.exlibris.c.RemoveBook;
import javaclasses.exlibris.c.UpdateBook;
import javaclasses.exlibris.c.rejection.BookAlreadyExists;
import javaclasses.exlibris.c.rejection.CannotRemoveMissingBook;
import javaclasses.exlibris.c.rejection.CannotUpdateMissingBook;

import static io.spine.time.Time.getCurrentTime;
import static io.spine.util.Exceptions.newIllegalArgumentException;
import static javaclasses.exlibris.c.book.BookAggregateRejections.bookAlreadyExists;
import static javaclasses.exlibris.c.book.BookAggregateRejections.cannotRemoveMissingBook;
import static javaclasses.exlibris.c.book.BookAggregateRejections.cannotUpdateMissingBook;

/**
 * The aggregate managing the state of a {@link Book}.
 *
 * @author Alexander Karpets
 * @author Paul Ageyev
 */
public class BookAggregate extends Aggregate<BookId, Book, BookVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id the identifier for the new aggregate
     */
    public BookAggregate(BookId id) {
        super(id);
    }

    /**
     * Handles a {@code AddBook} command.
     *
     * <p>For details see {@link AddBook}.
     *
     * @param cmd command with book parameters that necessary to add a book.
     * @return a {@code BookAdded} event.
     * @throws BookAlreadyExists if a book already exists.
     */
    @Assign
    BookAdded handle(AddBook cmd) throws BookAlreadyExists {
        final BookDetails bookDetails = cmd.getBookDetails();

        if (bookDetails.equals(getState().getBookDetails())) {
            throw bookAlreadyExists(cmd);
        }

        final BookId bookId = cmd.getBookId();
        final UserId userId = cmd.getLibrarianId();

        final BookAdded result = BookAdded.newBuilder()
                                          .setBookId(bookId)
                                          .setLibrarianId(userId)
                                          .setDetails(bookDetails)
                                          .setWhenAdded(getCurrentTime())
                                          .build();
        return result;
    }

    /**
     * Handles a {@code UpdateBook} command.
     *
     * <p>For details see {@link UpdateBook}.
     *
     * @param cmd command with book details that a librarian is going to change.
     * @return a {@code BookUpdated} event.
     * @throws CannotUpdateMissingBook if a book is missing.
     */
    @Assign
    BookUpdated handle(UpdateBook cmd) throws CannotUpdateMissingBook {
        final BookId bookId = cmd.getBookId();

        if (!bookId.equals(getState().getBookId())) {
            throw cannotUpdateMissingBook(cmd);
        }

        final UserId librarianId = cmd.getLibrarianId();
        final BookDetailsChange bookDetails = cmd.getBookDetails();

        final BookUpdated result = BookUpdated.newBuilder()
                                              .setBookId(bookId)
                                              .setLibrarianId(librarianId)
                                              .setBookDetailsChange(bookDetails)
                                              .setWhenUpdated(getCurrentTime())
                                              .build();
        return result;
    }

    /**
     * Handles a {@code RemoveBook} command.
     *
     * <p>For details see {@link RemoveBook}.
     *
     * @param cmd command with a removal reason.
     * @return a {@code BookRemoved} event.
     * @throws CannotRemoveMissingBook if a book is missing.
     */
    @SuppressWarnings("all") /*Cause of codacy needs a default switch statement
                             which will never be called*/
    @Assign
    BookRemoved handle(RemoveBook cmd) throws CannotRemoveMissingBook {
        if (!getState().hasBookDetails()) {
            throw cannotRemoveMissingBook(cmd);
        }

        final BookId bookId = cmd.getBookId();
        final UserId librarianId = cmd.getLibrarianId();
        final String customReason = cmd.getCustomReason();

        final BookRemoved.Builder bookRemoved = BookRemoved.newBuilder()
                                                           .setBookId(bookId)
                                                           .setLibrarianId(librarianId)
                                                           .setWhenRemoved(getCurrentTime());

        final RemoveBook.BookRemovalReasonCase removalReasonCase = cmd.getBookRemovalReasonCase();
        switch (removalReasonCase) {
            case OUTDATED: {
                bookRemoved.setOutdated(true);
                break;
            }
            case CUSTOM_REASON: {
                bookRemoved.setCustomReason(customReason);
                break;
            }
            case BOOKREMOVALREASON_NOT_SET: {
                throw newIllegalArgumentException("The book cannot be removed without reason.",
                                                  removalReasonCase);
            }
        }
        return bookRemoved.build();
    }

    /**
     * Handles a {@code BookAdded} event.
     *
     * <p>For details see {@link BookAdded}.
     *
     * @param event a {@code BookAdded} event message.
     */
    @Apply
    void bookAdded(BookAdded event) {
        final BookId bookId = event.getBookId();
        final BookDetails bookDetails = event.getDetails();

        getBuilder().setBookId(bookId)
                    .setBookDetails(bookDetails);
    }

    /**
     * Handles a {@code BookUpdated} event.
     *
     * <p>For details see {@link BookUpdated}.
     *
     * @param event a {@code BookUpdated} event message.
     */
    @Apply
    void bookUpdated(BookUpdated event) {
        final BookId bookId = event.getBookId();
        final BookDetailsChange bookDetails = event.getBookDetailsChange();

        getBuilder().setBookId(bookId)
                    .setBookDetails(bookDetails.getNewBookDetails());
    }

    /**
     * Handles a {@code BookRemoved} event.
     *
     * <p>For details see {@link BookRemoved}.
     *
     * @param event a {@code BookRemoved} event message.
     */
    @Apply
    void bookRemoved(BookRemoved event) {
        getBuilder().clearBookId()
                    .clearBookDetails();
    }
}

