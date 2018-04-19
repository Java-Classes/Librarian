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
import io.spine.net.EmailAddress;
import io.spine.net.Url;
import io.spine.people.PersonName;
import javaclasses.exlibris.AuthorName;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookSynopsis;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.Category;
import javaclasses.exlibris.Isbn;
import javaclasses.exlibris.Isbn62;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.BookRemoved;

import static io.spine.time.Time.getCurrentTime;

/**
 * A factory of the book events for the test needs.
 *
 * @author Yurii Haidamaka
 */
public class BookEventFactory {

    public static final Isbn ISBN = Isbn.newBuilder()
                                        .setValue("0201485672")
                                        .build();

    public static final Isbn62 ISBN_62 = Isbn62.newBuilder()
                                               .setValue("2mBSCRqZ")
                                               .build();

    public static final BookId BOOK_ID = BookId.newBuilder()
                                               .setIsbn62(ISBN_62)
                                               .build();
    public static final EmailAddress USER_EMAIL_ADRESS = EmailAddress.newBuilder()
                                                                     .setValue(
                                                                             "paulageyev@gmail.com")
                                                                     .build();
    public static final UserId USER_ID = UserId.newBuilder()
                                               .setEmail(USER_EMAIL_ADRESS)
                                               .build();

    public static final BookTitle TITLE = BookTitle.newBuilder()
                                                   .setTitle("Refactoring")
                                                   .build();

    public static final AuthorName AUTHOR = AuthorName.newBuilder()
                                                      .addAuthorName(
                                                              PersonName.newBuilder()
                                                                        .setFamilyName(
                                                                                "Fowler")
                                                                        .setGivenName(
                                                                                "Martin"))
                                                      .build();

    public static final Url COVER_URL = Url.newBuilder()
                                           .setRaw("http://library.teamdev.com/book/1")
                                           .build();

    public static final BookSynopsis SYNOPSIS = BookSynopsis.newBuilder()
                                                            .setBookSynopsis(
                                                                    "As the application of object " +
                                                                            "technology--particularly the Java programming language")
                                                            .build();

    public static final Category CATEGORY = Category.newBuilder()
                                                    .setValue("Programming")
                                                    .build();

    public static final BookDetails DETAILS = BookDetails.newBuilder()
                                                         .setIsbn(ISBN)
                                                         .setTitle(TITLE)
                                                         .setAuthor(AUTHOR)
                                                         .setBookCoverUrl(COVER_URL)
                                                         .setSynopsis(SYNOPSIS)
                                                         .addCategories(CATEGORY)
                                                         .build();

    private BookEventFactory() {
    }

    /**
     * Provides a pre-configured {@link BookAdded} event instance.
     *
     * @return the {@link BookAdded} instance
     */
    public static BookAdded bookAddedInstance() {
        return bookAddedInstance(BOOK_ID, DETAILS, USER_ID, getCurrentTime());
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
