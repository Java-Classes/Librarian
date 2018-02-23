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
import javaclasses.exlibris.c.UpdateBook;

import static io.spine.time.Time.getCurrentTime;
import static io.spine.util.Exceptions.newIllegalArgumentException;
import static javaclasses.exlibris.c.RemoveBook.BookRemovalReasonCase.CUSTOM_REASON;
import static javaclasses.exlibris.c.RemoveBook.BookRemovalReasonCase.OUTDATED;

/**
 * A factory of the task commands for the test needs.
 *
 * @author Paul Ageyev
 */
public class BookCommandFactory {

    public static final Isbn62 isbn62Value = Isbn62.newBuilder()
                                                   .setValue("0201485672")
                                                   .build();

    private static final Isbn62 isbn62Value2 = Isbn62.newBuilder()
                                                     .setValue("19411945")
                                                     .build();

    public static final EmailAddress userEmailAddress1 = EmailAddress.newBuilder()
                                                                     .setValue(
                                                                             "paulageyev@gmail.com")
                                                                     .build();

    private static final EmailAddress userEmailAddress2 = EmailAddress.newBuilder()
                                                                      .setValue(
                                                                              "petrVase4kin@gmail.com")
                                                                      .build();

    private static final EmailAddress librarianEmailAddress1 = EmailAddress.newBuilder()
                                                                           .setValue(
                                                                                   "smb@teamdev.com")
                                                                           .build();

    private static final EmailAddress librarianEmailAddress2 = EmailAddress.newBuilder()
                                                                           .setValue(
                                                                                   "Inn4ka@teamdev.com")
                                                                           .build();

    public static final BookTitle bookTitle = BookTitle.newBuilder()
                                                       .setTitle("Refactoring")
                                                       .build();

    private static final AuthorName authorName = AuthorName.newBuilder()
                                                           .addAuthorName(
                                                                   PersonName.newBuilder()
                                                                             .setFamilyName(
                                                                                     "Fowler")
                                                                             .setGivenName(
                                                                                     "Martin"))
                                                           .build();

    private static final Url bookCoverUrl = Url.newBuilder()
                                               .setRaw("http://library.teamdev.com/book/1")
                                               .build();

    private static final BookSynopsis bookSynopsis = BookSynopsis.newBuilder()
                                                                 .setBookSynopsis(
                                                                         "As the application of object " +
                                                                                 "technology--particularly the Java programming language")
                                                                 .build();

    private static final Category bookCategory = Category.newBuilder()
                                                         .setValue("Programming")
                                                         .build();
    public static final BookTitle bookTitle2 = BookTitle.newBuilder()
                                                        .setTitle("WHY SOFTWARE SUX")
                                                        .build();

    private static final AuthorName authorName2 = AuthorName.newBuilder()
                                                            .addAuthorName(
                                                                    PersonName.newBuilder()
                                                                              .setFamilyName(
                                                                                      "Cartman")
                                                                              .setGivenName(
                                                                                      "Eric"))
                                                            .build();

    private static final Url bookCoverUrl2 = Url.newBuilder()
                                                .setRaw("http://library.teamdev.com/book/144")
                                                .build();

    private static final BookSynopsis bookSynopsis2 = BookSynopsis.newBuilder()
                                                                  .setBookSynopsis(
                                                                          "Some info about software and why it sucks ")
                                                                  .build();

    private static final Category bookCategory2 = Category.newBuilder()
                                                          .setValue("Architecture")
                                                          .build();

    public static final BookId bookId = BookId.newBuilder()
                                              .setIsbn62(isbn62Value)
                                              .build();

    public static final BookId bookId2 = BookId.newBuilder()
                                               .setIsbn62(isbn62Value2)
                                               .build();

    public static final UserId userId = UserId.newBuilder()
                                              .setEmail(userEmailAddress1)
                                              .build();

    public static final UserId userId2 = UserId.newBuilder()
                                               .setEmail(userEmailAddress2)
                                               .build();

    public static final UserId librarianId = UserId.newBuilder()
                                                   .setEmail(librarianEmailAddress1)
                                                   .build();

    public static final UserId librarianId2 = UserId.newBuilder()
                                                    .setEmail(librarianEmailAddress2)
                                                    .build();

    public static final BookDetails bookDetails = BookDetails.newBuilder()
                                                             .setTitle(bookTitle)
                                                             .setAuthor(authorName)
                                                             .setBookCoverUrl(bookCoverUrl)
                                                             .setSynopsis(bookSynopsis)
                                                             .addCategories(bookCategory)
                                                             .build();

    public static final BookDetails bookDetails2 = BookDetails.newBuilder()
                                                              .setTitle(bookTitle2)
                                                              .setAuthor(authorName2)
                                                              .setBookCoverUrl(bookCoverUrl2)
                                                              .setSynopsis(bookSynopsis2)
                                                              .addCategories(bookCategory2)
                                                              .build();

    private static final String customReason = "The book was burned damaged";

    public static final RemoveBook.BookRemovalReasonCase removalOutdatedReason = OUTDATED;
    public static final RemoveBook.BookRemovalReasonCase removalCustomReason = CUSTOM_REASON;

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
                                                RemoveBook.BookRemovalReasonCase removalReasonCase) {

        final RemoveBook.Builder result = RemoveBook.newBuilder()
                                                    .setBookId(bookId)
                                                    .setLibrarianId(librarianId)
                                                    .setWhenRemoved(getCurrentTime());

        switch (removalReasonCase) {
            case OUTDATED: {
                result.setOutdated(true);
                break;
            }
            case CUSTOM_REASON: {
                result.setCustomReason(customReason);
                break;

            }
            case BOOKREMOVALREASON_NOT_SET: {
                throw newIllegalArgumentException("The book cannot be removed without reason.",
                                                  removalReasonCase);
            }
        }
        return result.build();
    }
}
