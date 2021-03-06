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

package javaclasses.exlibris.testdata;

import com.google.protobuf.Timestamp;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookDetailsChange;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.BookRemoved;
import javaclasses.exlibris.c.BookUpdated;

import static io.spine.time.Time.getCurrentTime;
import static javaclasses.exlibris.testdata.TestValues.BOOK_DETAILS;
import static javaclasses.exlibris.testdata.TestValues.BOOK_ID;
import static javaclasses.exlibris.testdata.TestValues.DETAILS_CHANGE;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;

/**
 * A factory of the book events for the test needs.
 *
 * @author Yurii Haidamaka
 */
public class BookEventFactory {

    private BookEventFactory() {
    }

    /**
     * Provides a pre-configured {@link BookAdded} event instance.
     *
     * @return the {@link BookAdded} instance
     */
    public static BookAdded bookAddedInstance() {
        return bookAddedInstance(BOOK_ID, BOOK_DETAILS, USER_ID, getCurrentTime());
    }

    /**
     * Provides the {@link BookAdded} event by book ID, details, user ID and time.
     *
     * @param bookId    the identifier oif a book
     * @param details   full information about book
     * @param userId    the identifier of a user who added a book
     * @param whenAdded time when book was added
     * @return the {@code BookAdded} instance
     */
    public static BookAdded bookAddedInstance(BookId bookId, BookDetails details, UserId userId,
                                              Timestamp whenAdded) {
        final BookAdded result = BookAdded.newBuilder()
                                          .setBookId(bookId)
                                          .setDetails(details)
                                          .setLibrarianId(userId)
                                          .setWhenAdded(whenAdded)
                                          .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link BookUpdated} event instance.
     *
     * @return the {@link BookUpdated} instance
     */
    public static BookUpdated bookUpdatedInstance() {
        return bookUpdatedInstance(BOOK_ID, DETAILS_CHANGE, USER_ID, getCurrentTime());
    }

    /**
     * Provides the {@link BookUpdated} event by book ID, details change, user ID and time.
     *
     * @param bookId        the identifier oif a book
     * @param detailsChange previous and current book details
     * @param userId        the identifier of a user who added a book
     * @param whenUpdated   time when book was updated
     * @return the {@code BookUpdated} instance
     */
    public static BookUpdated bookUpdatedInstance(BookId bookId, BookDetailsChange detailsChange,
                                                  UserId userId,
                                                  Timestamp whenUpdated) {
        final BookUpdated result = BookUpdated.newBuilder()
                                              .setBookId(bookId)
                                              .setBookDetailsChange(detailsChange)
                                              .setLibrarianId(userId)
                                              .setWhenUpdated(whenUpdated)
                                              .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link BookRemoved} event instance.
     *
     * @return the {@link BookRemoved} instance
     */
    public static BookRemoved bookRemovedInstance() {
        return bookRemovedInstance(BOOK_ID, USER_ID, getCurrentTime());
    }

    /**
     * Provides the {@link BookRemoved} event by book ID, user ID and time.
     *
     * @param bookId      the identifier oif a book
     * @param userId      the identifier of a user who removed a book
     * @param whenRemoved time when book was removed
     * @return the {@code BookRemoved} instance
     */
    public static BookRemoved bookRemovedInstance(BookId bookId, UserId userId,
                                                  Timestamp whenRemoved) {
        final BookRemoved result = BookRemoved.newBuilder()
                                              .setBookId(bookId)
                                              .setLibrarianId(userId)
                                              .setWhenRemoved(whenRemoved)
                                              .setOutdated(true)
                                              .build();
        return result;
    }

}
