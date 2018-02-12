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
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookSynopsis;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.Category;
import javaclasses.exlibris.Isbn62;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AddBook;

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

    private BookCommandFactory() {
    }

    public static AddBook createBookInstance() {
        return createBookInstance(bookId, userId, bookDetails);
    }

    public static AddBook createBookInstance(BookId bookId, UserId userId,
                                             BookDetails bookDetails) {

        final AddBook result = AddBook.newBuilder()
                                      .setBookId(bookId)
                                      .setUserId(userId)
                                      .setBookDetails(bookDetails)
                                      .build();
        return result;
    }

}
