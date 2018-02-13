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

package io.spine.javaclasses.exlibris.testdata;

import io.spine.net.EmailAddress;
import io.spine.net.Url;
import io.spine.people.PersonName;
import javaclasses.exlibris.AuthorName;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookDetailsChange;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookSynopsis;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.Category;
import javaclasses.exlibris.Isbn62;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.RemoveBook;

import static io.spine.time.Time.getCurrentTime;
import static javaclasses.exlibris.c.RemoveBook.BookRemovalReasonCase.OUTDATED;
import javaclasses.exlibris.c.UpdateBook;

/**
 * @author Paul Ageyev
 */
public class BookCommandFactory {

    public static final BookId bookId = BookId.newBuilder()
                                              .setIsbn62(Isbn62.newBuilder()
                                                               .setValue("0201485672"))
                                              .build();
    public static final UserId userId = UserId.newBuilder()
                                              .setEmail(EmailAddress.newBuilder()
                                                                    .setValue(
                                                                            "paulageyev@gmail.com"))
                                              .build();

    public static final UserId librarianId = UserId.newBuilder()
                                                   .setEmail(EmailAddress.newBuilder()
                                                                         .setValue(
                                                                                 "smb@teamdev.com"))
                                                   .build();
    public static final BookDetails bookDetails = BookDetails.newBuilder()
                                                             .setTitle(BookTitle.newBuilder()
                                                                                .setTitle(
                                                                                        "Refactoring"))
                                                             .setAuthor(AuthorName.newBuilder()
                                                                                  .addAuthorName(
                                                                                          PersonName.newBuilder()
                                                                                                    .setFamilyName(
                                                                                                            "Martin Fowler")))
                                                             .setBookCoverUrl(Url.newBuilder()
                                                                                 .setRaw("http://library.teamdev.com/book/1"))
                                                             .setSynopsis(BookSynopsis.newBuilder()
                                                                                      .setBookSynopsis(
                                                                                              "As the application of object " +
                                                                                                      "technology--particularly the Java programming language"))
                                                             .addCategories(Category.newBuilder()
                                                                                    .setValue(
                                                                                            "Programming"))
                                                             .build();

    public static final BookDetails bookDetails2 = BookDetails.newBuilder()
                                                              .setTitle(BookTitle.newBuilder()
                                                                                 .setTitle(
                                                                                         "Refactoring: Improving the Design of Existing Code"))
                                                              .setAuthor(AuthorName.newBuilder()
                                                                                   .addAuthorName(
                                                                                           PersonName.newBuilder()
                                                                                                     .setFamilyName(
                                                                                                             "Martin Fowler")))
                                                              .setBookCoverUrl(Url.newBuilder()
                                                                                  .setRaw("http://library.teamdev.com/book/1"))
                                                              .setSynopsis(BookSynopsis.newBuilder()
                                                                                       .setBookSynopsis(
                                                                                               "As the application of object " +
                                                                                                       "technology--particularly the Java programming language"))
                                                              .addCategories(Category.newBuilder()
                                                                                     .setValue(
                                                                                             "Programming"))
                                                              .build();

    public static final String customReason = "The book was burned damaged";

    public static final RemoveBook.BookRemovalReasonCase removalReason = OUTDATED;

    private BookCommandFactory() {
    }

    public static AddBook createBookInstance() {
        return createBookInstance(bookId, userId, bookDetails);
    }

    public static AddBook createBookInstance(BookId bookId, UserId userId,
                                             BookDetails bookDetails) {

        final AddBook result = AddBook.newBuilder()
                                      .setBookId(bookId)
                                      .setLibrarianId(userId)
                                      .setBookDetails(bookDetails)
                                      .build();

        return result;
    }

    public static UpdateBook updateBookInstance(BookId bookId, UserId userId,
                                                BookDetailsChange bookDetails) {

        final UpdateBook result = UpdateBook.newBuilder()
                                            .setBookId(bookId)
                                            .setLibrarianId(userId)
                                            .setBookDetails(bookDetails)
                                            .build();

        return result;

    }

    public static RemoveBook removeBookInstance(BookId bookId, UserId librarianId,
                                                RemoveBook.BookRemovalReasonCase bookRemovalReasonCase) {

        final RemoveBook.Builder result = RemoveBook.newBuilder()
                                                    .setBookId(bookId)
                                                    .setLibrarianId(librarianId)
                                                    .setWhenRemoved(getCurrentTime());

        switch (bookRemovalReasonCase) {
            case OUTDATED: {
                return result
                        .setOutdated(true)
                        .build();
            }
            case CUSTOM_REASON: {
                return result
                        .setCustomReason(customReason)
                        .build();

            } // TODO 12-Feb-2018[Dmytry Dyachenko]: write the rejection below
            /* case BOOKREMOVALREASON_NOT_SET: {

                break;
            }*/
            default: {
                return result.build();
            }
        }

    }

}
